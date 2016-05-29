/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.style;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.ui.PlatformUI;
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

import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaService;
import eu.esdihumboldt.hale.ui.style.service.internal.StylePreferences;

/**
 * Style helper methods
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class StyleHelper {

	/**
	 * Default fill opacity
	 */
	public static final double DEFAULT_FILL_OPACITY = 0.4;

	private static final StyleBuilder styleBuilder = new StyleBuilder();

	private static final StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);

	private static final FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);

	/**
	 * Returns a default style for the given type.
	 * 
	 * @param typeDef the type definition
	 * @param dataSet the data set (if known)
	 * @return the style
	 */
	public static FeatureTypeStyle getDefaultStyle(TypeDefinition typeDef,
			@Nullable DataSet dataSet) {
//		GeometrySchemaService gss = (GeometrySchemaService) PlatformUI.getWorkbench().getService(GeometrySchemaService.class);
//		List<QName> geomPath = gss.getDefaultGeometry(typeDef);
		// TODO determine default style from default geometry?

		Color defColor;
		if (dataSet != null) {
			defColor = StylePreferences.getDefaultColor(dataSet);
		}
		else {
			defColor = Color.DARK_GRAY;
		}
		int defWidth = StylePreferences.getDefaultWidth();

		FeatureTypeStyle result;

		// XXX for now create a polygon style in any case, as it contains fill
		// and stroke

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

		// XXX StyleBuilder does not support feature type names with namespace
//		QName name = getFeatureTypeName(typeDef);
//		result.featureTypeNames().add(new NameImpl(name.getNamespaceURI(), name.getLocalPart()));
		result.featureTypeNames().add(new NameImpl(getFeatureTypeName(typeDef)));

		return result;
	}

	/**
	 * Returns a default style for the given type.
	 * 
	 * @param dataSetTypes type definitions associated to their data set
	 * @return the style
	 */
	public static Style getRandomStyles(SetMultimap<DataSet, TypeDefinition> dataSetTypes) {
		int defWidth = StylePreferences.getDefaultWidth();

		Style style = styleFactory.createStyle();

		for (Entry<DataSet, TypeDefinition> entry : dataSetTypes.entries()) {
			DataSet dataSet = entry.getKey();
			TypeDefinition typeDef = entry.getValue();

			FeatureTypeStyle fts;

			// TODO based on default geometry?
			// polygon is always OK as it contains stroke and fill

			// Color color = generateRandomColor(Color.WHITE);
			float saturation;
			float brightness;
			switch (dataSet) {
			case TRANSFORMED:
				saturation = 0.8f;
				brightness = 0.6f;
				break;
			case SOURCE:
			default:
				saturation = 0.75f;
				brightness = 0.8f;
				break;
			}
			Color color = generateRandomColor(saturation, brightness);
			fts = createPolygonStyle(color, defWidth);

			fts.featureTypeNames().add(new NameImpl(getFeatureTypeName(typeDef)));

			style.featureTypeStyles().add(fts);
		}

		return style;
	}

	/**
	 * Returns a default style for the given type.
	 * 
	 * @param dataSetTypes type definitions associated to their data set
	 * @return the style
	 */
	public static Style getSpectrumStyles(SetMultimap<DataSet, TypeDefinition> dataSetTypes) {
		int defWidth = StylePreferences.getDefaultWidth();

		Style style = styleFactory.createStyle();

		GeometrySchemaService gss = PlatformUI.getWorkbench()
				.getService(GeometrySchemaService.class);

		for (DataSet dataSet : dataSetTypes.keySet()) {
			float saturation;
			float brightness;
			switch (dataSet) {
			case TRANSFORMED:
				saturation = 0.8f;
				brightness = 0.6f;
				break;
			case SOURCE:
			default:
				saturation = 0.75f;
				brightness = 0.8f;
				break;
			}

			Set<TypeDefinition> types = new HashSet<>(dataSetTypes.get(dataSet));
			Iterator<TypeDefinition> it = types.iterator();
			while (it.hasNext()) {
				TypeDefinition type = it.next();
				// remove invalid types
				if (type.getConstraint(AbstractFlag.class).isEnabled()
						|| gss.getDefaultGeometry(type) == null) {
					it.remove();
				}
			}

			int numberOfTypes = types.size();
			int index = 0;
			for (TypeDefinition typeDef : types) {
				FeatureTypeStyle fts;

				// TODO based on default geometry?
				// polygon is always OK as it contains stroke and fill

				// Color color = generateRandomColor(Color.WHITE);

				Color color;
				if (numberOfTypes == 1) {
					color = generateRandomColor(saturation, brightness);
				}
				else {
					color = Color.getHSBColor((float) index / (float) numberOfTypes, saturation,
							brightness);
				}
				fts = createPolygonStyle(color, defWidth);

				fts.featureTypeNames().add(new NameImpl(getFeatureTypeName(typeDef)));

				style.featureTypeStyles().add(fts);

				index++;
			}
		}

		return style;
	}

	/**
	 * Generate a random color. Mixing in WHITE will create pastel colors.
	 * Mixing in a pastel color will create tinted colors.
	 * 
	 * @param mix color to mix in (use average of RGB values)
	 * @return the generated color
	 */
	public static Color generateRandomColor(@Nullable Color mix) {
		Random random = new Random();
		int red = random.nextInt(256);
		int green = random.nextInt(256);
		int blue = random.nextInt(256);

		// mix the color
		if (mix != null) {
			red = (red + mix.getRed()) / 2;
			green = (green + mix.getGreen()) / 2;
			blue = (blue + mix.getBlue()) / 2;
		}

		Color color = new Color(red, green, blue);
		return color;
	}

	private static float GOLDEN_RATIO_CONJUGATE = 0.618033988749895f;

	/**
	 * Generate a random color.
	 * 
	 * Inspired by
	 * http://martin.ankerl.com/2009/12/09/how-to-create-random-colors
	 * -programmatically/
	 * 
	 * @param saturation the saturation (between 0.0f and 1.0f)
	 * @param brightness the brightness (between 0.0f and 1.0f)
	 * 
	 * @return the random color
	 */
	public static Color generateRandomColor(float saturation, float brightness) {
		Random random = new Random();

		float rand = random.nextFloat();
		rand = rand + GOLDEN_RATIO_CONJUGATE;
		rand = rand % 1;

		return Color.getHSBColor(rand, saturation, brightness);
	}

	// XXX StyleBuilder does not support feature type names with namespace
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
	 * 
	 * @param typeDef the type definition
	 * @return the feature type name
	 */
	public static String getFeatureTypeName(TypeDefinition typeDef) {
		// type or element name
		return typeDef.getDisplayName();
	}

	/**
	 * Get a style containing the default style for the given type.
	 * 
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
	 * 
	 * @param symbolizer the point symbolizer
	 * @param color the new color
	 * @param width the new line width
	 * @return the mutated symbolizer
	 */
	public static Symbolizer mutatePointSymbolizer(PointSymbolizer symbolizer, Color color,
			int width) {
		// mutate mark
		Mark mark = SLD.mark(symbolizer);

		Mark mutiMark = styleBuilder.createMark(mark.getWellKnownName(),
				styleBuilder.createFill(color, DEFAULT_FILL_OPACITY),
				styleBuilder.createStroke(color, width));

		// XXX commented because unsupported in Geotools 8.0-M1
//		mutiMark.setSize(mark.getSize());
//		mutiMark.setRotation(mark.getRotation());

		// create new symbolizer
		return styleBuilder.createPointSymbolizer(styleBuilder.createGraphic(null, mutiMark, null));
	}

	/**
	 * Manually create a default point style.
	 * 
	 * @param color the point color
	 * @param width the line width
	 * @return a Style for Point objects.
	 */
	@SuppressWarnings("unused")
	private static FeatureTypeStyle createPointStyle(Color color, double width) {
		PointSymbolizer symbolizer = createPointSymbolizer(color, width);
		// symbolizer.getGraphic().setSize(filterFactory.literal(1));
		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(symbolizer);
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.rules().add(rule);
		return fts;
	}

	/**
	 * Create a default point symbolizer.
	 * 
	 * @param color the color
	 * @param width the line width
	 * @return the point symbolizer
	 */
	public static PointSymbolizer createPointSymbolizer(Color color, double width) {
		return styleBuilder.createPointSymbolizer(styleBuilder.createGraphic(null,
				styleBuilder.createMark(StyleBuilder.MARK_X,
						styleBuilder.createFill(color, DEFAULT_FILL_OPACITY),
						styleBuilder.createStroke(color, width)),
				null));
	}

	/**
	 * Create a default line style.
	 * 
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
	 * 
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
	 * 
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
	 * 
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
