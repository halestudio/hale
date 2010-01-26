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

import org.apache.log4j.Logger;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.FeatureTypeHelper;
import eu.esdihumboldt.hale.rcp.utils.filter.FeatureFilterField;
import eu.esdihumboldt.hale.rcp.utils.filter.FeatureFilterField.FilterListener;

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
	
	private static final Logger log = Logger.getLogger(FeatureSelector.class);
	
	private final ComboViewer schemaTypes;
	
	private final ComboViewer featureTypes;
	
	private final ComboViewer count;
	
	private final FeatureFilterField filterField;
	
	private Iterable<Feature> selection;
	
	private FeatureType selectedType;
	
	private final Image refreshImage;
	
	private final Set<FeatureSelectionListener> listeners = new HashSet<FeatureSelectionListener>();
	
	/**
	 * Create a feature selector
	 * 
	 * @param parent the parent composite
	 */
	public FeatureSelector(Composite parent) {
		super(parent, SWT.NONE);
		
		refreshImage = AbstractUIPlugin.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID, "icons/refresh.gif").createImage();
		
		setLayout(new GridLayout(5, false));
		
		// schema type selector
		schemaTypes = new ComboViewer(this, SWT.READ_ONLY);
		schemaTypes.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof SchemaType) {
					switch ((SchemaType) element) {
					case SOURCE: return "Reference";
					case TARGET: return "Transformed";
					default:
						return "#unknown";
					}
				}
				else {
					return super.getText(element);
				}
			}
			
		});
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
		
		// filter field
		filterField = new FeatureFilterField(selectedType, this, SWT.NONE);
		filterField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterField.addListener(new FilterListener() {
			
			@Override
			public void filterChanged() {
				updateSelection();
			}
			
		});
		
		// refresh button
		Button refresh = new Button(this, SWT.PUSH);
		refresh.setImage(refreshImage);
		refresh.setToolTipText("Refresh");
		refresh.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		refresh.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSelection();
			}
			
		});
		
		// max count selector
		count = new ComboViewer(this, SWT.READ_ONLY);
		count.setContentProvider(ArrayContentProvider.getInstance());
		count.setInput(new Integer[]{Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3),
				Integer.valueOf(4), Integer.valueOf(5)});
		count.setSelection(new StructuredSelection(Integer.valueOf(2)));
		count.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateSelection();
			}
			
		});
		
		updateFeatureTypesSelection();
		
		// service listeners
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		ss.addListener(new HaleServiceListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
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
		});
		
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		is.addListener(new HaleServiceListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
				if (Display.getCurrent() != null) {
					updateSelection();
				}
				else {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {
						
						@Override
						public void run() {
							updateSelection();
						}
					});
				}
			}
			
		});
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
		
		layout(true, true);
		
		updateSelection();
	}

	/**
	 * Update the selection
	 */
	protected void updateSelection() {
		if (!featureTypes.getSelection().isEmpty()) {
			FeatureType featureType = (FeatureType) ((IStructuredSelection) featureTypes.getSelection()).getFirstElement();
			
			filterField.setFeatureType(featureType);
			
			SchemaType schemaType = (SchemaType) ((IStructuredSelection) schemaTypes.getSelection()).getFirstElement();
			
			Integer max = (Integer) ((IStructuredSelection) count.getSelection()).getFirstElement();
			
			InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
			
			List<Feature> featureList = new ArrayList<Feature>();
			DatasetType dataset = (schemaType == SchemaType.SOURCE)?(DatasetType.reference):(DatasetType.transformed);
			try {
				Filter filter = filterField.getFilter();
				
				if (filter == null) {
					Collection<? extends Feature> features = is.getFeaturesByType(
						dataset, 
						featureType);
					
					Iterator<? extends Feature> it = features.iterator();
					int num = 0;
					while (it.hasNext() && num < max) {
						featureList.add(it.next());
						num++;
					}
				}
				else {
					FeatureCollection<FeatureType, Feature> fc = is.getFeatures(dataset);
					
					FeatureIterator<Feature> it = fc.subCollection(filter).features();
					int num = 0;
					while (it.hasNext() && num < max) {
						featureList.add(it.next());
						num++;
					}
				}
			} catch (Exception e) {
				log.warn("Error creating filter");
			}
			
			selection = featureList;
			selectedType = featureType;
		}
		else {
			selection = null;
			selectedType = null;
			
			filterField.setFeatureType(null);
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

	/**
	 * @see Widget#dispose()
	 */
	@Override
	public void dispose() {
		refreshImage.dispose();
		
		super.dispose();
	}

}
