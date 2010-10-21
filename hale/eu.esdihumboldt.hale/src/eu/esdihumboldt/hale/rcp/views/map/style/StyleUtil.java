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

package eu.esdihumboldt.hale.rcp.views.map.style;

import java.awt.image.BufferedImage;

import org.eclipse.ui.PlatformUI;
import org.geotools.legend.Drawer;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.StyleService;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class StyleUtil {
	
	private static final int WIDTH = 16;
	
	private static final int HEIGHT = 16;
	
	private static final int[] LINE_POINTS = new int[]{0, HEIGHT - 1, WIDTH - 1, 0};

	private static final int[] POLY_POINTS = new int[]{0, 0, WIDTH - 1, 0, WIDTH - 1, HEIGHT - 1, 0, HEIGHT - 1};
	
	/**
	 * Get a legend image for a given feature type
	 * @param type the feature type
	 * @param definedOnly if only for defined styles a image shall be created
	 * @return the legend image or <code>null</code>
	 */
	public static BufferedImage getLegendImage(FeatureType type, boolean definedOnly) {
		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		Style style = (definedOnly)?(ss.getDefinedStyle(type)):(ss.getStyle(type));
		if (style == null) {
			return null;
		}
		
		// create a dummy feature based on the style
		Drawer d = Drawer.create();
		SimpleFeature feature = null;
		Symbolizer[] symbolizers = SLD.symbolizers(style);
		if (symbolizers.length > 0) {
			Symbolizer symbolizer = symbolizers[0];
			
			if (symbolizer instanceof LineSymbolizer) {
				feature = d.feature(d.line(LINE_POINTS));
			}
			else if (symbolizer instanceof PointSymbolizer) {
				feature = d.feature(d.point(WIDTH / 2, HEIGHT / 2));
			}
			if (symbolizer instanceof PolygonSymbolizer) {
				feature = d.feature(d.polygon(POLY_POINTS));
			}
		}
		
		if (feature != null) {
			BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB); 
//				GraphicsEnvironment.getLocalGraphicsEnvironment().
//    				getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(WIDTH, HEIGHT,
//    				Transparency.TRANSLUCENT);
			
//			MapView map = (MapView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MapView.ID);
//			if (map != null) {
//				RGB rgb = map.getPainter().getBackground();
//				Color color = new Color(rgb.red, rgb.green, rgb.blue);
//				Graphics2D g = image.createGraphics();
//				try {
//					g.setColor(color);
//					g.fillRect(0, 0, WIDTH, HEIGHT);
//				} finally {
//					g.dispose();
//				}
//			}
			
			d.drawDirect(image, feature, style);
			return image;
		}
		
		return null;
	}

}
