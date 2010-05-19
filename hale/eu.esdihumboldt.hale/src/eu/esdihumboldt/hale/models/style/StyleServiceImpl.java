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
package eu.esdihumboldt.hale.models.style;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.models.AbstractUpdateService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * A default {@link StyleService} implementation that will provide simple styles
 * for Lines, Points and Polygons if none have been loaded from an SLD.
 * 
 * @author Thorsten Reitz, Simon Templer 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class StyleServiceImpl extends AbstractUpdateService
	implements StyleService {

	private static final Logger _log = Logger.getLogger(StyleServiceImpl.class);
	
	private static StyleService instance;
	
	private final Map<FeatureType, FeatureTypeStyle> styles;
	
	private static final StyleBuilder styleBuilder = new StyleBuilder();
	
	private static final StyleFactory styleFactory = 
		CommonFactoryFinder.getStyleFactory(null);
	
	private static final FilterFactory filterFactory = 
		CommonFactoryFinder.getFilterFactory(null);
	
	private final SchemaService schemaService;

	/**
	 * Queued styles
	 */
	private final Queue<FeatureTypeStyle> queuedStyles = new LinkedList<FeatureTypeStyle>();
	
	// Constructor, instance accessor ..........................................
	
	private StyleServiceImpl (SchemaService schema) {
		styles = new HashMap<FeatureType, FeatureTypeStyle>();
		
		schemaService = schema;
		
		// add listener to process queued styles
		schema.addListener(new HaleServiceListener() {
			
			@SuppressWarnings({ "unchecked", "deprecation" })
			@Override
			public void update(UpdateMessage message) {
				Collection<FeatureTypeStyle> failures = new ArrayList<FeatureTypeStyle>();
				boolean updateNeeded = false;
				
				while (!queuedStyles.isEmpty()) {
					FeatureTypeStyle fts = queuedStyles.poll();
					SchemaElement element = schemaService.getElementByName(fts.getFeatureTypeName());
					if (element != null && element.getFeatureType() != null) {
						if (addStyle(element.getFeatureType(), fts)) {
							updateNeeded = true;
						}
					}
					else {
						failures.add(fts);
					}
				}
				
				queuedStyles.addAll(failures);
				
				if (updateNeeded) {
					updateListeners();
				}
			}
		});
	}
	
	/**
	 * Get the style service instance
	 * 
	 * @param schema the schema service 
	 * 
	 * @return the style service instance
	 */
	public static StyleService getInstance(SchemaService schema) {
		if (instance == null) {
			instance = new StyleServiceImpl(schema);
		}
		
		return instance;
	}
	
	// StyleService methods ....................................................
	
	/**
	 * @see StyleService#getNamedStyle(String)
	 */
	@SuppressWarnings("deprecation")
	public Style getNamedStyle(String name) {
		Style style = styleFactory.createStyle();
		for (FeatureTypeStyle fts : this.styles.values()) {
			//XXX checks for the FeatureTypeStyle name instead of the UserStyle name
			if (fts.getName().equals(name)) {
				style = styleFactory.createStyle();
				style.addFeatureTypeStyle(fts);
				break;
			}
		}
		return style;
	}

	/** 
	 * This implementation will build a simple style if none is defined
	 * previously. 
	 * @see eu.esdihumboldt.hale.models.StyleService#getStyle(org.opengis.feature.type.FeatureType)
	 */
	@SuppressWarnings("deprecation")
	public Style getStyle(FeatureType ft) {
		FeatureTypeStyle fts = styles.get(ft);
		Style style = styleFactory.createStyle();
		if (fts != null) {
			style.addFeatureTypeStyle(fts);
		}
		else {
			style.addFeatureTypeStyle(getDefaultStyle(ft));
		}
		return style;
	}
	
	/**
	 * @see StyleService#getStyle()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Style getStyle() {
		Style style = styleFactory.createStyle();
		
		for (FeatureTypeStyle fts : styles.values()) {
			style.addFeatureTypeStyle(fts);
		}
		
		return style;
	}

	/**
	 * @see StyleService#getStyle(DatasetType)
	 */
	@Override
	public Style getStyle(final DatasetType dataset) {
		return getStyle(dataset, false);
	}

	/**
	 * @see StyleService#getSelectionStyle(DatasetType)
	 */
	@Override
	public Style getSelectionStyle(DatasetType type) {
		return getStyle(type, true);
	}
	
	@SuppressWarnings("deprecation")
	private Style getStyle(final DatasetType dataset, boolean selected) {
		Collection<SchemaElement> elements = (dataset == DatasetType.reference)?(schemaService.getSourceSchema()):(schemaService.getTargetSchema());
		
		if (elements == null) {
			elements = new ArrayList<SchemaElement>();
		}
		
		Style style = styleFactory.createStyle();
		
		for (SchemaElement element : elements) {
			if (!element.getType().isAbstract() && element.getType().isFeatureType()) {
				// only add styles for non-abstract feature types
				FeatureType ft = element.getFeatureType();
				
				FeatureTypeStyle fts = styles.get(ft);
				if (fts == null) {
					fts = getDefaultStyle(ft);
				}
				if (selected) {
					fts = getSelectedStyle(fts);
				}
				
				style.addFeatureTypeStyle(fts);
			}
		}
		
		return style;
	}

	/**
	 * Convert the given style for selection
	 * 
	 * @param fts the feature type style to convert
	 * 
	 * @return the converted feature type style
	 */
	@SuppressWarnings("deprecation")
	private FeatureTypeStyle getSelectedStyle(FeatureTypeStyle fts) {
		List<Rule> rules = fts.rules();
		
		List<Rule> newRules = new ArrayList<Rule>();
		
		for (Rule rule : rules) {
			Symbolizer[] symbolizers = rule.getSymbolizers();
			List<Symbolizer> newSymbolizers = new ArrayList<Symbolizer>();
			
			for (Symbolizer symbolizer : symbolizers) {
				// get symbolizers
				List<Symbolizer> addSymbolizers = getSelectionSymbolizers(symbolizer);
				if (addSymbolizers != null) {
					newSymbolizers.addAll(addSymbolizers);
				}
			}

			// create new rule
			Rule newRule = styleBuilder.createRule(newSymbolizers.toArray(new Symbolizer[newSymbolizers.size()]));
			newRule.setFilter(rule.getFilter());
			newRule.setIsElseFilter(rule.hasElseFilter());
			newRule.setName(rule.getName());
			newRules.add(newRule);
		}
		
		return  styleBuilder.createFeatureTypeStyle(fts.getFeatureTypeName(), 
				newRules.toArray(new Rule[newRules.size()]));
	}

	/**
	 * Get the symbolizers representing the given symbolizer for a selection
	 * 
	 * @param symbolizer the symbolizer
	 * 
	 * @return the selection symbolizers
	 */
	private List<Symbolizer> getSelectionSymbolizers(Symbolizer symbolizer) {
		List<Symbolizer> result = new ArrayList<Symbolizer>();
		
		Color color = StylePreferences.getSelectionColor();
		int width = StylePreferences.getSelectionWidth();
		
		if (symbolizer instanceof PolygonSymbolizer) {
			result.add(createPolygonSymbolizer(color, width));
		}
		else if (symbolizer instanceof LineSymbolizer) {
			result.add(createLineSymbolizer(color, width)); 
		}
		else if (symbolizer instanceof PointSymbolizer) {
			result.add(mutateSymbolizer((PointSymbolizer) symbolizer, color, width));
			//result.add(createPointSymbolizer(color, width));
		}
		else {
			// do not fall-back to original symbolizer cause we are painting over it
			//result.add(symbolizer);
		}
		
		return result;
	}

	private Symbolizer mutateSymbolizer(PointSymbolizer symbolizer,
			Color color, int width) {
		// mutate mark
		Mark mark = SLD.mark(symbolizer);
		
		Mark mutiMark = styleBuilder.createMark(mark.getWellKnownName(), 
				styleBuilder.createFill(color), 
				styleBuilder.createStroke(color, width));
		mutiMark.setSize(mark.getSize());
		mutiMark.setRotation(mark.getRotation());
		
		// create new symbolizer
		return styleBuilder.createPointSymbolizer(styleBuilder.createGraphic(
				null, mutiMark, null));
	}

	/**
	 * Returns a default style for the given feature type
	 * 
	 * @param ft the feature type
	 * @return the style
	 */
	public static FeatureTypeStyle getDefaultStyle(FeatureType ft) {
		FeatureType current = ft;
		Class<?> type = null;
		
		// find geometry type
		while (type == null && current != null) {
			GeometryDescriptor gd = current.getGeometryDescriptor();
			if (gd != null) {
				type = gd.getType().getBinding();
			}
			
			if (current.getSuper() instanceof FeatureType) {
				current = (FeatureType) current.getSuper();
			}
			else {
				current = null;
			}
		}
		
		FeatureTypeStyle result;
		
		Color defColor = StylePreferences.getDefaultColor();
		int defWidth = StylePreferences.getDefaultWidth();
		
		if (type != null) {
			if (type.isAssignableFrom(Polygon.class)
					|| type.isAssignableFrom(MultiPolygon.class)) {
				result = createPolygonStyle(defColor, defWidth);
			} else if (type.isAssignableFrom(LineString.class)
					|| type.isAssignableFrom(MultiLineString.class)) {
				result = createLineStyle(defColor, defWidth);
			} else {
				result = createPointStyle(defColor, defWidth);
			}
		}
		else {
			result = createPointStyle(defColor, defWidth);
		}
		
		result.setFeatureTypeName(ft.getName().getLocalPart());
		
		return result;
	}
	
	/**
	 * @see StyleService#addStyles(Style[])
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void addStyles(Style... styles) {
		boolean somethingHappened = false;
		
		for (Style style : styles) {
			for (FeatureTypeStyle fts : style.getFeatureTypeStyles()) {
				SchemaElement element = schemaService.getElementByName(fts.getFeatureTypeName());
				if (element != null && element.getFeatureType() != null) {
					if (addStyle(element.getFeatureType(), fts)) {
						somethingHappened = true;
					}
				}
				else {
					/*
					 * store for later schema update when
					 * feature type might be present
					 */
					queuedStyles.add(fts);
				}
			}
		}
		
		if (somethingHappened) {
			this.updateListeners();
		}
	}

	/**
	 * Add a feature type style
	 * 
	 * @param ft the feature type
	 * @param fts the feature type style
	 * 
	 * @return if the style definitions were changed
	 */
	private boolean addStyle(FeatureType ft, FeatureTypeStyle fts) {
		boolean somethingHappened = false;
		FeatureTypeStyle old = this.styles.get(ft);
		if (old != null) {
			if (!old.equals(fts)) {
				_log.info("Replacing style for feature type " + ft.getName());
				somethingHappened = true;
			}
		}
		else {
			_log.info("Adding style for feature type " + ft.getName());
			somethingHappened = true;
		}
		
		this.styles.put(ft, fts);
		return somethingHappened;
	}

	/**
	 * @see StyleService#addStyles(URL)
	 */
	public boolean addStyles(URL url) {
		SLDParser stylereader;
		try {
			stylereader = new SLDParser(styleFactory, url);
			Style[] styles = stylereader.readXML();
			
			addStyles(styles);
			
			return true;
		} catch (Exception e) {
			_log.error("Error reading styled layer descriptor", e);
			return false;
		}
	}
	
	/**
	 * @see StyleService#clearStyles()
	 */
	@Override
	public void clearStyles() {
		queuedStyles.clear();
		styles.clear();
		
		updateListeners();
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	private void updateListeners() {
		notifyListeners(new UpdateMessage<Object>(StyleService.class, null));
	}
	
	/**
	 * Manually create a Point Style for a FeatureType. Used methods are going
	 * to be removed in GT 2.6, so has to be updated in case of migration.
	 * @param color the point color
	 * @param width 
	 * @return a Style for Point objects.
	 */
	private static FeatureTypeStyle createPointStyle(Color color, double width) {
		PointSymbolizer symbolizer = createPointSymbolizer(color, width);
		//symbolizer.getGraphic().setSize(filterFactory.literal(1));
		Rule rule = styleFactory.createRule();
		rule.setSymbolizers(new Symbolizer[] { symbolizer });
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.rules().add(rule);
		return fts;
	}

	private static PointSymbolizer createPointSymbolizer(Color color, double width) {
		return styleBuilder.createPointSymbolizer(styleBuilder.createGraphic(
				null, styleBuilder.createMark(StyleBuilder.MARK_X, styleBuilder.createFill(color), 
						styleBuilder.createStroke(color, width)), null));
	}

	/**
	 * Manually create a Line Style for a FeatureType. Used methods are going
	 * to be removed in GT 2.6, so has to be updated in case of migration.
	 * @param color the line color
	 * @param width 
	 * @return a Style for Line/LineString objects.
	 */
	private static FeatureTypeStyle createLineStyle(Color color, double width) {
		LineSymbolizer symbolizer = createLineSymbolizer(color, width);
		
		Rule rule = styleFactory.createRule();
		rule.setSymbolizers(new Symbolizer[] { symbolizer });
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.rules().add(rule);
		return fts;
	}

	private static LineSymbolizer createLineSymbolizer(Color color, double width) {
		LineSymbolizer symbolizer = styleFactory.createLineSymbolizer();
		SLD.setLineColour(symbolizer, color);
		symbolizer.getStroke().setWidth(filterFactory.literal(width));
		return symbolizer;
	}

	/**
	 * Manually create a Polygon Style for a FeatureType. Used methods are going
	 * to be removed in GT 2.6, so has to be updated in case of migration.
	 * @param color the polygon color
	 * @param width 
	 * @return a Style for Polygon objects.
	 */
	private static FeatureTypeStyle createPolygonStyle(Color color, double width) {
		PolygonSymbolizer symbolizer = createPolygonSymbolizer(color, width);
		Rule rule = styleFactory.createRule();
		rule.setSymbolizers(new Symbolizer[] { symbolizer });
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.rules().add(rule);
		return fts;
	}

	private static PolygonSymbolizer createPolygonSymbolizer(Color color, double width) {
		PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer();
		SLD.setPolyColour(symbolizer, color);
		symbolizer.getStroke().setWidth(filterFactory.literal(width));
		Fill fill = styleFactory.createFill(filterFactory.literal(color),
				filterFactory.literal(0.3));
		symbolizer.setFill(fill);
		return symbolizer;
	}

}
