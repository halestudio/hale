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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.ui.util.jobs.ExclusiveSchedulingRule;

/**
 * Source viewer that independent of validation additionally does a compilation
 * of the content, e.g. for use with content assist.
 * 
 * @param <C> the type of the compilation result
 * 
 * @author Simon Templer
 */
public class CompilingSourceViewer<C> extends ValidatingSourceViewer {

	private static final ALogger log = ALoggerFactory.getLogger(CompilingSourceViewer.class);

	/**
	 * Name of the property holding the compilation result.
	 */
	public static final String PROPERTY_COMPILED = "compiled";

	/**
	 * The compile job.
	 */
	private Job compileJob;

	/**
	 * Compilation scheduling delay in milliseconds.
	 */
	private static final int COMPILE_DELAY = 100;

	private final boolean compilationEnabled;

	private final ReentrantLock changeLock = new ReentrantLock();

	/**
	 * If the document has changed since compilation. Protected by lock.
	 */
	private boolean changed = true;

	/**
	 * Set of futures to update. Protected by lock.
	 */
	private final Set<SettableFuture<C>> toUpdate = new HashSet<>();

	/**
	 * The compilation result. Protected by lock.
	 */
	private C compiled;

	private final SourceCompiler<C> compiler;

	/**
	 * Constructs a new validating source viewer.
	 * 
	 * @param parent the parent of the viewer's control
	 * @param ruler the vertical ruler used by this source viewer
	 * @param styles the SWT style bits for the viewer's control
	 * @param validator the source validator
	 * @param compiler the source compiler, <code>null</code> to disable
	 *            compilation
	 */
	public CompilingSourceViewer(Composite parent, IVerticalRuler ruler, int styles,
			SourceValidator validator, SourceCompiler<C> compiler) {
		super(parent, ruler, styles, validator);
		this.compiler = compiler;
		this.compilationEnabled = compiler != null;
	}

	/**
	 * Constructs a new validating source viewer.
	 * 
	 * @param parent the parent of the viewer's control
	 * @param verticalRuler the vertical ruler used by this source viewer
	 * @param overviewRuler the overview ruler
	 * @param showAnnotationsOverview <code>true</code> if the overview ruler
	 *            should be visible, <code>false</code> otherwise
	 * @param styles the SWT style bits for the viewer's control
	 * @param validator the source validator
	 * @param compiler the source compiler, <code>null</code> to disable
	 *            compilation
	 */
	public CompilingSourceViewer(Composite parent, IVerticalRuler verticalRuler,
			IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles,
			SourceValidator validator, SourceCompiler<C> compiler) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles, validator);
		this.compiler = compiler;
		this.compilationEnabled = compiler != null;
	}

	/**
	 * Get the compilation result.
	 * 
	 * @return a future reference to the compilation result, the actual
	 *         compilation result may be <code>null</code> if the compilation
	 *         failed
	 */
	public ListenableFuture<C> getCompiled() {
		SettableFuture<C> result = SettableFuture.create();
		if (!compilationEnabled) {
			result.set(null);
			return result;
		}

		changeLock.lock();
		try {
			if (changed) {
				// compilation result is not up-to-date
				toUpdate.add(result);
			}
			else {
				result.set(compiled);
			}
		} finally {
			changeLock.unlock();
		}

		return result;
	}

	@Override
	protected void init() {
		compileJob = new Job("Compile") {

			@Override
			public boolean shouldRun() {
				return compilationEnabled;
			}

			@Override
			public boolean shouldSchedule() {
				return compilationEnabled;
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				String content;
				changeLock.lock();
				try {
					if (!changed) {
						return Status.OK_STATUS;
					}
					IDocument doc = getDocument();
					if (doc != null) {
						content = doc.get();
					}
					else {
						content = null;
					}
					changed = false;
				} finally {
					changeLock.unlock();
				}

				C result = null;
				if (content != null) {
					try {
						// this is the potentially long running stuff
						result = compile(content);
					} catch (Exception e) {
						// ignore, but log
						log.warn("Error compiling document content", e);
					}
				}

				boolean notify = false;
				C previous = null;
				changeLock.lock();
				try {
					/*
					 * Only notify listeners if the document was not changed in
					 * the meantime.
					 */
					notify = !changed;
					if (notify) {
						// set result
						previous = compiled;
						compiled = result;

						// set result for futures
						for (SettableFuture<C> future : toUpdate) {
							future.set(result);
						}
						toUpdate.clear();
					}
				} finally {
					changeLock.unlock();
				}

				if (notify) {
					// notify listeners
					PropertyChangeEvent event = new PropertyChangeEvent(CompilingSourceViewer.this,
							PROPERTY_COMPILED, previous, result);
					notifyOnPropertyChange(event);
				}

				return Status.OK_STATUS;
			}
		};
		compileJob.setSystem(true);
		compileJob.setRule(new ExclusiveSchedulingRule(compileJob));

		super.init();
	}

	/**
	 * Do the compilation.
	 * 
	 * @param content the document content
	 * @return the compilation result
	 */
	protected final C compile(String content) {
		return compiler.compile(content);
	}

	@Override
	protected void scheduleValidation() {
		if (compilationEnabled) {
			changeLock.lock();
			try {
				changed = true;
			} finally {
				changeLock.unlock();
			}

			// schedule compilation
			compileJob.schedule(COMPILE_DELAY);
		}

		super.scheduleValidation();
	}

}
