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

package eu.esdihumboldt.hale.ui.style;

import java.awt.Color;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.NameImpl;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.FilterFactory;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.style.service.internal.StylePreferences;

/**
 * Style helper methods
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class StyleHelper {
	
	private static final int WIDTH = 16;
	
	private static final int HEIGHT = 16;
	
	@SuppressWarnings("unused")
	private static final int[] LINE_POINTS = new int[]{0, HEIGHT - 1, WIDTH - 1, 0};

	@SuppressWarnings("unused")
	private static final int[] POLY_POINTS = new int[]{0, 0, WIDTH - 1, 0, WIDTH - 1, HEIGHT - 1, 0, HEIGHT - 1};
	
	/**
	 * Default fill opacity
	 */
	public static final double DEFAULT_FILL_OPACITY = 0.3;
	
	private static final StyleBuilder styleBuilder = new StyleBuilder();
	
	private static final StyleFactory styleFactory = 
		CommonFactoryFinder.getStyleFactory(null);
	
	private static final FilterFactory filterFactory = 
		CommonFactoryFinder.getFilterFactory(null);
	
//	/**
//	 * Get a legend image for a given feature type
//	 * @param type the feature type
//	 * @param definedOnly if only for defined styles a image shall be created
//	 * @return the legend image or <code>null</code>
//	 */
//	public static BufferedImage getLegendImage(FeatureType type, boolean definedOnly) {
//		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
//		Style style = (definedOnly)?(ss.getDefinedStyle(type)):(ss.getStyle(type));
//		if (style == null) {
//			return null;
//		}
//		
//		// create a dummy feature based on the style
//		Drawer d = Drawer.create();
//		SimpleFeature feature = null;
//		Symbolizer[] symbolizers = SLD.symbolizers(style);
//		if (symbolizers.length > 0) {
//			Symbolizer symbolizer = symbolizers[0];
//			
//			if (symbolizer instanceof LineSymbolizer) {
//				feature = d.feature(d.line(LINE_POINTS));
//			}
//			else if (symbolizer instanceof PointSymbolizer) {
//				feature = d.feature(d.point(WIDTH / 2, HEIGHT / 2));
//			}
//			if (symbolizer instanceof PolygonSymbolizer) {
//				feature = d.feature(d.polygon(POLY_POINTS));
//			}
//		}
//		
//		if (feature != null) {
//			BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB); 
////				GraphicsEnvironment.getLocalGraphicsEnvironment().
////    				getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(WIDTH, HEIGHT,
////    				Transparency.TRANSLUCENT);
//			
//			RGB rgb = ss.getBackground();
//			Color color = new Color(rgb.red, rgb.green, rgb.blue);
//			Graphics2D g = image.createGraphics();
//			try {
//				g.setColor(color);
//				g.fillRect(0, 0, WIDTH, HEIGHT);
//			} finally {
//				g.dispose();
//			}
//			
//			d.drawDirect(image, feature, style);
//			return image;
//		}
//		
//		return null;
//	}
	
	/**
	 * Returns a default style for the given type.
	 * @param typeDef the type definition
	 * @param dataSet the data set
	 * @return the style
	 */
	public static FeatureTypeStyle getDefaultStyle(TypeDefinition typeDef, DataSet dataSet) {
//		GeometrySchemaService gss = (GeometrySchemaService) PlatformUI.getWorkbench().getService(GeometrySchemaService.class);
//		List<QName> geomPath = gss.getDefaultGeometry(typeDef);
		//TODO determine default style from default geometry?
		
		Color defColor = StylePreferences.getDefaultColor(dataSet);
		int defWidth = StylePreferences.getDefaultWidth();
		
		FeatureTypeStyle result;
		
		//XXX for now create a polygon style in any case, as it contains fill and stroke
		
//		if (type != null) {
//			if (type.isAssignableFrom(Polygon.class)
//					|| type.isAssignableFrom(MultiPolygon.class)) {
				result = createPolygonStyle(defColor, defWidth);
//			} else if (type.isAssignableFrom(LineString.class)
//					|| type.isAssignableFrom(MultiLineString.class)) {
//				result = createLineStyle(defColor, defWidth);
//			} else {
//				result = createPointStyle(defColor, defWidth);
//			}
//		}
//		else {
//			result = createPointStyle(defColor, defWidth);
//		}
		
		//XXX StyleBuilder does not support feature type names with namespace
//		QName name = getFeatureTypeName(typeDef);
//		result.featureTypeNames().add(new NameImpl(name.getNamespaceURI(), name.getLocalPart()));
		result.featureTypeNames().add(new NameImpl(getFeatureTypeName(typeDef)));
		
		return result;
	}
	
	//XXX StyleBuilder does not support feature type names with namespace
//	/**
//	 * Get the name used in styles for the given type definition.
//	 * @param typeDef the type definition
//	 * @return the feature type name
//	 */
//	public static QName getFeatureTypeName(TypeDefinition typeDef) {
//		// default to element name
//		Collection<? extends XmlElement> elements = typeDef.getConstraint(XmlElements.class).getElements();
//		if (elements.size() == 1) {
//			// only use element name if it is unique
//			return elements.iterator().next().getName();
//		}
//		
//		// type
//		return typeDef.getName();
//	}
	
	/**
	 * Get the name used in styles for the given type definition.
	 * @param typeDef the type definition
	 * @return the feature type name
	 */
	public static String getFeatureTypeName(TypeDefinition typeDef) {
		// type or element name
		return typeDef.getDisplayName();
	}

	/**
	 * Get a style containing the default style for the given type.
	 * @param type the type definition
	 * @param dataSet the data set
	 * @return the style with the default type style
	 */
	public static Style getStyle(TypeDefinition type, DataSet dataSet) {
		Style style = styleFactory.createStyle();
		
		style.featureTypeStyles().add(getDefaultStyle(type, dataSet));
		
		return style;
	}
	
	/**
	 * Create a new point symbolizer based on the given one.
	 * @param symbolizer the point symbolizer
	 * @param color the new color
	 * @param width the new line width
	 * @return the mutated symbolizer
	 */
	public static Symbolizer mutatePointSymbolizer(PointSymbolizer symbolizer,
			Color color, int width) {
		// mutate mark
		Mark mark = SLD.mark(symbolizer);
		
		Mark mutiMark = styleBuilder.createMark(mark.getWellKnownName(), 
				styleBuilder.createFill(color, DEFAULT_FILL_OPACITY), 
				styleBuilder.createStroke(color, width));
		
		//XXX commented because unsupported in Geotools 8.0-M1
//		mutiMark.setSize(mark.getSize());
//		mutiMark.setRotation(mark.getRotation());
		
		// create new symbolizer
		return styleBuilder.createPointSymbolizer(styleBuilder.createGraphic(
				null, mutiMark, null));
	}
	
	/**
	 * Manually create a default point style.
	 * @param color the point color
	 * @param width the line width
	 * @return a Style for Point objects.
	 */
	@SuppressWarnings("unused")
	private static FeatureTypeStyle createPointStyle(Color color, double width) {
		PointSymbolizer symbolizer = createPointSymbolizer(color, width);
		//symbolizer.getGraphic().setSize(filterFactory.literal(1));
		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(symbolizer);
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.rules().add(rule);
		return fts;
	}

	/**
	 * Create a default point symbolizer.
	 * @param color the color
	 * @param width the line width
	 * @return the point symbolizer
	 */
	public static PointSymbolizer createPointSymbolizer(Color color, double width) {
		return styleBuilder.createPointSymbolizer(styleBuilder.createGraphic(
				null, styleBuilder.createMark(StyleBuilder.MARK_X, styleBuilder.createFill(color, DEFAULT_FILL_OPACITY), 
						styleBuilder.createStroke(color, width)), null));
	}

	/**
	 * Create a default line style.
	 * @param color the line color
	 * @param width the line width
	 * @return a Style for Line/LineString objects.
	 */
	@SuppressWarnings("unused")
	private static FeatureTypeStyle createLineStyle(Color color, double width) {
		LineSymbolizer symbolizer = createLineSymbolizer(color, width);
		
		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(symbolizer);
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.rules().add(rule);
		return fts;
	}

	/**
	 * Create a default line symbolizer.
	 * @param color the color
	 * @param width the line width
	 * @return the line symbolizer
	 */
	public static LineSymbolizer createLineSymbolizer(Color color, double width) {
		LineSymbolizer symbolizer = styleFactory.createLineSymbolizer();
		SLD.setLineColour(symbolizer, color);
		symbolizer.getStroke().setWidth(filterFactory.literal(width));
		return symbolizer;
	}

	/**
	 * Create a default polygon style.
	 * @param color the polygon color
	 * @param width the line width
	 * @return a Style for Polygon objects
	 */
	private static FeatureTypeStyle createPolygonStyle(Color color, double width) {
		PolygonSymbolizer symbolizer = createPolygonSymbolizer(color, width);
		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(symbolizer);
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.rules().add(rule);
		return fts;
	}

	/**
	 * Create a default polygon symbolizer.
	 * @param color the color
	 * @param width the line width
	 * @return the polygon symbolizer
	 */
	public static PolygonSymbolizer createPolygonSymbolizer(Color color, double width) {
		PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer();
		SLD.setPolyColour(symbolizer, color);
		symbolizer.getStroke().setWidth(filterFactory.literal(width));
		Fill fill = styleFactory.createFill(filterFactory.literal(color),
				filterFactory.literal(DEFAULT_FILL_OPACITY));
		symbolizer.setFill(fill);
		return symbolizer;
	}

}
