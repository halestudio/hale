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
package eu.esdihumboldt.hale.models.impl;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

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
import org.geotools.styling.Stroke;
import org.geotools.styling.StrokeImpl;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.StyleService;

/**
 * A default {@link StyleService} implementation that will provide simple styles
 * for Lines, Points and Polygons if none have been loaded from an SLD.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class StyleServiceImpl 
	implements StyleService {
	
	private static Logger _log = Logger.getLogger(StyleServiceImpl.class);
	
	private static StyleService instance = new StyleServiceImpl();
	
	private Map<FeatureType, Style> styles;
	
	private Set<HaleServiceListener> listeners = 
		new HashSet<HaleServiceListener>();
	
	private StyleFactory styleFactory = 
		CommonFactoryFinder.getStyleFactory(null);
	
	private FilterFactory filterFactory = 
		CommonFactoryFinder.getFilterFactory(null);
	
	// Constructor, instance accessor ..........................................
	
	private StyleServiceImpl () {
		this.styles = new HashMap<FeatureType, Style>();
	}
	
	public static StyleService getInstance() {
		return StyleServiceImpl.instance;
	}

	/** 
	 * This implementation will build a simple style if none is defined
	 * previously. 
	 * @see eu.esdihumboldt.hale.models.StyleService#getStyle(org.opengis.feature.type.FeatureType)
	 */
	@SuppressWarnings("unchecked")
	public Style getStyle(FeatureType ft) {
		Style result = null;
		if (this.styles.get(ft) == null) {
			Class type = ft.getGeometryDescriptor().getType().getBinding();
			if (type.isAssignableFrom(Polygon.class)
					|| type.isAssignableFrom(MultiPolygon.class)) {
				return createPolygonStyle();
			} else if (type.isAssignableFrom(LineString.class)
					|| type.isAssignableFrom(MultiLineString.class)) {
				return createLineStyle();
			} else {
				return createPointStyle();
			}
		}
		else {
			result = this.styles.get(ft);
		}
		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.UpdateService#addListener(eu.esdihumboldt.hale.models.HaleServiceListener)
	 */
	public boolean addListener(HaleServiceListener sl) {
		this.listeners.add(sl);
		return false;
	}
	
	/**
	 * @see eu.esdihumboldt.hale.models.StyleService#addStyles(java.net.URL)
	 */
	@SuppressWarnings("deprecation")
	public boolean addStyles(URL url) {
		try {
			Style style = this.initializeStyles(new File(url.toString()));
			for (FeatureTypeStyle fts : style.getFeatureTypeStyles()) {
				_log.debug("" + fts.getFeatureTypeName());
			}
		}
		catch (Exception ex) {
			
		}
		this.updateListeners();
		return false;
	}

	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			_log.info("Updating a listener.");
			hsl.update();
		}
	}
	
	private Style initializeStyles(File sld) {
		SLDParser stylereader;
		try {
			stylereader = new SLDParser(styleFactory, sld.toURL());
			Style[] style = stylereader.readXML();
			return style[0]; // FIXME check whether one or all styles should be returned.
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(0);
		}
		return null;
	}
	
	/**
	 * Manually create a Point Style for a FeatureType. Used methods are going
	 * to be removed in GT 2.6, so has to be updated in case of migration.
	 * @return a Style for Point objects.
	 */
	@SuppressWarnings("deprecation")
	private Style createPointStyle() {
		Style style;
		PointSymbolizer symbolizer = styleFactory.createPointSymbolizer();
		symbolizer.getGraphic().setSize(filterFactory.literal(1));
		Rule rule = styleFactory.createRule();
		rule.setSymbolizers(new Symbolizer[] { symbolizer });
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.setRules(new Rule[] { rule });
		style = styleFactory.createStyle();
		style.addFeatureTypeStyle(fts);
		return style;
	}

	/**
	 * Manually create a Line Style for a FeatureType. Used methods are going
	 * to be removed in GT 2.6, so has to be updated in case of migration.
	 * @return a Style for Line/LineString objects.
	 */
	@SuppressWarnings("deprecation")
	private Style createLineStyle() {
		Style style;
		LineSymbolizer symbolizer = styleFactory.createLineSymbolizer();
		SLD.setLineColour(symbolizer, new Color(57, 75, 95));
		symbolizer.getStroke().setWidth(filterFactory.literal(1));
		symbolizer.getStroke().setColor(filterFactory.literal(
				new Color(57, 75, 95)));

		Rule rule = styleFactory.createRule();
		rule.setSymbolizers(new Symbolizer[] { symbolizer });
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.setRules(new Rule[] { rule });
		style = styleFactory.createStyle();
		style.addFeatureTypeStyle(fts);
		return style;
	}

	/**
	 * Manually create a Polygon Style for a FeatureType. Used methods are going
	 * to be removed in GT 2.6, so has to be updated in case of migration.
	 * @return a Style for Polygon objects.
	 */
	@SuppressWarnings("deprecation")
	private Style createPolygonStyle() {
		Style style;
		PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer();
		Fill fill = styleFactory.createFill(filterFactory.literal("#FFAA00"),
				filterFactory.literal(0.5));
		symbolizer.setFill(fill);
		Rule rule = styleFactory.createRule();
		rule.setSymbolizers(new Symbolizer[] { symbolizer });
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.setRules(new Rule[] { rule });
		style = styleFactory.createStyle();
		style.addFeatureTypeStyle(fts);
		return style;
	}


}
