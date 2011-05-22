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
package eu.esdihumboldt.hale.ui.style.service.internal;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.type.FeatureType;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.DefinitionUtil;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.HaleServiceListener;
import eu.esdihumboldt.hale.ui.service.UpdateMessage;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService.DatasetType;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.style.helper.StyleHelper;
import eu.esdihumboldt.hale.ui.style.internal.InstanceStylePlugin;
import eu.esdihumboldt.hale.ui.style.service.StyleService;

/**
 * A default {@link StyleService} implementation that will provide simple styles
 * for Lines, Points and Polygons if none have been loaded from an SLD.
 * 
 * @author Thorsten Reitz, Simon Templer 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StyleServiceImpl extends AbstractStyleService {

	private static final ALogger _log = ALoggerFactory.getLogger(StyleServiceImpl.class);

	private static StyleService instance;
	
	private final Map<FeatureType, FeatureTypeStyle> styles;
	
	private final SchemaService schemaService;

	/**
	 * Queued styles
	 */
	private final Queue<FeatureTypeStyle> queuedStyles = new LinkedList<FeatureTypeStyle>();
	
	private static final StyleBuilder styleBuilder = new StyleBuilder();
	
	private static final StyleFactory styleFactory = 
		CommonFactoryFinder.getStyleFactory(null);

	private RGB background = null;
	
	// Constructor, instance accessor ..........................................
	
	private StyleServiceImpl (SchemaService schema) {
		styles = new HashMap<FeatureType, FeatureTypeStyle>();
		
		schemaService = schema;
		
		// add listener to process queued styles
		schema.addListener(new HaleServiceListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void update(@SuppressWarnings("rawtypes") UpdateMessage message) {
				Collection<FeatureTypeStyle> failures = new ArrayList<FeatureTypeStyle>();
				boolean updateNeeded = false;
				
				while (!queuedStyles.isEmpty()) {
					FeatureTypeStyle fts = queuedStyles.poll();
					Definition element = schemaService.getTypeByName(fts.getFeatureTypeName());
					if (element != null && DefinitionUtil.getFeatureType(element) != null) {
						if (addStyle(DefinitionUtil.getFeatureType(element), fts)) {
							updateNeeded = true;
						}
					}
					else {
						failures.add(fts);
					}
				}
				
				queuedStyles.addAll(failures);
				
				if (updateNeeded) {
					notifyStylesAdded();
				}
			}
		});
		
		// listen to style preference changes
		IPreferenceStore prefStore = InstanceStylePlugin.getDefault().getPreferenceStore();
		prefStore.addPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				String property = event.getProperty();
				if (StylePreferences.ALL_KEYS.contains(property)) {
					// settings changed
					notifySettingsChanged();
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
	@Override
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
	 * @see StyleService#getStyle(FeatureType)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public Style getStyle(FeatureType ft) {
		FeatureTypeStyle fts = styles.get(ft);
		Style style = styleFactory.createStyle();
		if (fts != null) {
			style.addFeatureTypeStyle(fts);
		}
		else {
			style.addFeatureTypeStyle(StyleHelper.getDefaultStyle(ft));
		}
		return style;
	}
	
	/**
	 * @see StyleService#getDefinedStyle(FeatureType)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public Style getDefinedStyle(FeatureType ft) {
		FeatureTypeStyle fts = styles.get(ft);
		if (fts != null) {
			Style style = styleFactory.createStyle();
			style.addFeatureTypeStyle(fts);
			return style;
		}
		else {
			return null;
		}
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
		Map<Definition, FeatureType> elements = (dataset == DatasetType.source)?(schemaService.getSourceSchema().getTypes()):(schemaService.getTargetSchema().getTypes());
		
		if (elements == null) {
			elements = new HashMap<Definition, FeatureType>();
		}
		
		Style style = styleFactory.createStyle();
		
		for (Entry<Definition, FeatureType> entry : elements.entrySet()) {
			TypeDefinition typeDef = DefinitionUtil.getType(entry.getKey());
			
			if (!typeDef.isAbstract() && typeDef.hasGeometry()) {
				// only add styles for non-abstract feature types
				FeatureType ft = entry.getValue();
				
				FeatureTypeStyle fts = styles.get(ft);
				if (fts == null) {
					fts = StyleHelper.getDefaultStyle(ft);
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
			result.add(StyleHelper.createPolygonSymbolizer(color, width));
		}
		else if (symbolizer instanceof LineSymbolizer) {
			result.add(StyleHelper.createLineSymbolizer(color, width)); 
		}
		else if (symbolizer instanceof PointSymbolizer) {
			result.add(StyleHelper.mutatePointSymbolizer((PointSymbolizer) symbolizer, color, width));
			//result.add(createPointSymbolizer(color, width));
		}
		else {
			// do not fall-back to original symbolizer cause we are painting over it
			//result.add(symbolizer);
		}
		
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
				Definition element = schemaService.getTypeByName(fts.getFeatureTypeName());
				if (element != null && DefinitionUtil.getFeatureType(element) != null) {
					if (addStyle(DefinitionUtil.getFeatureType(element), fts)) {
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
			notifyStylesAdded();
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
				_log.info("Replacing style for feature type " + ft.getName()); //$NON-NLS-1$
				somethingHappened = true;
			}
		}
		else {
			_log.info("Adding style for feature type " + ft.getName()); //$NON-NLS-1$
			somethingHappened = true;
		}
		
		this.styles.put(ft, fts);
		return somethingHappened;
	}

	/**
	 * @see StyleService#addStyles(URL)
	 */
	@Override
	public boolean addStyles(URL url) {
		SLDParser stylereader;
		try {
			stylereader = new SLDParser(styleFactory, url);
			Style[] styles = stylereader.readXML();
			
			addStyles(styles);
			
			return true;
		} catch (Exception e) {
			_log.error("Error reading styled layer descriptor", e); //$NON-NLS-1$
			return false;
		}
	}
	
	/**
	 * @see StyleService#getBackground()
	 */
	@Override
	public RGB getBackground() {
		if (background == null) {
			return StylePreferences.getDefaultBackground();
		}
		else {
			return background;
		}
	}

	/**
	 * @see StyleService#setBackground(RGB)
	 */
	@Override
	public void setBackground(RGB color) {
		this.background = color;
		
		notifyBackgroundChanged(color);
	}

	/**
	 * @see StyleService#clearStyles()
	 */
	@Override
	public void clearStyles() {
		queuedStyles.clear();
		styles.clear();
		
		notifyStylesRemoved();
	}
	
}
