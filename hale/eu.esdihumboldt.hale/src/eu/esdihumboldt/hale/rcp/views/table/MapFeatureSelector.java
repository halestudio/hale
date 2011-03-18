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

package eu.esdihumboldt.hale.rcp.views.table;

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
import org.eclipse.jface.viewers.LabelProvider;
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
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.identity.FeatureId;

import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.rcp.views.map.FeatureSelection;
import eu.esdihumboldt.hale.rcp.views.map.MapView;
import eu.esdihumboldt.hale.rcp.views.table.filter.FeatureSelectionListener;
import eu.esdihumboldt.hale.rcp.views.table.filter.FeatureSelector;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MapFeatureSelector implements FeatureSelector {
	
	/**
	 * Feature selector control
	 */
	private class FeatureSelectorControl extends Composite implements ISelectionListener {
		
		private final ComboViewer featureTypes;
		
		private final Map<SchemaElement, List<Feature>> featureMap = new HashMap<SchemaElement, List<Feature>>();
		
		private SchemaElement selectedType;
		
		private ISelection lastSelection;

		/**
		 * @see Composite#Composite(Composite, int)
		 */
		public FeatureSelectorControl(Composite parent, int style) {
			super(parent, style);
			
			setLayout(new GridLayout(1, false));
			
			// feature type selector
			featureTypes = new ComboViewer(this, SWT.READ_ONLY);
			featureTypes.setContentProvider(ArrayContentProvider.getInstance());
			featureTypes.setComparator(new ViewerComparator() {

				@Override
				public int compare(Viewer viewer, Object e1, Object e2) {
					if (e1 instanceof FeatureType && e2 instanceof FeatureType) {
						return ((FeatureType) e1).getName().getLocalPart().compareTo(
								((FeatureType) e2).getName().getLocalPart());
					}
					return super.compare(viewer, e1, e2);
				}
				
			});
			featureTypes.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof SchemaElement) {
						return ((SchemaElement) element).getElementName().getLocalPart();
					}
					return super.getText(element);
				}
				
			});
			featureTypes.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateSelection();
				}
				
			});
			featureTypes.getControl().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
					
			// service listeners
			ISelectionService ss = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
			ss.addPostSelectionListener(this);
			
			lastSelection = ss.getSelection();
			if (lastSelection == null || !(lastSelection instanceof FeatureSelection)) {
				lastSelection = ss.getSelection(MapView.ID);
			}
			
			updateFeatureTypesSelection();
		}
		
		/**
		 * Update the feature types selection
		 */
		protected void updateFeatureTypesSelection() {
			featureMap.clear();
			
			final SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
			
			// schema
			Collection<SchemaElement> elements = schemaService.getSchema(schema);
			Set<FeatureType> fts = new HashSet<FeatureType>();
			Map<String, SchemaElement> elementMap = new HashMap<String, SchemaElement>();
			for (SchemaElement element : elements) {
				FeatureType type = element.getFeatureType();
				if (type != null) {
					fts.add(type);
					elementMap.put(getIdentifier(type), element);
				}
			}
			
			// get selected features
			Collection<Feature> features = getSelectedFeatures();
			
			if (features != null && !features.isEmpty()) {
				// determine feature types
				Iterator<Feature> it = features.iterator();
				while (it.hasNext()) {
					Feature feature = it.next();
					FeatureType type = feature.getType();
					SchemaElement element = elementMap.get(getIdentifier(type));
					List<Feature> featureList = featureMap.get(element);
					if (featureList == null) {
						featureList = new ArrayList<Feature>();
						featureMap.put(element, featureList);
					}
					featureList.add(feature);
				}
			}
			
			Collection<SchemaElement> selectableTypes = featureMap.keySet();
			featureTypes.setInput(selectableTypes);
			
			if (!selectableTypes.isEmpty()) {
				featureTypes.setSelection(new StructuredSelection(selectableTypes.iterator().next()));
				featureTypes.getControl().setEnabled(true);
			}
			else {
				featureTypes.getControl().setEnabled(false);
			}
			
			layout(true, true);
			
			updateSelection();
		}

		private String getIdentifier(FeatureType featureType) {
			return featureType.getName().getNamespaceURI() + "/" + featureType.getName().getLocalPart(); //$NON-NLS-1$
		}

		/**
		 * Get the selected features form the current {@link FeatureSelection}
		 * 
		 * @return the selected features or <code>null</code>
		 */
		private Collection<Feature> getSelectedFeatures() {
			if (lastSelection == null || lastSelection.isEmpty() || !(lastSelection instanceof FeatureSelection)) {
				return null;
			}
			else {
				Set<FeatureId> selectedIds = ((FeatureSelection) lastSelection).getFeatureIds();
				
				final InstanceService instanceService = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
				
				DatasetType type = (schema == SchemaType.SOURCE)?(DatasetType.reference):(DatasetType.transformed);
				FeatureCollection<FeatureType, Feature> collection = instanceService.getFeatures(type);
				
				List<Feature> features = new ArrayList<Feature>();
				
				Iterator<Feature> it = collection.iterator();
				while (it.hasNext()) {
					Feature feature = it.next();
					if (selectedIds.contains(feature.getIdentifier())) {
						features.add(feature);
					}
				}
				
				return features;
			}
		}

		/**
		 * Update the selection
		 */
		protected void updateSelection() {
			if (!featureTypes.getSelection().isEmpty()) {
				SchemaElement featureType = (SchemaElement) ((IStructuredSelection) featureTypes.getSelection()).getFirstElement();
				
				selectedType = featureType;
			}
			else {
				selectedType = null;
			}
			
			for (FeatureSelectionListener listener : listeners) {
				listener.selectionChanged(selectedType, getSelection());
			}
		}
		
		/**
		 * Get the currently selected features
		 * 
		 * @return the currently selected features
		 */
		public Iterable<Feature> getSelection() {
			if (selectedType == null) {
				return null;
			}
			else {
				return featureMap.get(selectedType);
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
			if (selection instanceof FeatureSelection) {
				lastSelection = selection;
				updateFeatureTypesSelection();
			}
		}

	}
	
	private final Set<FeatureSelectionListener> listeners = new HashSet<FeatureSelectionListener>();
	
	private FeatureSelectorControl current;
	
	private final SchemaType schema;

	/**
	 * Constructor
	 * 
	 * @param schema the schema type
	 */
	public MapFeatureSelector(SchemaType schema) {
		this.schema = schema;
	}

	/**
	 * @see FeatureSelector#addSelectionListener(FeatureSelectionListener)
	 */
	@Override
	public void addSelectionListener(FeatureSelectionListener listener) {
		listeners.add(listener);
		
		if (current != null && !current.isDisposed()) {
			listener.selectionChanged(current.selectedType, current.getSelection());
		}
	}

	/**
	 * @see FeatureSelector#createControl(Composite)
	 */
	@Override
	public Control createControl(Composite parent) {
		current = new FeatureSelectorControl(parent, SWT.NONE);
		return current;
	}

	/**
	 * @see FeatureSelector#removeSelectionListener(FeatureSelectionListener)
	 */
	@Override
	public void removeSelectionListener(FeatureSelectionListener listener) {
		listeners.remove(listener);
	}

}
