/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.StyleService;

/**
 * This renderer renders the instances provided by the {@link InstanceService}
 * according to the defined styles. It supports several splitting and overlay 
 * styles to make it possible to visually compare data before and after 
 * transformation.
 * 
 * For efficient updating during the transformation definition, the renderer 
 * actually maintains a subrenderer per FeatureType.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SplitRenderer {
	
	private static Logger _log = Logger.getLogger(SplitRenderer.class);
	
	private SplitStyle splitStyle = SplitStyle.VERTICAL;
	
	private StyleService styleService = null;
	
	private StreamingRenderer renderer;
	
	private ReferencedEnvelope mapArea;
	
	private Rectangle paintArea;
	
	/**
	 * Default minimum constructor. References to {@link StyleService} and
	 * {@link InstanceService} are required since the renderer needs to update
	 * on changes.
	 * @param is
	 * @param ss
	 */
	public SplitRenderer(StyleService ss){
		super();
		this.styleService = ss;
		this.paintArea = new Rectangle();
		this.configureRenderer();
	}
	
	/**
	 * Renders the given Set of {@link Feature} objects. If there are Features
	 * already managed by the renderer that have the same {@link FeatureType},
	 * as well as the same ID, they will be removed.
	 * 
	 * @param ft
	 * @param features
	 */
	public BufferedImage renderFeatures(FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		if (fc == null) {
			_log.warn("No FeatureCollection object was provided to the " +
					"renderer.");
			return null;
		}
		// prepare the passed Feature objects for rendering
		Feature f = fc.features().next();
		CoordinateReferenceSystem crs = 
			f.getDefaultGeometryProperty().getDescriptor().getCoordinateReferenceSystem();
		
		if (crs == null) {
			crs = fc.getSchema().getCoordinateReferenceSystem();
		}
		
		if (crs == null) {
			try {
				_log.warn("Retrieving the CRS from the schema and the "
						+ "instance data failed; defaulting to EPSG:31251."); 
				crs = CRS.decode("EPSG:31251");
			} catch (Exception e) {
				_log.error("Decoding the default CRS failed, no accurate " +
						"projection will be shown", e);
			}
		}
		
		MapContext mapContext = new DefaultMapContext(crs);

		Style style = this.styleService.getStyle(fc.getSchema());
		
        mapContext.addLayer(
        		(FeatureCollection<SimpleFeatureType, SimpleFeature>) fc, style);
        this.renderer.setContext(mapContext);

        BufferedImage image = new BufferedImage(paintArea.width, paintArea.height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(126, 166, 210));
        graphics.fillRect(
        		paintArea.x, paintArea.y, paintArea.width, paintArea.height);
        this.renderer.paint((Graphics2D) graphics, paintArea, mapArea);
        
        // write image to file for testing purposes
		/*java.io.File file = new java.io.File("C:\\GetElevationProfiletest.png");
		try {
			ImageIO.write(image, "PNG", file);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
        
        return image;

	}
	
	private void configureRenderer() {
		this.renderer = new StreamingRenderer();
		Map hints = new HashMap();
        if (renderer instanceof StreamingRenderer) {
            hints = renderer.getRendererHints();
            if (hints == null) {
                hints = new HashMap();
            }
            /*if (hints.containsKey(StreamingRenderer.LABEL_CACHE_KEY)) {
                labelCache = (LabelCache) hints
                        .get(StreamingRenderer.LABEL_CACHE_KEY);
            } else {
                hints.put(StreamingRenderer.LABEL_CACHE_KEY, labelCache);
            }*/
            renderer.setRendererHints(hints);
        }
        hints.put("memoryPreloadingEnabled", Boolean.FALSE);
        this.renderer.setRendererHints(hints);
    }
	
	/**
	 * Remove all Features of the given {@link FeatureType} from the renderer.
	 * @param ft
	 */
	public void clearFeatures(FeatureType ft) {
		
	}
	
    public ReferencedEnvelope getMapArea() {
        return this.mapArea;
    }

    public void setMapArea(ReferencedEnvelope mapArea) {
        this.mapArea = mapArea;
    }
	
	public void setSplitStyle(SplitStyle spst) {
		this.splitStyle = spst;
	}
	
	public SplitStyle getSplitStyle() {
		return this.splitStyle;
	}
	
	/**
	 * @return the paintArea
	 */
	public Rectangle getPaintArea() {
		return paintArea;
	}

	/**
	 * @param paintArea the paintArea to set
	 */
	public void setPaintArea(Rectangle paintArea) {
		this.paintArea = paintArea;
	}

	public enum SplitStyle {
		NONE,
		HORIZONTAL,
		VERTICAL,
		DIAGONAL_UP,
		DIAGONAL_DOWN,
		OVERLAY
	}

}
