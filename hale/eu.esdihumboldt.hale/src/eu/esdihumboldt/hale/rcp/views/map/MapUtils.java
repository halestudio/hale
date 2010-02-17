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
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
		if (fc != null) {
			Feature f = fc.features().next();
			if (f.getDefaultGeometryProperty() != null) {
				crs = f.getDefaultGeometryProperty().getDescriptor().getCoordinateReferenceSystem();
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
	 * @return a {@link MapContext} with the given CRS and the 
	 * {@link FeatureCollection} identified by the given {@link DatasetType}.
	 */
	@SuppressWarnings("unchecked")
	public static MapContext buildMapContext(CoordinateReferenceSystem crs, DatasetType type) {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		
		FeatureCollection<?, ?> fc = is.getFeatures(type);
		if (fc != null && fc.size() > 0) {
			if (crs == null) {
				crs = determineCRS(is.getFeatures(DatasetType.reference)); // TODO always use source CRS! (Check whether OK)
			}
			
			log.info("features size: " + fc.size()); //$NON-NLS-1$
			log.info("features bounds: " + fc.getBounds()); //$NON-NLS-1$
			Style style = ss.getStyle(type);
			
			Map<SimpleFeatureType, FeatureCollection<SimpleFeatureType, SimpleFeature>> groupedFeatures = new HashMap<SimpleFeatureType, FeatureCollection<SimpleFeatureType, SimpleFeature>>();
			Iterator it = fc.iterator();
			while (it.hasNext()) {
				Object tmp = it.next();
				
				if (tmp instanceof SimpleFeature) {
					SimpleFeature feature = (SimpleFeature) tmp;
					FeatureCollection<SimpleFeatureType, SimpleFeature> collection = groupedFeatures.get(feature.getFeatureType());
					if (collection == null) {
						collection = new MemoryFeatureCollection(feature.getFeatureType());
						groupedFeatures.put(feature.getFeatureType(), collection);
					}
					collection.add(feature);
				}
				else {
					log.error("Unrecognized Feature"); //$NON-NLS-1$
				}
			}
			
			MapContext mc = new DefaultMapContext(crs);
			for (Entry<SimpleFeatureType, FeatureCollection<SimpleFeatureType, SimpleFeature>> entry : groupedFeatures.entrySet()) {
				mc.addLayer(entry.getValue(), style);
			}
			
			return mc;
		}
		else {
			return new DefaultMapContext(crs);
		}
	}

}
