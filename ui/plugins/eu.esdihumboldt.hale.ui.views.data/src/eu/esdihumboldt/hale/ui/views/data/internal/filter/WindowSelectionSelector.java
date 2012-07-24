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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.util.selection.SelectionTrackerUtil;
import eu.esdihumboldt.hale.ui.views.data.AbstractDataView;

/**
 * Instance selector retrieving values from the selection service.
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class WindowSelectionSelector implements InstanceSelector {
	
	/**
	 * Instance selector control
	 */
	private class InstanceSelectorControl extends Composite implements ISelectionListener {
		private final ComboViewer instanceTypes;
		private final Map<TypeDefinition, List<Instance>> instanceMap = new HashMap<TypeDefinition, List<Instance>>();
		private TypeDefinition selectedType;

		/**
		 * @see Composite#Composite(Composite, int)
		 */
		public InstanceSelectorControl(Composite parent, int style) {
			super(parent, style);
			
			setLayout(new GridLayout(1, false));
			
			// instance type selector
			instanceTypes = new ComboViewer(this, SWT.READ_ONLY);
			instanceTypes.setContentProvider(ArrayContentProvider.getInstance());
			instanceTypes.setComparator(new ViewerComparator() {

				@Override
				public int compare(Viewer viewer, Object e1, Object e2) {
					if (e1 instanceof FeatureType && e2 instanceof FeatureType) {
						return ((FeatureType) e1).getName().getLocalPart().compareTo(
								((FeatureType) e2).getName().getLocalPart());
					}
					return super.compare(viewer, e1, e2);
				}
				
			});
			instanceTypes.setLabelProvider(new DefinitionLabelProvider());
			instanceTypes.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateSelection();
				}
				
			});
			instanceTypes.getControl().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
					
			// service listeners
			ISelectionService ss = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
			ss.addPostSelectionListener(this);
			
			ISelection selection = ss.getSelection();

			if (!(selection instanceof InstanceSelection))
				selection = SelectionTrackerUtil.getTracker().getSelection(InstanceSelection.class);

			if (selection != null)
				lastSelection = (InstanceSelection) selection;
			
			updateTypeSelection();
		}
		
		/**
		 * Update the feature types selection
		 */
		protected void updateTypeSelection() {
			instanceMap.clear();
			
			// get selected instance
			Collection<Instance> instances = getSelectedInstances(null);
			
			if (instances != null && !instances.isEmpty()) {
				// determine types
				Iterator<Instance> it = instances.iterator();
				while (it.hasNext()) {
					Instance instance = it.next();
					TypeDefinition type = instance.getDefinition();
					List<Instance> instanceList = instanceMap.get(type);
					if (instanceList == null) {
						instanceList = new ArrayList<Instance>();
						instanceMap.put(type, instanceList);
					}
					instanceList.add(instance);
				}
			}
			
			Set<TypeDefinition> selectableTypes = instanceMap.keySet();
			
			instanceTypes.setInput(selectableTypes);
			
			if (!selectableTypes.isEmpty()) {
				instanceTypes.setSelection(new StructuredSelection(selectableTypes.iterator().next()));
				instanceTypes.getControl().setEnabled(true);
			}
			else {
				instanceTypes.getControl().setEnabled(false);
			}
			
			layout(true, true);
			
			updateSelection();
		}

		/**
		 * Get the selected instances form the current {@link InstanceSelection}
		 * @param typeFilter the type for the instances to match, 
		 *   <code>null</code> for any type
		 * @return the selected features or <code>null</code>
		 */
		private Collection<Instance> getSelectedInstances(TypeDefinition typeFilter) {
			if (lastSelection == null || lastSelection.isEmpty()) {
				return null;
			}
			else {
				List<?> elements = lastSelection.toList();
				Collection<Instance> result = new ArrayList<Instance>(elements.size());
				
				final InstanceService instanceService = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
				
				// collect instances from selection that match the data set
				for (Object element : elements) {
					Instance instance = null;
					if (element instanceof Instance
							&& ((Instance) element).getDataSet() == dataSet) {
						instance = (Instance) element;
					}
					else if (element instanceof InstanceReference 
							&& ((InstanceReference) element).getDataSet() == dataSet) {
						instance = instanceService.getInstance((InstanceReference) element);
					}
					
					if (instance != null 
							&& (typeFilter == null 
							|| typeFilter.equals(instance.getDefinition()))) {
						// add instance if type filter matches
						result.add(instance);
					}
				}
				
				return result;
			}
		}

		/**
		 * Update the selection.
		 */
		protected void updateSelection() {
			if (!instanceTypes.getSelection().isEmpty()) {
				TypeDefinition type = (TypeDefinition) ((IStructuredSelection) instanceTypes.getSelection()).getFirstElement();
				
				selectedType = type;
			}
			else {
				selectedType = null;
			}
			
			for (InstanceSelectionListener listener : listeners) {
				listener.selectionChanged(selectedType, getSelection());
			}
		}
		
		/**
		 * Get the currently selected instances.
		 * @return the currently selected instances
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
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().removePostSelectionListener(this);
			
			listeners.clear();
			
			super.dispose();
		}

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (!(part instanceof AbstractDataView) // don't react on data view changes (to prevent loops)
					&& selection instanceof InstanceSelection) {
				lastSelection = (InstanceSelection) selection;
				updateTypeSelection();
			}
		}
	}

	private final Set<InstanceSelectionListener> listeners = new HashSet<InstanceSelectionListener>();
	private InstanceSelectorControl current;
	private final DataSet dataSet;
	private InstanceSelection lastSelection;

	/**
	 * Constructor
	 * @param dataSet the data set 
	 */
	public WindowSelectionSelector(DataSet dataSet) {
		this.dataSet = dataSet;
	}

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

	/**
	 * Show the given selection.
	 *
	 * @param is the selection to show
	 */
	public void showSelection(InstanceSelection is) {
		if (current != null && !current.isDisposed())
			current.selectionChanged(null, is);
	}
}
