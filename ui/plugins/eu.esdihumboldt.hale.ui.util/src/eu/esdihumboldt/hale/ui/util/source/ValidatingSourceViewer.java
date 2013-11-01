/*
 * Copyright (c) 2013 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.util.source;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Source viewer that validates its content on document changes asynchronously
 * in a Job.
 * 
 * @author Simon Templer
 */
public class ValidatingSourceViewer extends SourceViewer {

	private static class ExclusiveSchedulingRule implements ISchedulingRule {

		private final Object owner;

		/**
		 * Create a rule for scheduling Jobs exclusively if they have the same
		 * owner.
		 * 
		 * @param owner the rule owner
		 */
		public ExclusiveSchedulingRule(Object owner) {
			super();
			this.owner = owner;
		}

		@Override
		public boolean contains(ISchedulingRule rule) {
			return rule instanceof ExclusiveSchedulingRule
					&& owner == ((ExclusiveSchedulingRule) rule).owner;
		}

		@Override
		public boolean isConflicting(ISchedulingRule rule) {
			return contains(rule);
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(ValidatingSourceViewer.class);

	private final AtomicBoolean validationEnabled = new AtomicBoolean(true);

	private final ReentrantLock changeLock = new ReentrantLock();

	/**
	 * If the document has changed since validation. Protected by lock.
	 */
	private boolean changed = true;

	/**
	 * If the document was valid in the last validation run.
	 */
	private boolean valid = false;

	private IDocumentListener documentListener;

	private IDocument lastDocument;

	private Job validateJob;

	/**
	 * Name of the property holding the state if the viewer's document is valid.
	 */
	public static final String PROPERTY_VALID = "valid";

	private final Set<IPropertyChangeListener> propertyChangeListeners = new CopyOnWriteArraySet<>();

	/**
	 * @see SourceViewer#SourceViewer(Composite, IVerticalRuler, int)
	 */
	public ValidatingSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		super(parent, ruler, styles);

		init();
	}

	/**
	 * @see SourceViewer#SourceViewer(Composite, IVerticalRuler, IOverviewRuler,
	 *      boolean, int)
	 */
	public ValidatingSourceViewer(Composite parent, IVerticalRuler verticalRuler,
			IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);

		init();
	}

	/**
	 * Validate the document content. The default implementation always returns
	 * <code>true</code>.
	 * 
	 * @param content the document content
	 * @return if the content is valid
	 */
	protected boolean validate(String content) {
		return true;
	}

	/**
	 * Initialize the Job and listener.
	 */
	private void init() {
		validateJob = new Job("Source viewer validation") {

			@Override
			public boolean shouldRun() {
				return validationEnabled.get();
			}

			@Override
			public boolean shouldSchedule() {
				return validationEnabled.get();
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				String content;
				changeLock.lock();
				try {
					if (!changed) {
						return Status.OK_STATUS;
					}
					content = getDocument().get();
					changed = false;
				} finally {
					changeLock.unlock();
				}

				boolean success = false;
				try {
					// this is the potentially long running stuff
					success = validate(content);
				} catch (Exception e) {
					// ignore, but log
					log.warn("Error validating document content", e);
					success = false;
				}

				boolean notify = false;
				changeLock.lock();
				try {
					/*
					 * Only notify listeners if the document was not changed in
					 * the meantime and the valid state is different than
					 * before.
					 */
					notify = !changed && valid != success;
					if (notify) {
						// set result
						valid = success;
					}
				} finally {
					changeLock.unlock();
				}

				if (notify) {
					for (IPropertyChangeListener listener : propertyChangeListeners) {
						try {
							PropertyChangeEvent event = new PropertyChangeEvent(
									ValidatingSourceViewer.this, PROPERTY_VALID, !success, success);
							listener.propertyChange(event);
						} catch (Exception e) {
							log.error("Error notifying listener on property change", e);
						}
					}
				}

				return Status.OK_STATUS;
			}
		};
		validateJob.setUser(false);
		validateJob.setRule(new ExclusiveSchedulingRule(this));

		documentListener = new IDocumentListener() {

			@Override
			public void documentChanged(DocumentEvent event) {
				changeLock.lock();
				try {
					changed = true;
				} finally {
					changeLock.unlock();
				}

				// schedule validation
				validateJob.schedule(100);
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				// do nothing
			}
		};
	}

	/**
	 * Force new validation.
	 */
	public void forceValidation() {
		documentListener.documentChanged(null);
	}

	/**
	 * Get the result of the last validation.
	 * 
	 * @return the validation
	 */
	public boolean isValid() {
		changeLock.lock();
		try {
			return valid;
		} finally {
			changeLock.unlock();
		}
	}

	@Override
	public void setDocument(IDocument document) {
		super.setDocument(document);

		updateListener(document);
	}

	private void updateListener(IDocument document) {
		if (lastDocument != document) {
			if (lastDocument != null) {
				lastDocument.removeDocumentListener(documentListener);
			}

			if (document != null) {
				document.addDocumentListener(documentListener);
			}
			lastDocument = document;

			if (document != null) {
				// initial validation
				validateJob.schedule(100);
			}
		}
	}

	@Override
	public void setDocument(IDocument document, int visibleRegionOffset, int visibleRegionLength) {
		super.setDocument(document, visibleRegionOffset, visibleRegionLength);

		updateListener(document);
	}

	@Override
	public void setDocument(IDocument document, IAnnotationModel annotationModel) {
		super.setDocument(document, annotationModel);

		updateListener(document);
	}

	@Override
	public void setDocument(IDocument document, IAnnotationModel annotationModel,
			int modelRangeOffset, int modelRangeLength) {
		super.setDocument(document, annotationModel, modelRangeOffset, modelRangeLength);

		updateListener(document);
	}

	/**
	 * Add a property change listener. It will be notified on changes to the
	 * {@value #PROPERTY_VALID} property.
	 * 
	 * @param listener the listener to add
	 */
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		propertyChangeListeners.add(listener);
	}

	/**
	 * Remove a property change listener.
	 * 
	 * @param listener the listener to add
	 */
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		propertyChangeListeners.remove(listener);
	}

}
