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
import java.util.Collections;
import java.util.Comparator;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.filter.FeatureFilterField;
import eu.esdihumboldt.hale.rcp.utils.filter.FeatureFilterField.FilterListener;
import eu.esdihumboldt.hale.rcp.views.table.Messages;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * Selects filtered features
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class InstanceServiceFeatureSelector implements FeatureSelector {
	
	private static final Logger log = Logger.getLogger(InstanceServiceFeatureSelector.class);
	
	/**
	 * Feature selector control
	 */
	private class FeatureSelectorControl extends Composite {
		
		private final ComboViewer schemaTypes;
		
		private final ComboViewer featureTypes;
		
		private final ComboViewer count;
		
		private final FeatureFilterField filterField;
		
		private Iterable<Feature> selection;
		
		private SchemaElement selectedType;
		
		private final Image refreshImage;

		private final HaleServiceListener schemaListener;

		private final HaleServiceListener instanceListener;
	
		/**
		 * @see Composite#Composite(Composite, int)
		 */
		public FeatureSelectorControl(Composite parent, int style) {
			super(parent, style);
			
			refreshImage = AbstractUIPlugin.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID, "icons/refresh.gif").createImage(); //$NON-NLS-1$
			
			setLayout(new GridLayout((fixedSchemaType == null)?(4):(3), false));
			
			// schema type selector
			if (fixedSchemaType == null) {
				schemaTypes = new ComboViewer(this, SWT.READ_ONLY);
				schemaTypes.setLabelProvider(new LabelProvider() {
		
					@Override
					public String getText(Object element) {
						if (element instanceof SchemaType) {
							switch ((SchemaType) element) {
							case SOURCE: return Messages.InstanceServiceFeatureSelector_SourceReturnText;
							case TARGET: return Messages.InstanceServiceFeatureSelector_TargetReturnText;
							default:
								return Messages.InstanceServiceFeatureSelector_defaultReturnText;
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
			}
			else {
				schemaTypes = null;
			}
			
			// feature type selector
			featureTypes = new ComboViewer(this, SWT.READ_ONLY);
			featureTypes.setContentProvider(ArrayContentProvider.getInstance());
			/*featureTypes.setComparator(new ViewerComparator() {

				@Override
				public int compare(Viewer viewer, Object e1, Object e2) {
					if (e1 instanceof FeatureType && e2 instanceof FeatureType) {
						return ((FeatureType) e1).getName().getLocalPart().compareTo(
								((FeatureType) e2).getName().getLocalPart());
					}
					return super.compare(viewer, e1, e2);
				}
				
			});*/
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
			
			// filter field
			filterField = new FeatureFilterField((selectedType == null)?(null):(selectedType.getType()), this, SWT.NONE);
			filterField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			filterField.addListener(new FilterListener() {
				
				@Override
				public void filterChanged() {
					updateSelection();
				}
				
			});
			
			// refresh button
			/*XXX disabled for now - Button refresh = new Button(this, SWT.PUSH);
			refresh.setImage(refreshImage);
			refresh.setToolTipText("Refresh");
			refresh.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			refresh.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					updateSelection();
				}
				
			});*/
			
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
			
			if (schemaTypes != null) {
				schemaTypes.addSelectionChangedListener(new ISelectionChangedListener() {
					
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						updateFeatureTypesSelection();
					}
					
				});
			}
			
			// service listeners
			SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
			ss.addListener(schemaListener = new HaleServiceListener() {
				
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
			is.addListener(instanceListener = new HaleServiceListener() {
				
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
			SchemaType schemaType = getSchemaType();
			
			SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
			Collection<SchemaElement> elements = ss.getSchema(schemaType);
			
			List<SchemaElement> filteredTypes = new ArrayList<SchemaElement>();
			for (SchemaElement element : elements) {
				if (!element.getType().isAbstract() && element.getType().isFeatureType()) {
					filteredTypes.add(element);
				}
			}
			
			Collections.sort(filteredTypes, new Comparator<SchemaElement>() {

				@Override
				public int compare(SchemaElement o1, SchemaElement o2) {
					return o1.getElementName().getLocalPart().compareTo(o2.getElementName().getLocalPart());
				}
				
			});
			
			featureTypes.setInput(filteredTypes);
			
			FeatureType typeToSelect = null;
			
			// try to determine type to select from data set
			DatasetType dataset = (schemaType == SchemaType.SOURCE)?(DatasetType.reference):(DatasetType.transformed);
			InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
			FeatureCollection<FeatureType, Feature> features = is.getFeatures(dataset);
			if (features != null) {
				Iterator<Feature> itFeature = features.iterator();
				while (itFeature.hasNext() && typeToSelect == null) {
					Feature feature = itFeature.next();
					if (feature instanceof SimpleFeature) {
						typeToSelect = ((SimpleFeature) feature).getFeatureType();
					}
				}
			}
			
			SchemaElement elementToSelect = null;
			if (typeToSelect != null) {
				// find type to select in filtered types
				SchemaElement typeFound = null;
				Iterator<SchemaElement> it = filteredTypes.iterator();
				while (typeFound == null && it.hasNext()) {
					SchemaElement type = it.next();
					if (type.getFeatureType().getName().equals(typeToSelect.getName())) { //XXX is Name.equals really working?
						typeFound = type;
					}
				}
				
				elementToSelect = typeFound;
			}
			
			// fallback selection
			if (elementToSelect == null && !filteredTypes.isEmpty()) {
				elementToSelect = filteredTypes.iterator().next();
			}
			
			if (elementToSelect != null) {
				featureTypes.setSelection(new StructuredSelection(elementToSelect));
			}
			
			layout(true, true);
			
			updateSelection();
		}

		/**
		 * Get the selected schema type
		 * 
		 * @return the selected schema type
		 */
		private SchemaType getSchemaType() {
			if (fixedSchemaType != null) {
				return fixedSchemaType;
			}
			else {
				return (SchemaType) ((IStructuredSelection) schemaTypes.getSelection()).getFirstElement();
			}
		}

		/**
		 * Update the selection
		 */
		protected void updateSelection() {
			if (!featureTypes.getSelection().isEmpty()) {
				SchemaElement type = (SchemaElement) ((IStructuredSelection) featureTypes.getSelection()).getFirstElement();
				
				filterField.setType(type.getType());
				
				SchemaType schemaType = getSchemaType();
				
				Integer max = (Integer) ((IStructuredSelection) count.getSelection()).getFirstElement();
				
				InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
				
				List<Feature> featureList = new ArrayList<Feature>();
				DatasetType dataset = (schemaType == SchemaType.SOURCE)?(DatasetType.reference):(DatasetType.transformed);
				try {
					Filter filter = filterField.getFilter();
					
					if (filter == null) {
						Collection<? extends Feature> features = is.getFeaturesByType(
							dataset, 
							type.getFeatureType());
						
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
					log.warn("Error creating filter"); //$NON-NLS-1$
				}
				
				selection = featureList;
				selectedType = type;
			}
			else {
				selection = null;
				selectedType = null;
				
				filterField.setType(null);
			}
			
			for (FeatureSelectionListener listener : listeners) {
				listener.selectionChanged(selectedType, selection);
			}
		}
		
		/**
		 * @see Widget#dispose()
		 */
		@Override
		public void dispose() {
			SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
			InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
			
			ss.removeListener(schemaListener);
			is.removeListener(instanceListener);
			
			refreshImage.dispose();
			
			listeners.clear();
			
			super.dispose();
		}

	}
	
	private final Set<FeatureSelectionListener> listeners = new HashSet<FeatureSelectionListener>();
	
	private FeatureSelectorControl current;
	
	private final SchemaType fixedSchemaType;
	
	/**
	 * Create a feature selector
	 * 
	 * @param fixedSchemaType the fixed schema type or <code>null</code> to
	 *   allow selecting the schema type
	 */
	public InstanceServiceFeatureSelector(SchemaType fixedSchemaType) {
		super();
		
		this.fixedSchemaType = fixedSchemaType;
	}
	
	/**
	 * @see FeatureSelector#addSelectionListener(FeatureSelectionListener)
	 */
	public void addSelectionListener(FeatureSelectionListener listener) {
		listeners.add(listener);
		
		if (current != null && !current.isDisposed()) {
			listener.selectionChanged(current.selectedType, current.selection);
		}
	}
	
	/**
	 * @see FeatureSelector#removeSelectionListener(FeatureSelectionListener)
	 */
	public void removeSelectionListener(FeatureSelectionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see FeatureSelector#createControl(Composite)
	 */
	@Override
	public Control createControl(Composite parent) {
		current = new FeatureSelectorControl(parent, SWT.NONE);
		return current;
	}

}
