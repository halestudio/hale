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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.filter.TypeFilterField;
import eu.esdihumboldt.hale.ui.filter.cql.CQLFilterField;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.util.selection.SelectionTrackerUtil;
import eu.esdihumboldt.hale.ui.views.data.AbstractDataView;

/**
 * Instance selector retrieving values from the selection service.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class WindowSelectionSelector implements AdvancedInstanceSelector {

	/**
	 * Instance selector control
	 */
	private class InstanceSelectorControl extends Composite implements ISelectionListener {

		private final ComboViewer instanceTypes;
		private final Map<TypeDefinition, List<Instance>> instanceMap = new HashMap<TypeDefinition, List<Instance>>();
		private TypeDefinition selectedType;
		private final CQLFilterField filterField;
		private final ComboViewer count;

		/**
		 * @see Composite#Composite(Composite, int)
		 */
		public InstanceSelectorControl(Composite parent, int style) {
			super(parent, style);

			GridLayout layout = new GridLayout(3, false);
			layout.marginHeight = 2;
			layout.marginWidth = 3;
			setLayout(layout);

			// instance type selector
			instanceTypes = new ComboViewer(this, SWT.READ_ONLY);
			instanceTypes.setContentProvider(ArrayContentProvider.getInstance());
			instanceTypes.setComparator(new DefinitionComparator());
			instanceTypes.setLabelProvider(new DefinitionLabelProvider(null));
			instanceTypes.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateSelection();
				}

			});
			instanceTypes.getControl()
					.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

			// filter field
			filterField = new CQLFilterField((selectedType == null) ? (null) : (selectedType), this,
					SWT.NONE, SchemaSpaceID.TARGET);
			filterField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			filterField.addListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(TypeFilterField.PROPERTY_FILTER)) {
						updateSelection();
					}
				}
			});

			// max count selector
			count = new ComboViewer(this, SWT.READ_ONLY);
			count.setContentProvider(ArrayContentProvider.getInstance());
			count.setInput(new Integer[] { Integer.valueOf(1), Integer.valueOf(2),
					Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5) });
			count.setSelection(new StructuredSelection(Integer.valueOf(2)));
			count.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateSelection();
				}
			});

			// service listeners
			ISelectionService ss = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getSelectionService();
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

			boolean enabled = !selectableTypes.isEmpty();
			if (enabled)
				instanceTypes
						.setSelection(new StructuredSelection(selectableTypes.iterator().next()));

			instanceTypes.getControl().setEnabled(enabled);
			count.getControl().setEnabled(enabled);
			filterField.setEnabled(enabled);

			layout(true, true);

			updateSelection();
		}

		/**
		 * Get the selected instances form the current {@link InstanceSelection}
		 * 
		 * @param typeFilter the type for the instances to match,
		 *            <code>null</code> for any type
		 * @return the selected features or <code>null</code>
		 */
		private Collection<Instance> getSelectedInstances(TypeDefinition typeFilter) {
			if (lastSelection == null || lastSelection.isEmpty()) {
				return null;
			}
			else {
				List<?> elements = lastSelection.toList();
				Collection<Instance> result = new ArrayList<Instance>(elements.size());

				final InstanceService instanceService = PlatformUI.getWorkbench()
						.getService(InstanceService.class);

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

					if (instance != null && (typeFilter == null
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
				TypeDefinition type = (TypeDefinition) ((IStructuredSelection) instanceTypes
						.getSelection()).getFirstElement();
				filterField.setType(type);
				selectedType = type;
			}
			else {
				filterField.setType(null);
				selectedType = null;
			}

			for (InstanceSelectionListener listener : listeners) {
				listener.selectionChanged(selectedType, getSelection());
			}
		}

		/**
		 * Get the currently selected instances.
		 * 
		 * @return the currently selected instances
		 */
		public Iterable<Instance> getSelection() {
			if (selectedType == null)
				return null;
			else {
				List<Instance> selection = instanceMap.get(selectedType);

				Integer max = (Integer) ((IStructuredSelection) count.getSelection())
						.getFirstElement();
				Filter filter = filterField.getFilter();

				List<Instance> result = new ArrayList<Instance>(max);
				int count = 0;
				Iterator<Instance> iter = selection.iterator();
				while (iter.hasNext() && count < max) {
					Instance instance = iter.next();
					if (filter == null || filter.match(instance)) {
						result.add(instance);
						count++;
					}
				}

				return result;
			}
		}

		/**
		 * @see Widget#dispose()
		 */
		@Override
		public void dispose() {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
					.removePostSelectionListener(this);

			listeners.clear();

			super.dispose();
		}

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (!(part instanceof AbstractDataView) // don't react on data view
													// changes (to prevent
													// loops)
					&& selection instanceof InstanceSelection) {
				lastSelection = (InstanceSelection) selection;
				if (part != null) {
					selectionImage = part.getTitleImage();
				}
				else {
					selectionImage = null;
				}
				updateButton();
				updateTypeSelection();
			}
		}
	}

	private final Set<InstanceSelectionListener> listeners = new HashSet<InstanceSelectionListener>();
	private InstanceSelectorControl current;
	private final DataSet dataSet;
	private InstanceSelection lastSelection;

	private Button activator;
	private Image defaultImage;
	private Image selectionImage;

	/**
	 * Constructor
	 * 
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
	 * @param image an image to show for the selection, may be <code>null</code>
	 */
	public void showSelection(InstanceSelection is, final Image image) {
		if (current != null && !current.isDisposed()) {
			current.selectionChanged(new IWorkbenchPart() { // dummy part

				@SuppressWarnings("unchecked")
				@Override
				public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
					// dummy
					return null;
				}

				@Override
				public void setFocus() {
					// dummy
				}

				@Override
				public void removePropertyListener(IPropertyListener listener) {
					// dummy
				}

				@Override
				public String getTitleToolTip() {
					// dummy
					return null;
				}

				@Override
				public Image getTitleImage() {
					return image;
				}

				@Override
				public String getTitle() {
					// dummy
					return null;
				}

				@Override
				public IWorkbenchPartSite getSite() {
					// dummy
					return null;
				}

				@Override
				public void dispose() {
					// dummy
				}

				@Override
				public void createPartControl(Composite parent) {
					// dummy
				}

				@Override
				public void addPropertyListener(IPropertyListener listener) {
					// dummy
				}
			}, is);
		}
	}

	/**
	 * @see AdvancedInstanceSelector#setActivator(Button)
	 */
	@Override
	public void setActivator(Button activator) {
		defaultImage = activator.getImage();
		this.activator = activator;
		updateButton();
	}

	/**
	 * Update the activator button image.
	 */
	private void updateButton() {
		if (activator != null) {
			if (selectionImage != null) {
				activator.setImage(selectionImage);
			}
			else {
				activator.setImage(defaultImage);
			}
//			activator.redraw();
		}
	}
}
