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
package eu.esdihumboldt.hale.ui.style.service.internal;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.NameImpl;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.xml.styling.SLDParser;
import org.opengis.feature.type.Name;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.ui.common.service.style.StyleService;
import eu.esdihumboldt.hale.ui.common.service.style.StyleServiceListener;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.ui.style.StyleHelper;
import eu.esdihumboldt.hale.ui.style.internal.InstanceStylePlugin;

/**
 * A default {@link StyleService} implementation that will provide simple styles
 * for Lines, Points and Polygons if none have been loaded from an SLD.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StyleServiceImpl extends AbstractStyleService {

	private static final ALogger _log = ALoggerFactory.getLogger(StyleServiceImpl.class);

	private final Map<TypeDefinition, FeatureTypeStyle> styles;

	private final SchemaService schemaService;

	/**
	 * Queued styles
	 */
	private final Queue<FeatureTypeStyle> queuedStyles = new LinkedList<FeatureTypeStyle>();

	private static final StyleBuilder styleBuilder = new StyleBuilder();

	private static final StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);

	private RGB background = null;

	private FeatureTypeStyle fbStyle = null;

	// Constructor, instance accessor ..........................................

	/**
	 * Create a style service.
	 * 
	 * @param projectService the project service
	 * @param schemaService the schema service
	 */
	public StyleServiceImpl(final ProjectService projectService, SchemaService schemaService) {
		styles = new HashMap<TypeDefinition, FeatureTypeStyle>();

		this.schemaService = schemaService;

		// add listener to process queued styles
		schemaService.addSchemaServiceListener(new SchemaServiceListener() {

			@Override
			public void schemaAdded(SchemaSpaceID spaceID, Schema schema) {
				update();
			}

			@Override
			public void schemasCleared(SchemaSpaceID spaceID) {
				update();
			}

			@Override
			public void mappableTypesChanged(SchemaSpaceID spaceID,
					Collection<? extends TypeDefinition> types) {
				update();
			}

			private void update() {
				Collection<FeatureTypeStyle> failures = new ArrayList<FeatureTypeStyle>();
				boolean updateNeeded = false;

				while (!queuedStyles.isEmpty()) {
					FeatureTypeStyle fts = queuedStyles.poll();
					Collection<TypeDefinition> types = findTypes(fts.featureTypeNames());
					if (types != null && !types.isEmpty()) {
						for (TypeDefinition type : types) {
							if (addStyle(type, fts)) {
								updateNeeded = true;
							}
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

		// clear styles on project clean
		projectService.addListener(new ProjectServiceAdapter() {

			@Override
			public void onClean() {
				clearStyles();
			}

		});

		// notify project service of style changes
		// XXX not sure how this is done elsewhere, e.g. on alignment changes
		// TODO revise?
		addListener(new StyleServiceListener() {

			@Override
			public void stylesRemoved(StyleService styleService) {
				projectService.setChanged();
			}

			@Override
			public void stylesAdded(StyleService styleService) {
				projectService.setChanged();
			}

			@Override
			public void styleSettingsChanged(StyleService styleService) {
				// this is an application wide setting
			}

			@Override
			public void backgroundChanged(StyleService styleService, RGB background) {
				// this is an application wide setting
			}
		});
	}

	// StyleService methods ....................................................

	/**
	 * @see StyleService#getNamedStyle(String)
	 */
	@Override
	public Style getNamedStyle(String name) {
		Style style = styleFactory.createStyle();
		for (FeatureTypeStyle fts : this.styles.values()) {
			// XXX checks for the FeatureTypeStyle name instead of the UserStyle
			// name
			if (fts.getName().equals(name)) {
				style = styleFactory.createStyle();
				style.featureTypeStyles().add(fts);
				break;
			}
		}
		return style;
	}

	/**
	 * This implementation will build a simple style if none is defined
	 * previously.
	 * 
	 * @see StyleService#getStyle(TypeDefinition, DataSet)
	 */
	@Override
	public Style getStyle(TypeDefinition type, @Nullable DataSet dataSet) {
		FeatureTypeStyle fts = styles.get(type);
		Style style = styleFactory.createStyle();
		if (fts != null) {
			style.featureTypeStyles().add(fts);
		}
		else {
			if (fbStyle != null) {
				style.featureTypeStyles().add(fbStyle);
			}
			else {
				style.featureTypeStyles().add(StyleHelper.getDefaultStyle(type, dataSet));
			}
		}
		return style;
	}

	/**
	 * @see StyleService#getDefinedStyle(TypeDefinition)
	 */
	@Override
	public Style getDefinedStyle(TypeDefinition type) {
		FeatureTypeStyle fts = styles.get(type);
		if (fts != null) {
			Style style = styleFactory.createStyle();
			style.featureTypeStyles().add(fts);
			return style;
		}
		else {
			return null;
		}
	}

	/**
	 * @see StyleService#getStyle()
	 */
	@Override
	public Style getStyle() {
		Style style = styleFactory.createStyle();

		for (FeatureTypeStyle fts : styles.values()) {
			style.featureTypeStyles().add(fts);
		}
		if (fbStyle != null) {
			style.featureTypeStyles().add(fbStyle);
		}

		return style;
	}

	/**
	 * @see StyleService#getStyle(DataSet)
	 */
	@Override
	public Style getStyle(final DataSet dataset) {
		return getStyle(dataset, false);
	}

	/**
	 * @see StyleService#getSelectionStyle(DataSet)
	 */
	@Override
	public Style getSelectionStyle(DataSet type) {
		return getStyle(type, true);
	}

	private Style getStyle(final DataSet dataset, boolean selected) {
		SchemaSpace schemas = schemaService.getSchemas(
				(dataset == DataSet.SOURCE) ? (SchemaSpaceID.SOURCE) : (SchemaSpaceID.TARGET));

		Style style = styleFactory.createStyle();

		for (TypeDefinition type : schemas.getMappingRelevantTypes()) {
			if (!type.getConstraint(AbstractFlag.class).isEnabled()) {
				// only add styles for non-abstract feature types
				FeatureTypeStyle fts = styles.get(type);
				if (fts == null) {
					if (fbStyle != null) {
						fts = fbStyle;
					}
					else {
						fts = StyleHelper.getDefaultStyle(type, dataset);
					}

				}
				if (selected) {
					fts = getSelectedStyle(fts);
				}

				style.featureTypeStyles().add(fts);
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
			Rule newRule = styleBuilder
					.createRule(newSymbolizers.toArray(new Symbolizer[newSymbolizers.size()]));
			newRule.setFilter(rule.getFilter());
			newRule.setElseFilter(rule.isElseFilter());
			newRule.setName(rule.getName());
			newRules.add(newRule);
		}

		// TODO Handle case if there is more than one featureTypeName
		return styleBuilder.createFeatureTypeStyle(fts.featureTypeNames().stream().findFirst()
				.orElse(new NameImpl("Feature")).getLocalPart(),
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
			result.add(
					StyleHelper.mutatePointSymbolizer((PointSymbolizer) symbolizer, color, width));
			// result.add(createPointSymbolizer(color, width));
		}
		else {
			// do not fall-back to original symbolizer cause we are painting
			// over it
			// result.add(symbolizer);
		}

		return result;
	}

	/**
	 * @see StyleService#addStyles(Style[])
	 */
	@Override
	public void addStyles(Style... styles) {
		boolean somethingHappened = false;

		for (Style style : styles) {
			for (FeatureTypeStyle fts : style.featureTypeStyles()) {
				if (!fts.featureTypeNames().isEmpty() && fts.featureTypeNames().iterator().next()
						.getLocalPart().equals("Feature")) {
					this.fbStyle = fts;
					somethingHappened = true;
				}
				else {
					Collection<TypeDefinition> types = findTypes(fts.featureTypeNames());
					if (types != null && !types.isEmpty()) {
						for (TypeDefinition type : types) {
							if (addStyle(type, fts)) {
								somethingHappened = true;
							}
						}
					}
					else {
						/*
						 * store for later schema update when feature type might
						 * be present
						 */
						queuedStyles.add(fts);
					}
				}
			}
		}

		if (somethingHappened) {
			notifyStylesAdded();
		}
	}

	/**
	 * Search the available types for matching names.
	 * 
	 * @param featureTypeNames the feature type names
	 * @return the types or <code>null</code>
	 */
	private Collection<TypeDefinition> findTypes(Set<Name> featureTypeNames) {
		if (featureTypeNames == null) {
			return null;
		}

		// prepare names
		Set<QName> qnames = new HashSet<QName>();
		Set<String> localnames = new HashSet<String>();
		for (Name name : featureTypeNames) {
			String ns = name.getNamespaceURI();
			if (ns == null || ns.isEmpty()) {
				localnames.add(name.getLocalPart());
			}
			else {
				qnames.add(new QName(ns, name.getLocalPart()));
			}
		}

		Collection<TypeDefinition> result = new ArrayList<TypeDefinition>();

		// search source...
		result.addAll(
				findTypes(schemaService.getSchemas(SchemaSpaceID.SOURCE), qnames, localnames));
		// and target types
		result.addAll(
				findTypes(schemaService.getSchemas(SchemaSpaceID.TARGET), qnames, localnames));

		return result;
	}

	/**
	 * Search the given type index for matching names.
	 * 
	 * @param typeIndex the type index
	 * @param qnames the qualified names
	 * @param localnames the local names
	 * @return the types or an empty collection
	 */
	private Collection<TypeDefinition> findTypes(TypeIndex typeIndex, Set<QName> qnames,
			Set<String> localnames) {
		Collection<TypeDefinition> result = new ArrayList<TypeDefinition>();
		// check all mappable types
		for (TypeDefinition type : typeIndex.getMappingRelevantTypes()) {
			String name = StyleHelper.getFeatureTypeName(type);
			if (localnames.contains(name)) {
				result.add(type);
			}
			// TODO support for qnames (another method in StyleHelper?)
		}
		return result;
	}

	/*
	 * private Collection<TypeDefinition> findFallbackTypes(){
	 * Collection<TypeDefinition> result = new ArrayList<TypeDefinition>();
	 * 
	 * TypeIndex typeIndexSource =
	 * schemaService.getSchemas(SchemaSpaceID.SOURCE); TypeIndex typeIndexTarget
	 * = schemaService.getSchemas(SchemaSpaceID.TARGET);
	 * 
	 * for (TypeDefinition type : typeIndexSource.getMappingRelevantTypes()){
	 * result.add(type); } for (TypeDefinition type :
	 * typeIndexTarget.getMappingRelevantTypes()){ result.add(type); } return
	 * result; }
	 */

	/**
	 * Add a type style.
	 * 
	 * @param type the type definition
	 * @param fts the type style
	 * @return if the style definitions were changed
	 */
	private boolean addStyle(TypeDefinition type, FeatureTypeStyle fts) {
		boolean somethingHappened = false;
		FeatureTypeStyle old = this.styles.get(type);
		if (old != null) {
			if (!old.equals(fts)) {
				_log.info("Replacing style for feature type " + type.getName()); //$NON-NLS-1$
				somethingHappened = true;
			}
		}
		else {
			_log.info("Adding style for feature type " + type.getName()); //$NON-NLS-1$
			somethingHappened = true;
		}

		this.styles.put(type, fts);
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
		fbStyle = null;

		notifyStylesRemoved();
	}

}
