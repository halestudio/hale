/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.data.internal.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.align.transformation.service.impl.DefaultInstanceSink;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceMetadata;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.sample.InstanceSampleService;

/**
 * Instance selector based on a transformation sample
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SampleTransformInstanceSelector implements InstanceSelector {

	/**
	 * Instance selector control
	 */
	private class InstanceSelectorControl extends Composite {

		private final ComboViewer typesCombo;

		private final ListMultimap<TypeDefinition, Instance> instanceMap = ArrayListMultimap
				.create();

		private TypeDefinition selectedType;

		private final Observer referenceListener;

		private AlignmentServiceAdapter alignmentListener;

		/**
		 * @see Composite#Composite(Composite, int)
		 */
		public InstanceSelectorControl(Composite parent, int style) {
			super(parent, style);

			setLayout(new GridLayout(1, false));

			// feature type selector
			typesCombo = new ComboViewer(this, SWT.READ_ONLY);
			typesCombo.setContentProvider(ArrayContentProvider.getInstance());
			typesCombo.setComparator(new DefinitionComparator());
			typesCombo.setLabelProvider(new DefinitionLabelProvider(null));
			typesCombo.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateSelection();
				}

			});
			typesCombo.getControl()
					.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

			updateFeatureTypesSelection();

			// service listeners
			InstanceSampleService rss = PlatformUI.getWorkbench()
					.getService(InstanceSampleService.class);
			rss.addObserver(referenceListener = new Observer() {

				@Override
				public void update(Observable arg0, Object arg1) {
					updateInDisplayThread();
				}
			});

			AlignmentService alService = PlatformUI.getWorkbench()
					.getService(AlignmentService.class);
			alService.addListener(alignmentListener = new AlignmentServiceAdapter() {

				@Override
				public void alignmentCleared() {
					updateInDisplayThread();
				}

				@Override
				public void cellsRemoved(Iterable<Cell> cells) {
					updateInDisplayThread();
				}

				@Override
				public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
					updateInDisplayThread();
				}

				@Override
				public void cellsAdded(Iterable<Cell> cells) {
					updateInDisplayThread();
				}

				@Override
				public void alignmentChanged() {
					updateInDisplayThread();
				}

				@Override
				public void customFunctionsChanged() {
					updateInDisplayThread();
				}

				@Override
				public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
					// property changes may affect transformation result
					updateInDisplayThread();
				}
			});
		}

		private void updateInDisplayThread() {
			if (Display.getCurrent() != null) {
				preSelectionChanged();
				updateFeatureTypesSelection();
				postSelectionChanged();
			}
			else {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						preSelectionChanged();
						updateFeatureTypesSelection();
						postSelectionChanged();
					}
				});
			}
		}

		/**
		 * Update the feature types selection
		 */
		protected void updateFeatureTypesSelection() {
			instanceMap.clear();

			final AtomicBoolean finished = new AtomicBoolean(false);
			Job job = new Job("Transform samples") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						final InstanceSampleService rss = PlatformUI.getWorkbench()
								.getService(InstanceSampleService.class);
						final AlignmentService alService = PlatformUI.getWorkbench()
								.getService(AlignmentService.class);
						final TransformationService cst = HalePlatform
								.getService(TransformationService.class);

						// get reference instances
						Collection<Instance> reference = rss.getReferenceInstances();

						if (reference != null && !reference.isEmpty()) {
							// create an instance collection
							InstanceCollection instances = new DefaultInstanceCollection(reference);

							DefaultInstanceSink target = new DefaultInstanceSink();

							// transform features
							TransformationReport report = cst.transform(alService.getAlignment(), // Alignment
									instances, target, HaleUI.getServiceProvider(),
									new ProgressMonitorIndicator(monitor));

							if (!report.isSuccess()) {
								// TODO log message
							}

							// Sort target instances by comparing meta data IDs
							// of the source
							// instances with the SourcesIDs of the target
							// instances
							ArrayList<Instance> targetSorted = new ArrayList<Instance>();
							ResourceIterator<Instance> it = instances.iterator();
							try {
								while (it.hasNext()) {
									Instance inst = it.next();
									for (Instance instance : target.getInstances()) {
										if (InstanceMetadata.getID(inst)
												.equals(InstanceMetadata.getSourceID(instance))) {
											targetSorted.add(instance);
										}
									}
								}
							} finally {
								it.close();
							}

							// check if there are target instances without a
							// matched source id
							for (Instance instance : target.getInstances()) {
								if (!targetSorted.contains(instance)) {
									targetSorted.add(instance);
								}
							}

							// determine types
							for (Instance instance : targetSorted) {
								instanceMap.put(instance.getDefinition(), instance);
							}
						}

						return Status.OK_STATUS;
					} finally {
						finished.set(true);
					}
				}
			};

			job.schedule();
			if (Display.getCurrent() != null) {
				while (!finished.get()) {
					if (!Display.getCurrent().readAndDispatch()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// ignore
						}
					}
				}
			}
			try {
				job.join();
			} catch (InterruptedException e) {
				// ignore
			}

			Collection<TypeDefinition> selectableTypes = instanceMap.keySet();
			typesCombo.setInput(selectableTypes);

			if (!selectableTypes.isEmpty()) {
				typesCombo.setSelection(new StructuredSelection(selectableTypes.iterator().next()));
				typesCombo.getControl().setEnabled(true);
			}
			else {
				typesCombo.getControl().setEnabled(false);
			}

			layout(true, true);

			updateSelection();
		}

		/**
		 * Update the selection
		 */
		protected void updateSelection() {
			if (!typesCombo.getSelection().isEmpty()) {
				TypeDefinition featureType = (TypeDefinition) ((IStructuredSelection) typesCombo
						.getSelection()).getFirstElement();

				selectedType = featureType;
			}
			else {
				selectedType = null;
			}

			for (InstanceSelectionListener listener : listeners) {
				listener.selectionChanged(selectedType, getSelection());
			}
		}

		/**
		 * calling pre selection change in listener
		 */
		public void preSelectionChanged() {
			// disable types combo box before any changes apply to transformed
			// data view,
			// enable it again after selection change completed and if combo box
			// has entries
			typesCombo.getControl().setEnabled(false);

			for (InstanceSelectionListener listener : listeners) {
				listener.preSelectionChange();
			}
		}

		/**
		 * calling post selection change in listener
		 */
		public void postSelectionChanged() {
			for (InstanceSelectionListener listener : listeners) {
				listener.postSelectionChange();
			}
		}

		/**
		 * Get the currently selected features
		 * 
		 * @return the currently selected features
		 */
		public Iterable<Instance> getSelection() {
			if (selectedType == null) {
				return null;
			}
			else {
				return instanceMap.get(selectedType);
			}
		}

		/**
		 * @see Widget#dispose()
		 */
		@Override
		public void dispose() {
			InstanceSampleService rss = PlatformUI.getWorkbench()
					.getService(InstanceSampleService.class);
			rss.deleteObserver(referenceListener);

			AlignmentService alService = PlatformUI.getWorkbench()
					.getService(AlignmentService.class);
			alService.removeListener(alignmentListener);

			listeners.clear();

			super.dispose();
		}

	}

	private final Set<InstanceSelectionListener> listeners = new HashSet<InstanceSelectionListener>();

	private InstanceSelectorControl current;

	/**
	 * @see InstanceSelector#addSelectionListener(InstanceSelectionListener)
	 */
	@Override
	public void addSelectionListener(InstanceSelectionListener listener) {
		listeners.add(listener);

		if (current != null && !current.isDisposed()) {
			listener.selectionChanged(current.selectedType, current.getSelection());
		}
	}

	/**
	 * @see InstanceSelector#createControl(Composite)
	 */
	@Override
	public Control createControl(Composite parent) {
		current = new InstanceSelectorControl(parent, SWT.NONE);
		return current;
	}

	/**
	 * @see InstanceSelector#removeSelectionListener(InstanceSelectionListener)
	 */
	@Override
	public void removeSelectionListener(InstanceSelectionListener listener) {
		listeners.remove(listener);
	}

}
