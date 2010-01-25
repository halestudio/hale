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

package eu.esdihumboldt.hale.rcp.views.table.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.rcp.utils.FeatureTypeHelper;

/**
 * Selects filtered features
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureSelector extends Composite {
	
	/**
	 * Feature selection listener interface
	 */
	public interface FeatureSelectionListener {
		
		/**
		 * Called when the selection changed
		 * 
		 * @param type the feature type
		 * @param selection the selected features
		 */
		public void selectionChanged(FeatureType type, Iterable<Feature> selection);
		
	}
	
	private ComboViewer schemaTypes;
	
	private ComboViewer featureTypes;
	
	private ComboViewer count;
	
	private Iterable<Feature> selection;
	
	private FeatureType selectedType;
	
	private final Set<FeatureSelectionListener> listeners = new HashSet<FeatureSelectionListener>();
	
	/**
	 * Create a feature selector
	 * 
	 * @param parent the parent composite
	 */
	public FeatureSelector(Composite parent) {
		super(parent, SWT.NONE);
		
		setLayout(new GridLayout(3, false));
		
		// schema type selector
		schemaTypes = new ComboViewer(this, SWT.READ_ONLY);
		schemaTypes.setContentProvider(ArrayContentProvider.getInstance());
		schemaTypes.setInput(new Object[]{SchemaType.SOURCE, SchemaType.TARGET});
		schemaTypes.setSelection(new StructuredSelection(SchemaType.SOURCE));
		schemaTypes.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateFeatureTypesSelection();
			}
			
		});
		
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
				if (element instanceof FeatureType) {
					return ((FeatureType) element).getName().getLocalPart();
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
		
		// max count selector
		count = new ComboViewer(this, SWT.READ_ONLY);
		count.setContentProvider(ArrayContentProvider.getInstance());
		count.setInput(new Integer[]{Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)});
		count.setSelection(new StructuredSelection(Integer.valueOf(2)));
		count.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateSelection();
			}
			
		});
		
		updateFeatureTypesSelection();
	}
	
	/**
	 * Update the feature types selection
	 */
	protected void updateFeatureTypesSelection() {
		SchemaType schemaType = (SchemaType) ((IStructuredSelection) schemaTypes.getSelection()).getFirstElement();
		
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		Collection<FeatureType> types = ss.getSchema(schemaType);
		
		List<FeatureType> filteredTypes = new ArrayList<FeatureType>();
		for (FeatureType type : types) {
			if (!FeatureTypeHelper.isAbstract(type) && !FeatureTypeHelper.isPropertyType(type)) {
				filteredTypes.add(type);
			}
		}
		
		featureTypes.setInput(filteredTypes);
		
		if (!filteredTypes.isEmpty()) {
			featureTypes.setSelection(new StructuredSelection(filteredTypes.iterator().next()));
		}
		updateSelection();
	}

	/**
	 * Update the selection
	 */
	protected void updateSelection() {
		if (!featureTypes.getSelection().isEmpty()) {
			FeatureType featureType = (FeatureType) ((IStructuredSelection) featureTypes.getSelection()).getFirstElement();
			
			SchemaType schemaType = (SchemaType) ((IStructuredSelection) schemaTypes.getSelection()).getFirstElement();
			
			Integer max = (Integer) ((IStructuredSelection) count.getSelection()).getFirstElement();
			
			InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
			Collection<? extends Feature> features = is.getFeaturesByType(
					(schemaType == SchemaType.SOURCE)?(DatasetType.reference):(DatasetType.transformed), 
					featureType);
			
			List<Feature> featureList = new ArrayList<Feature>();
			Iterator<? extends Feature> it = features.iterator();
			int num = 0;
			while (it.hasNext() && num < max) {
				featureList.add(it.next());
				num++;
			}
			
			selection = featureList;
			selectedType = featureType;
		}
		else {
			selection = null;
			selectedType = null;
		}
		
		for (FeatureSelectionListener listener : listeners) {
			listener.selectionChanged(selectedType, selection);
		}
	}
	
	/**
	 * Add a listener
	 * 
	 * @param listener the listener to add
	 */
	public void addSelectionListener(FeatureSelectionListener listener) {
		listeners.add(listener);
		
		listener.selectionChanged(selectedType, selection);
	}
	
	/**
	 * Remove a listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeSelectionListener(FeatureSelectionListener listener) {
		listeners.remove(listener);
	}

}
