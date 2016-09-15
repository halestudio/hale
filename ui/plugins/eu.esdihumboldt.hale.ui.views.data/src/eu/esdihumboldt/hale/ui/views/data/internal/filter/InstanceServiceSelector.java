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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceMetadata;
import eu.esdihumboldt.hale.common.instance.model.MetaFilter;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.filter.TypeFilterField;
import eu.esdihumboldt.hale.ui.filter.cql.CQLFilterField;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.ui.views.data.internal.DataViewPlugin;
import eu.esdihumboldt.hale.ui.views.data.internal.Messages;

/**
 * Selects filtered features
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class InstanceServiceSelector implements InstanceSelector {

//	private static final ALogger log = ALoggerFactory.getLogger(InstanceServiceSelector.class);

	/**
	 * Instance selector control
	 */
	private class InstanceSelectorControl extends Composite {

		private final ComboViewer schemaSpaces;

		private final ComboViewer typeDefinitions;

		private final ComboViewer count;

		final CQLFilterField filterField;

		private Iterable<Instance> selection;

		/*
		 * XXX There seems to be a minor (memory leak) with selectedType not
		 * being reset when the project is cleared, though this should be OK, as
		 * it will be replaced, when a schema is loaded.
		 */
		private TypeDefinition selectedType;

		private final Image refreshImage;

		private final SchemaServiceListener schemaListener;

		private final InstanceServiceListener instanceListener;

		/**
		 * @see Composite#Composite(Composite, int)
		 */
		public InstanceSelectorControl(Composite parent, int style) {
			super(parent, style);

			refreshImage = DataViewPlugin.getImageDescriptor("icons/refresh.gif").createImage(); //$NON-NLS-1$

			GridLayout layout = new GridLayout((spaceID == null) ? (4) : (3), false);
			layout.marginHeight = 2;
			layout.marginWidth = 3;
			setLayout(layout);

			// schema type selector
			if (spaceID == null) {
				schemaSpaces = new ComboViewer(this, SWT.READ_ONLY);
				schemaSpaces.setLabelProvider(new LabelProvider() {

					@Override
					public String getText(Object element) {
						if (element instanceof SchemaSpaceID) {
							switch ((SchemaSpaceID) element) {
							case SOURCE:
								return Messages.InstanceServiceFeatureSelector_SourceReturnText;
							case TARGET:
								return Messages.InstanceServiceFeatureSelector_TargetReturnText;
							default:
								return Messages.InstanceServiceFeatureSelector_defaultReturnText;
							}
						}
						else {
							return super.getText(element);
						}
					}

				});
				schemaSpaces.setContentProvider(ArrayContentProvider.getInstance());
				schemaSpaces.setInput(new Object[] { SchemaSpaceID.SOURCE, SchemaSpaceID.TARGET });
				schemaSpaces.setSelection(new StructuredSelection(SchemaSpaceID.SOURCE));
			}
			else {
				schemaSpaces = null;
			}

			// feature type selector
			typeDefinitions = new ComboViewer(this, SWT.READ_ONLY);
			typeDefinitions.setContentProvider(ArrayContentProvider.getInstance());
			typeDefinitions.setComparator(new DefinitionComparator());
			typeDefinitions.setLabelProvider(new DefinitionLabelProvider(null));
			typeDefinitions.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateSelection();
				}

			});

			// filter field
			filterField = new CQLFilterField((selectedType == null) ? (null) : (selectedType), this,
					SWT.NONE, spaceID);
			filterField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			filterField.addListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(TypeFilterField.PROPERTY_FILTER)) {
						updateSelection();
					}
				}
			});

			// refresh button
			/*
			 * XXX disabled for now - Button refresh = new Button(this,
			 * SWT.PUSH); refresh.setImage(refreshImage);
			 * refresh.setToolTipText("Refresh"); refresh.setLayoutData(new
			 * GridData(SWT.CENTER, SWT.CENTER, false, false));
			 * refresh.addSelectionListener(new SelectionAdapter() {
			 * 
			 * @Override public void widgetSelected(SelectionEvent e) {
			 * updateSelection(); }
			 * 
			 * });
			 */

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

			updateTypesSelection();

			if (schemaSpaces != null) {
				schemaSpaces.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						updateTypesSelection();
					}

				});
			}

			// service listeners
			SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
			ss.addSchemaServiceListener(schemaListener = new SchemaServiceListener() {

				@Override
				public void schemaAdded(SchemaSpaceID spaceID, Schema schema) {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {

						@Override
						public void run() {
							updateTypesSelection();
						}
					});
				}

				@Override
				public void schemasCleared(SchemaSpaceID spaceID) {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {

						@Override
						public void run() {
							updateTypesSelection();
						}
					});
				}

				@Override
				public void mappableTypesChanged(SchemaSpaceID spaceID,
						Collection<? extends TypeDefinition> types) {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {

						@Override
						public void run() {
							updateTypesSelection();
						}
					});
				}
			});

			InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
			is.addListener(instanceListener = new InstanceServiceAdapter() {

				@Override
				public void datasetChanged(DataSet dataSet) {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {

						@Override
						public void run() {
							updateTypesSelection();
						}
					});
				}

			});
		}

		/**
		 * Update the feature types selection
		 */
		protected void updateTypesSelection() {
			SchemaSpaceID space = getSchemaSpace();

			TypeDefinition lastSelected = null;
			ISelection lastSelection = typeDefinitions.getSelection();
			if (!lastSelection.isEmpty() && lastSelection instanceof IStructuredSelection) {
				lastSelected = (TypeDefinition) ((IStructuredSelection) lastSelection)
						.getFirstElement();
			}

			DataSet dataset = (space == SchemaSpaceID.SOURCE) ? (DataSet.SOURCE)
					: (DataSet.TRANSFORMED);
			InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);

			// get instance types
			List<TypeDefinition> filteredTypes = new ArrayList<TypeDefinition>(
					is.getInstanceTypes(dataset));

			if (filteredTypes.isEmpty()) {
				// if there are no instances present, show all types
				SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
				filteredTypes = new ArrayList<TypeDefinition>(
						ss.getSchemas(space).getMappingRelevantTypes());
			}

			typeDefinitions.setInput(filteredTypes);

			// select the previously selected type if possible
			TypeDefinition typeToSelect = (filteredTypes.contains(lastSelected)) ? (lastSelected)
					: (null);

			// fallback selection
			if (typeToSelect == null && !filteredTypes.isEmpty()) {
				typeToSelect = filteredTypes.iterator().next();
			}

			if (typeToSelect != null) {
				typeDefinitions.setSelection(new StructuredSelection(typeToSelect));
			}

			boolean enabled = !filteredTypes.isEmpty();
			typeDefinitions.getControl().setEnabled(enabled);
			count.getControl().setEnabled(enabled);

			layout(true, true);

			updateSelection();
		}

		/**
		 * Get the selected schema type
		 * 
		 * @return the selected schema type
		 */
		private SchemaSpaceID getSchemaSpace() {
			if (spaceID != null) {
				return spaceID;
			}
			else {
				return (SchemaSpaceID) ((IStructuredSelection) schemaSpaces.getSelection())
						.getFirstElement();
			}
		}

		/**
		 * Update the selection
		 */
		protected void updateSelection() {
			if (!typeDefinitions.getSelection().isEmpty()) {
				TypeDefinition type = (TypeDefinition) ((IStructuredSelection) typeDefinitions
						.getSelection()).getFirstElement();

				filterField.setType(type);

				SchemaSpaceID space = getSchemaSpace();

				Integer max = (Integer) ((IStructuredSelection) count.getSelection())
						.getFirstElement();

				InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);

				List<Instance> instanceList = new ArrayList<Instance>();
				DataSet dataset = (space == SchemaSpaceID.SOURCE) ? (DataSet.SOURCE)
						: (DataSet.TRANSFORMED);

				Filter filter = null;
				String filterExpression = filterField.getFilterExpression();
				/*
				 * Custom filter handling.
				 * 
				 * FIXME Ultimately this should be done by the filter field
				 * instead, which should be able to handle all kinds of
				 * registered filters (e.g. also Groovy).
				 */
				if (filterExpression.startsWith("id:")) {
					// XXX meta ID "hack"
					String metaFilter = filterExpression.substring("id:".length());
					String[] values = metaFilter.split(",");

					filter = new MetaFilter(type, InstanceMetadata.METADATA_ID,
							new HashSet<>(Arrays.asList(values)));
				}
				else if (filterExpression.startsWith("source:")) {
					// XXX meta source ID "hack"
					String metaFilter = filterExpression.substring("source:".length());
					String[] values = metaFilter.split(",");

					filter = new MetaFilter(type, InstanceMetadata.METADATA_SOURCEID,
							new HashSet<>(Arrays.asList(values)));
				}
				else {
					filter = filterField.getFilter();
				}

				InstanceCollection instances = is.getInstances(dataset);
				if (filter != null) {
					instances = instances.select(filter);
				}

				ResourceIterator<Instance> it = instances.iterator();
				try {
					int num = 0;
					while (it.hasNext() && num < max) {
						Instance instance = it.next();
						if (instance.getDefinition().equals(type)) {
							instanceList.add(instance);
							num++;
						}
					}
				} finally {
					it.close();
				}

				selection = instanceList;
				selectedType = type;
			}
			else {
				selection = null;
				selectedType = null;

				filterField.setType(null);
			}

			for (InstanceSelectionListener listener : listeners) {
				listener.selectionChanged(selectedType, selection);
			}
		}

		/**
		 * @see Widget#dispose()
		 */
		@Override
		public void dispose() {
			SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
			InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);

			ss.removeSchemaServiceListener(schemaListener);
			is.removeListener(instanceListener);

			refreshImage.dispose();

			listeners.clear();

			super.dispose();
		}

	}

	private final Set<InstanceSelectionListener> listeners = new HashSet<InstanceSelectionListener>();

	private InstanceSelectorControl current;

	private final SchemaSpaceID spaceID;

	/**
	 * Create an instance selector
	 * 
	 * @param spaceID the fixed schema space ID or <code>null</code> to allow
	 *            selecting the schema space
	 */
	public InstanceServiceSelector(SchemaSpaceID spaceID) {
		super();

		this.spaceID = spaceID;
	}

	/**
	 * @see InstanceSelector#addSelectionListener(InstanceSelectionListener)
	 */
	@Override
	public void addSelectionListener(InstanceSelectionListener listener) {
		listeners.add(listener);

		if (current != null && !current.isDisposed()) {
			listener.selectionChanged(current.selectedType, current.selection);
		}
	}

	/**
	 * @see InstanceSelector#removeSelectionListener(InstanceSelectionListener)
	 */
	@Override
	public void removeSelectionListener(InstanceSelectionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see InstanceSelector#createControl(Composite)
	 */
	@Override
	public Control createControl(Composite parent) {
		current = new InstanceSelectorControl(parent, SWT.NONE);
		return current;
	}

}
