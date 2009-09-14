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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.PlatformUI;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.referencing.CRS;
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
	
	private static final String epsg31251wkt = 
		"PROJCS[\"MGI (Ferro)/AustriaGKWestZone\"," +
			"GEOGCS[\"MGI (Ferro)\"," +
				"DATUM[\"Militar-Geographische Institut (Ferro)\"," +
					"SPHEROID[\"Bessel 1841\",6377397.155,299.1528128," +
					"AUTHORITY[\"EPSG\",\"7004\"]],AUTHORITY[\"EPSG\",\"6805\"]]," +
				"PRIMEM[\"Ferro\",-17.666666666666668,AUTHORITY[\"EPSG\",\"8909\"]]," +
				"UNIT[\"degree\",0.017453292519943295]," +
				"AXIS[\"Geodetic latitude\",NORTH]," +
				"AXIS[\"Geodetic longitude\",EAST]," +
				"AUTHORITY[\"EPSG\",\"4805\"]]," +
			"PROJECTION[\"Transverse Mercator\"]," +
		"PARAMETER[\"central_meridian\",28.0]," +
		"PARAMETER[\"latitude_of_origin\",0.0]," +
		"PARAMETER[\"scale_factor\",1.0]," +
		"PARAMETER[\"false_easting\",0.0]," +
		"PARAMETER[\"false_northing\",-5000000.0]," +
		"UNIT[\"m\",1.0]," +
		"AXIS[\"Y\",EAST]," +
		"AXIS[\"X\",NORTH]," +
		"AUTHORITY[\"EPSG\",\"31251\"]]";
	
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
			crs = f.getDefaultGeometryProperty().getDescriptor().getCoordinateReferenceSystem();
		}
		
		// then check the schema.
		if (crs == null && fc != null) {
			crs = fc.getSchema().getCoordinateReferenceSystem();
		}
		
		// if none is available, use a default.
		if (crs == null) {
			try {
				log.warn("Retrieving the CRS from the schema and the "
						+ "instance data failed; defaulting to EPSG:31251."); 
				crs = CRS.parseWKT(epsg31251wkt);
			} catch (Exception e) {
				log.error("Decoding the default CRS failed, no accurate " +
						"projection will be shown", e);
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
		
		if (crs == null) {
			crs = determineCRS(is.getFeatures(type));
		}
		
		MapContext mc = new DefaultMapContext(crs); 
		FeatureCollection<?, ?> fc = is.getFeatures(type);
		if (fc != null) {
			log.info("features size: " + fc.size());
			log.info("features bounds: " + fc.getBounds());
			Style style = ss.getStyle(type); //fc.getSchema());
			mc.addLayer(
	        		(FeatureCollection<SimpleFeatureType, SimpleFeature>) fc, style);
		}
		return mc;
	}

}
