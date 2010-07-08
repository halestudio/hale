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
package eu.esdihumboldt.hale.rcp.views.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.styling.Style;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;

/**
 * Utility methods for rendering the map
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class MapUtils {
	
	private static final Log log = LogFactory.getLog(FeatureTileRenderer.class);
	
	/**
	 * Determine the coordinate reference system for a feature collection
	 * 
	 * @param fc the feature collection
	 * 
	 * @return the coordinate reference system or null
	 */
	private static CoordinateReferenceSystem determineCRS(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		CoordinateReferenceSystem crs = null;
		
		// try the instance data first.
		if (fc != null && !fc.isEmpty()) {
			Feature f = fc.features().next();
			if (f.getDefaultGeometryProperty() != null) {
				GeometryAttribute gp = f.getDefaultGeometryProperty();
				crs = gp.getDescriptor().getCoordinateReferenceSystem();
				
				if (crs == null) {
					// next try - user data of value
					Object value = gp.getValue();
					if (value instanceof Geometry) {
						Object userData = ((Geometry) value).getUserData();
						if (userData instanceof CoordinateReferenceSystem) {
							crs = (CoordinateReferenceSystem) userData;
						}
					}
				}
			}
		}
		
		// then check the schema.
		if (crs == null && fc != null) {
			crs = fc.getSchema().getCoordinateReferenceSystem();
		}
		
		// if none is available, use a default.
		if (crs == null) {
			final Display display = Display.getCurrent();
			
			SelectCRSDialog dialog = new SelectCRSDialog(display.getActiveShell());
			while (crs == null) {
				if (!dialog.determineCRS()) {
					return null; //XXX unable to determine CRS
				}
				else {
					crs = SelectCRSDialog.getValue();
				}
			}
		}
		return crs;
	}
	
	/**
	 * @param crs the {@link CoordinateReferenceSystem} to use.
	 * @param type the {@link DatasetType} to render.
	 * @param status 
	 * @param selection 
	 * @param selectionOnly 
	 * @return a {@link MapContext} with the given CRS and the 
	 * {@link FeatureCollection} identified by the given {@link DatasetType}.
	 */
	@SuppressWarnings("unchecked")
	public static MapContext buildMapContext(CoordinateReferenceSystem crs, 
			DatasetType type, FeaturePaintStatus status, Set<FeatureId> selection, 
			boolean selectionOnly) {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		
		FeatureCollection<?, ?> fc = is.getFeatures(type);
		if (fc != null && fc.size() > 0) {
			if (crs == null) {
				crs = determineCRS(is.getFeatures(DatasetType.reference)); // TODO always use source CRS! (Check whether OK)
			}
			
			if (selectionOnly) {
				//TODO apply a filter for the selected features?
				//fc.s
			}
			
			//log.info("features size: " + fc.size()); //$NON-NLS-1$
			//log.info("features bounds: " + fc.getBounds()); //$NON-NLS-1$
			
			int failed = 0;
			
			Map<SimpleFeatureType, FeatureCollection<SimpleFeatureType, SimpleFeature>> groupedFeatures = new HashMap<SimpleFeatureType, FeatureCollection<SimpleFeatureType, SimpleFeature>>();
			Map<SimpleFeatureType, FeatureCollection<SimpleFeatureType, SimpleFeature>> selectedFeatures = new HashMap<SimpleFeatureType, FeatureCollection<SimpleFeatureType, SimpleFeature>>();
			Iterator it = fc.iterator();
			while (it.hasNext()) {
				Object tmp = it.next();
				
				if (tmp instanceof SimpleFeature) {
					SimpleFeature feature = (SimpleFeature) tmp;
					
					if (validateFeature(feature)) {
						Map<SimpleFeatureType, FeatureCollection<SimpleFeatureType, SimpleFeature>> collectionMap;
						if (selectionOnly && selection.contains(feature.getIdentifier())) {
							collectionMap = selectedFeatures;
						}
						else if (!selectionOnly) {
							collectionMap = groupedFeatures;
						}
						else {
							collectionMap = null;
						}
						
						if (collectionMap != null) {
							FeatureCollection<SimpleFeatureType, SimpleFeature> collection = collectionMap.get(feature.getFeatureType());
							if (collection == null) {
								collection = new MemoryFeatureCollection(feature.getFeatureType());
								collectionMap.put(feature.getFeatureType(), collection);
							}
							
							collection.add(feature);
						}
					}
					else {
						failed++;
					}
				}
				else {
					failed++;
					log.error("Unrecognized Feature"); //$NON-NLS-1$
				}
			}
			
			switch (type) {
			case reference:
				status.setReferenceFailed(failed);
				break;
			case transformed:
				status.setTransformedFailed(failed);
				break;
			}
			
			MapContext mc = new DefaultMapContext(crs);
			// add normal features
			Style style = ss.getStyle(type);
			for (Entry<SimpleFeatureType, FeatureCollection<SimpleFeatureType, SimpleFeature>> entry : groupedFeatures.entrySet()) {
				mc.addLayer(entry.getValue(), style);
			}
			
			// add selected features
			style = ss.getSelectionStyle(type);
			for (Entry<SimpleFeatureType, FeatureCollection<SimpleFeatureType, SimpleFeature>> entry : selectedFeatures.entrySet()) {
				mc.addLayer(entry.getValue(), style);
			}
			
			return mc;
		}
		else {
			return new DefaultMapContext(crs);
		}
	}

	/**
	 * Validate if the given feature may be added to the layer
	 * 
	 * @param feature the feature
	 * 
	 * @return if the feature may be added to a layer for painting
	 */
	private static boolean validateFeature(SimpleFeature feature) {
		// check if a default geometry exists
		if (feature.getDefaultGeometry() == null) {
			return false;
		}
				
		return true;
	}

}
