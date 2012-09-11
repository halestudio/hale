/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.views.data.internal.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.align.transformation.service.impl.DefaultInstanceSink;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceMetadata;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;
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
			typesCombo.setLabelProvider(new DefinitionLabelProvider());
			typesCombo.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateSelection();
				}

			});
			typesCombo.getControl().setLayoutData(
					new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

			updateFeatureTypesSelection();

			// service listeners
			InstanceSampleService rss = (InstanceSampleService) PlatformUI.getWorkbench()
					.getService(InstanceSampleService.class);
			rss.addObserver(referenceListener = new Observer() {

				@Override
				public void update(Observable arg0, Object arg1) {
					updateInDisplayThread();
				}
			});

			AlignmentService alService = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
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
				public void cellReplaced(Cell oldCell, Cell newCell) {
					updateInDisplayThread();
				}

				@Override
				public void cellsAdded(Iterable<Cell> cells) {
					updateInDisplayThread();
				}
			});
		}

		private void updateInDisplayThread() {
			if (Display.getCurrent() != null) {
				updateFeatureTypesSelection();
			}
			else {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						updateFeatureTypesSelection();
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
						final InstanceSampleService rss = (InstanceSampleService) PlatformUI
								.getWorkbench().getService(InstanceSampleService.class);
						final AlignmentService alService = (AlignmentService) PlatformUI
								.getWorkbench().getService(AlignmentService.class);
						final TransformationService cst = OsgiUtils
								.getService(TransformationService.class);

						// get reference instances
						Collection<Instance> reference = rss.getReferenceInstances();

						if (reference != null && !reference.isEmpty()) {
							// create an instance collection
							InstanceCollection instances = new DefaultInstanceCollection(reference);

							DefaultInstanceSink target = new DefaultInstanceSink();

							// transform features
							TransformationReport report = cst.transform(alService.getAlignment(), // Alignment
									instances, target, new ProgressMonitorIndicator(monitor));

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
										if (InstanceMetadata.getID(inst).equals(
												InstanceMetadata.getSourceID(instance))) {
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
			InstanceSampleService rss = (InstanceSampleService) PlatformUI.getWorkbench()
					.getService(InstanceSampleService.class);
			rss.deleteObserver(referenceListener);

			AlignmentService alService = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
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
