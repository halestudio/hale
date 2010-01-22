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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.rcp.utils.FeatureTypeHelper;

/**
 * A default {@link StyleService} implementation that will provide simple styles
 * for Lines, Points and Polygons if none have been loaded from an SLD.
 * 
 * @author Thorsten Reitz, Simon Templer 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class StyleServiceImpl 
	implements StyleService {

	private static final Logger _log = Logger.getLogger(StyleServiceImpl.class);
	
	private static StyleService instance;
	
	private final Map<FeatureType, FeatureTypeStyle> styles;
	
	private final Set<HaleServiceListener> listeners = 
		new HashSet<HaleServiceListener>();
	
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
					FeatureType ft = schemaService.getFeatureTypeByName(fts.getFeatureTypeName());
					if (ft != null) {
						if (addStyle(ft, fts)) {
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
	 * @see StyleService#getStyle(eu.esdihumboldt.hale.models.InstanceService.DatasetType)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Style getStyle(final DatasetType dataset) {
		Collection<FeatureType> types = (dataset == DatasetType.reference)?(schemaService.getSourceSchema()):(schemaService.getTargetSchema());
		
		if (types == null) {
			types = new ArrayList<FeatureType>();
		}
		
		Style style = styleFactory.createStyle();
		
		for (FeatureType type : types) {
			if (!FeatureTypeHelper.isAbstract(type) && 
					!FeatureTypeHelper.isPropertyType(type)) {
				// only add styles for non-abstract feature types
				
				FeatureTypeStyle fts = styles.get(type);
				if (fts == null) {
					fts = getDefaultStyle(type);
				}
				
				style.addFeatureTypeStyle(fts);
			}
		}
		
		return style;
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
		
		if (type != null) {
			if (type.isAssignableFrom(Polygon.class)
					|| type.isAssignableFrom(MultiPolygon.class)) {
				result = createPolygonStyle();
			} else if (type.isAssignableFrom(LineString.class)
					|| type.isAssignableFrom(MultiLineString.class)) {
				result = createLineStyle();
			} else {
				result = createPointStyle();
			}
		}
		else {
			result = createPointStyle();
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
				FeatureType ft = schemaService.getFeatureTypeByName(fts.getFeatureTypeName());
				if (ft != null) {
					if (addStyle(ft, fts)) {
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
	
	// UpdateService methods ...................................................
	
	/**
	 * @see eu.esdihumboldt.hale.models.UpdateService#addListener(eu.esdihumboldt.hale.models.HaleServiceListener)
	 */
	public boolean addListener(HaleServiceListener sl) {
		this.listeners.add(sl);
		return false;
	}

	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	@SuppressWarnings("unchecked")
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			_log.info("Updating a listener.");
			hsl.update(new UpdateMessage(StyleService.class, null)); //FIXME
		}
	}
	
	/**
	 * Manually create a Point Style for a FeatureType. Used methods are going
	 * to be removed in GT 2.6, so has to be updated in case of migration.
	 * @return a Style for Point objects.
	 */
	private static FeatureTypeStyle createPointStyle() {
		PointSymbolizer symbolizer = styleFactory.createPointSymbolizer();
		symbolizer.getGraphic().setSize(filterFactory.literal(1));
		Rule rule = styleFactory.createRule();
		rule.setSymbolizers(new Symbolizer[] { symbolizer });
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.rules().add(rule);
		return fts;
	}

	/**
	 * Manually create a Line Style for a FeatureType. Used methods are going
	 * to be removed in GT 2.6, so has to be updated in case of migration.
	 * @return a Style for Line/LineString objects.
	 */
	private static FeatureTypeStyle createLineStyle() {
		LineSymbolizer symbolizer = styleFactory.createLineSymbolizer();
		SLD.setLineColour(symbolizer, new Color(57, 75, 95));
		symbolizer.getStroke().setWidth(filterFactory.literal(1));
		
		Rule rule = styleFactory.createRule();
		rule.setSymbolizers(new Symbolizer[] { symbolizer });
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.rules().add(rule);
		return fts;
	}

	/**
	 * Manually create a Polygon Style for a FeatureType. Used methods are going
	 * to be removed in GT 2.6, so has to be updated in case of migration.
	 * @return a Style for Polygon objects.
	 */
	private static FeatureTypeStyle createPolygonStyle() {
		PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer();
		SLD.setPolyColour(symbolizer, new Color(57, 75, 95));
		symbolizer.getStroke().setWidth(filterFactory.literal(1));
		Fill fill = styleFactory.createFill(filterFactory.literal("#FFAA00"),
				filterFactory.literal(0.5));
		symbolizer.setFill(fill);
		Rule rule = styleFactory.createRule();
		rule.setSymbolizers(new Symbolizer[] { symbolizer });
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.rules().add(rule);
		return fts;
	}

}
