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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
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
	 * @param resetCustomCRS reset the user CRS
	 * 
	 * @return the coordinate reference system or null
	 */
	private static CoordinateReferenceSystem determineCRS(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc, boolean resetCustomCRS) {
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
			final Display display = Display.getCurrent();
			
			SelectCRSDialog dialog = new SelectCRSDialog(display.getActiveShell());
			while (crs == null) {
				if (!dialog.determineCRS(resetCustomCRS)) {
					return null; //XXX unable to determine CRS
				}
				else {
					crs = dialog.getValue();
				}
			}
		}
		return crs;
	}
	
	/**
	 * @param crs the {@link CoordinateReferenceSystem} to use.
	 * @param type the {@link DatasetType} to render.
	 * @param resetCustomCRS reset the user CRS
	 * @return a {@link MapContext} with the given CRS and the 
	 * {@link FeatureCollection} identified by the given {@link DatasetType}.
	 */
	@SuppressWarnings("unchecked")
	public static MapContext buildMapContext(CoordinateReferenceSystem crs, DatasetType type, boolean resetCustomCRS) {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		
		FeatureCollection<?, ?> fc = is.getFeatures(type);
		if (fc != null && fc.size() > 0) {
			if (crs == null) {
				crs = determineCRS(is.getFeatures(type), resetCustomCRS);
			}
			
			log.info("features size: " + fc.size());
			log.info("features bounds: " + fc.getBounds());
			Style style = ss.getStyle(type); //fc.getSchema());
			
			MapContext mc = new DefaultMapContext(crs); 
			mc.addLayer(
	        		(FeatureCollection<SimpleFeatureType, SimpleFeature>) fc, style);
			
			return mc;
		}
		else {
			return new DefaultMapContext(crs);
		}
	}

}
