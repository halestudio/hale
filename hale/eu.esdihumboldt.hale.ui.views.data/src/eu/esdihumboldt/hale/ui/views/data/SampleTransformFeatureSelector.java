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

package eu.esdihumboldt.hale.ui.views.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.Feature;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.InstanceSelectionListener;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.InstanceSelector;

/**
 * Feature selector based on a transformation sample
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SampleTransformFeatureSelector implements InstanceSelector {
	
	/**
	 * Feature selector control
	 */
	private class FeatureSelectorControl extends Composite {
		
		private final ComboViewer featureTypes;
		
		private final Map<Definition, List<Feature>> featureMap = new HashMap<Definition, List<Feature>>();
		
		private Definition selectedType;

		private final Observer referenceListener;
		
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
					if (e1 instanceof Definition && e2 instanceof Definition) {
						return ((Definition) e1).getDisplayName().compareTo(
								((Definition) e2).getDisplayName());
					}
					return super.compare(viewer, e1, e2);
				}
				
			});
			featureTypes.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof Definition) {
						return ((Definition) element).getDisplayName();
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
					
			updateFeatureTypesSelection();
			
			// service listeners
			ReferenceSampleService rss = (ReferenceSampleService) PlatformUI.getWorkbench().getService(ReferenceSampleService.class);
			rss.addObserver(referenceListener = new Observer() {
				
				@Override
				public void update(Observable arg0, Object arg1) {
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
		}
		
		/**
		 * Update the feature types selection
		 */
		protected void updateFeatureTypesSelection() {
			//FIXME update
//			featureMap.clear();
//			
//			final ReferenceSampleService rss = (ReferenceSampleService) PlatformUI.getWorkbench().getService(ReferenceSampleService.class);
//			final AlignmentService alService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
//			final CstService cst = (CstService) PlatformUI.getWorkbench().getService(CstService.class);
//			final SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
//			
//			// target schema
//			Map<Definition, FeatureType> targetTypes = schemaService.getTargetSchema().getTypes();
//			Set<FeatureType> fts = new HashSet<FeatureType>(targetTypes.values());
//			Map<FeatureType, Definition> elementMap = new HashMap<FeatureType, Definition>();
//			for (Entry<Definition, FeatureType> entry : targetTypes.entrySet()) {
//				elementMap.put(entry.getValue(), entry.getKey());
//			}
//			
//			// get reference features
//			Collection<Feature> reference = rss.getReferenceInstances();
//			
//			if (reference != null && !reference.isEmpty()) {
//				// create a feature collection
//				MemoryFeatureCollection features = new MemoryFeatureCollection(((SimpleFeature) reference.iterator().next()).getFeatureType());
//				for (Feature refFeature : reference) {
//					features.add((SimpleFeature) refFeature);
//				}
//				
//				// transform features
//				FeatureCollection<FeatureType, Feature> transformed = (FeatureCollection<FeatureType, Feature>) cst.transform(
//						features, // Input Features
//						alService.getAlignment(), // Alignment
//						fts);
//				
//				// determine feature types
//				Iterator<Feature> it = transformed.iterator();
//				while (it.hasNext()) {
//					Feature feature = it.next();
//					FeatureType type = feature.getType();
//					Definition element = elementMap.get(type);
//					List<Feature> featureList = featureMap.get(element);
//					if (featureList == null) {
//						featureList = new ArrayList<Feature>();
//						featureMap.put(element, featureList);
//					}
//					featureList.add(feature);
//				}
//			}
//			
//			Collection<Definition> selectableTypes = featureMap.keySet();
//			featureTypes.setInput(selectableTypes);
//			
//			if (!selectableTypes.isEmpty()) {
//				featureTypes.setSelection(new StructuredSelection(selectableTypes.iterator().next()));
//				featureTypes.getControl().setEnabled(true);
//			}
//			else {
//				featureTypes.getControl().setEnabled(false);
//			}
//			
//			layout(true, true);
//			
//			updateSelection();
		}

		/**
		 * Update the selection
		 */
		protected void updateSelection() {
			//FIXME update
//			if (!featureTypes.getSelection().isEmpty()) {
//				Definition featureType = (Definition) ((IStructuredSelection) featureTypes.getSelection()).getFirstElement();
//				
//				selectedType = featureType;
//			}
//			else {
//				selectedType = null;
//			}
//			
//			for (InstanceSelectionListener listener : listeners) {
//				listener.selectionChanged(selectedType, getSelection());
//			}
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
			ReferenceSampleService rss = (ReferenceSampleService) PlatformUI.getWorkbench().getService(ReferenceSampleService.class);
			rss.deleteObserver(referenceListener);
			
			listeners.clear();
			
			super.dispose();
		}

	}

	private final Set<InstanceSelectionListener> listeners = new HashSet<InstanceSelectionListener>();
	
	private FeatureSelectorControl current;
	
	/**
	 * @see InstanceSelector#addSelectionListener(InstanceSelectionListener)
	 */
	@Override
	public void addSelectionListener(InstanceSelectionListener listener) {
		//FIXME update
//		listeners.add(listener);
//		
//		if (current != null && !current.isDisposed()) {
//			listener.selectionChanged(current.selectedType, current.getSelection());
//		}
	}

	/**
	 * @see InstanceSelector#createControl(Composite)
	 */
	@Override
	public Control createControl(Composite parent) {
		current = new FeatureSelectorControl(parent, SWT.NONE);
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
