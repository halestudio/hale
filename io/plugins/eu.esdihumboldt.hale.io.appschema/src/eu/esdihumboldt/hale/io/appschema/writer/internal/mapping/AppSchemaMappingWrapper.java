/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.appschema.writer.internal.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AppSchemaDataAccessType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AttributeExpressionMappingType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AttributeExpressionMappingType.Expression;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AttributeMappingType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AttributeMappingType.ClientProperty;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.IncludesPropertyType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.NamespacesPropertyType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.NamespacesPropertyType.Namespace;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore.Parameters;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore.Parameters.Parameter;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TargetTypesPropertyType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TargetTypesPropertyType.FeatureType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping.AttributeMappings;
import eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils;

/**
 * App-schema mapping configuration wrapper.
 * 
 * <p>
 * Holds the state associated to the same mapping configuration and provides
 * utility methods to mutate it.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AppSchemaMappingWrapper {

	/**
	 * Base name for special attributes used for feature chaining.
	 */
	public static final String FEATURE_LINK_FIELD = "FEATURE_LINK";

	private final String defaultPrefix = "nns__";
	private int prefixCounter = 1;
	private final Map<String, Namespace> namespaceUriMap;
	private final Map<String, Namespace> namespacePrefixMap;
	private final Map<Integer, FeatureTypeMapping> featureTypeMappings;
	private final Map<Integer, Integer> featureLinkCounter;
	private final Map<Integer, AttributeMappingType> attributeMappings;

	private final Map<String, Set<FeatureTypeMapping>> featureTypesByTargetElement;
	private final Map<String, Set<FeatureTypeMapping>> nonFeatureTypesByTargetElement;

	private final AppSchemaDataAccessType appSchemaMapping;

	/**
	 * Constructor.
	 * 
	 * @param appSchemaMapping the app-schema mapping to wrap
	 */
	public AppSchemaMappingWrapper(AppSchemaDataAccessType appSchemaMapping) {
		this.appSchemaMapping = appSchemaMapping;

		initMapping(this.appSchemaMapping);

		this.namespaceUriMap = new HashMap<String, Namespace>();
		this.namespacePrefixMap = new HashMap<String, Namespace>();
		this.featureTypeMappings = new HashMap<Integer, FeatureTypeMapping>();
		this.featureLinkCounter = new HashMap<Integer, Integer>();
		this.attributeMappings = new HashMap<Integer, AttributeMappingType>();
		this.featureTypesByTargetElement = new HashMap<String, Set<FeatureTypeMapping>>();
		this.nonFeatureTypesByTargetElement = new HashMap<String, Set<FeatureTypeMapping>>();
	}

	/**
	 * Return the configuration of the default datastore.
	 * 
	 * <p>
	 * An empty datastore configuration is created if none is available.
	 * </p>
	 * 
	 * @return the default datastore's configuration.
	 */
	public DataStore getDefaultDataStore() {
		List<DataStore> dataStores = appSchemaMapping.getSourceDataStores().getDataStore();
		if (dataStores.size() == 0) {
			DataStore defaultDS = new DataStore();
			defaultDS.setId(UUID.randomUUID().toString());
			defaultDS.setParameters(new Parameters());
			dataStores.add(defaultDS);
		}

		return dataStores.get(0);
	}

	/**
	 * Return a namespace object with the provided URI and prefix.
	 * 
	 * <p>
	 * If a namespace object for the same URI already exists, it is returned.
	 * Otherwise, a new one is created.
	 * </p>
	 * <p>
	 * If the prefix is empty, a non-empty prefix is automatically generated.
	 * If, in a subsequent call to this method, a non-empty prefix is provided,
	 * the user-provided prefix will replace the generated one.
	 * </p>
	 * 
	 * @param namespaceURI the namespace URI
	 * @param prefix the namespace prefix
	 * @return the created namespace object
	 */
	Namespace getOrCreateNamespace(String namespaceURI, String prefix) {
		if (namespaceURI != null && !namespaceURI.isEmpty()) {
			if (!namespaceUriMap.containsKey(namespaceURI)) {
				String basePrefix, uniquePrefix;
				if (prefix == null || prefix.trim().isEmpty()) {
					basePrefix = defaultPrefix;
					uniquePrefix = basePrefix + prefixCounter;
					prefixCounter++;
				}
				else {
					basePrefix = prefix;
					uniquePrefix = basePrefix;
				}
				// make sure prefix is unique
				while (namespacePrefixMap.containsKey(uniquePrefix)) {
					uniquePrefix = basePrefix + prefixCounter;
					prefixCounter++;
				}

				Namespace ns = new Namespace();
				ns.setPrefix(uniquePrefix);
				ns.setUri(namespaceURI);

				namespaceUriMap.put(namespaceURI, ns);
				namespacePrefixMap.put(uniquePrefix, ns);

				appSchemaMapping.getNamespaces().getNamespace().add(ns);

				return ns;
			}
			else {
				// update prefix if provided prefix is not empty and currently
				// assigned prefix was made up
				Namespace ns = namespaceUriMap.get(namespaceURI);
				if (prefix != null && !prefix.isEmpty() && ns.getPrefix().startsWith(defaultPrefix)) {
					// // check prefix is unique
					// if (!namespacePrefixMap.containsKey(prefix)) {
					// remove old prefix-NS mapping from namespacePrefixMap
					namespacePrefixMap.remove(ns.getPrefix());
					// add new prefix-NS mapping to namespacePrefixMap
					ns.setPrefix(prefix);
					namespacePrefixMap.put(prefix, ns);
					// }
				}
				return ns;
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Add a schema URI to the list of target types.
	 * 
	 * @param schemaURI the schema URI
	 */
	public void addSchemaURI(String schemaURI) {
		if (schemaURI != null && !schemaURI.isEmpty()) {
			this.appSchemaMapping.getTargetTypes().getFeatureType().getSchemaUri().add(schemaURI);
		}
	}

	/**
	 * Updates a schema URI in the generated mapping configuration.
	 * 
	 * @param oldSchemaURI the current schema URI
	 * @param newSchemaURI the updated schema URI
	 */
	public void updateSchemaURI(String oldSchemaURI, String newSchemaURI) {
		if (oldSchemaURI != null && !oldSchemaURI.isEmpty() && newSchemaURI != null
				&& !newSchemaURI.isEmpty()) {
			List<String> uris = this.appSchemaMapping.getTargetTypes().getFeatureType()
					.getSchemaUri();
			if (uris.contains(oldSchemaURI)) {
				uris.remove(oldSchemaURI);
				uris.add(newSchemaURI);
			}
		}
	}

	/**
	 * @see AppSchemaMappingWrapper#buildAttributeXPath(TypeDefinition, List)
	 * 
	 * @param owningType the type owning the target property
	 * @param propertyEntityDef the target property definition
	 * @return the XPath expression pointing to the target property
	 */
	public String buildAttributeXPath(TypeDefinition owningType,
			PropertyEntityDefinition propertyEntityDef) {
		List<ChildContext> propertyPath = propertyEntityDef.getPropertyPath();

		return buildAttributeXPath(owningType, propertyPath);
	}

	/**
	 * Build an XPath expression to be used as &lt;targetAttribute&gt; for the
	 * provided target property definition.
	 * 
	 * <p>
	 * The algorithm to build the path is as follows:
	 * <ol>
	 * <li>the property path is traversed backwards, from end to beginning</li>
	 * <li>on each step, a new path segment is added at the top of the list, but
	 * only if the child definition describes a property and not a group</li>
	 * <li>on each step, if a non-null context name is defined on the child
	 * context, <code>[&lt;context name&gt;]</code> string is appended to the
	 * path segment</li>
	 * <li>the traversal stops when the parent type of the last visited property
	 * equals to the provided owning type</li>
	 * </ol>
	 * 
	 * @param owningType the type owning the target property
	 * @param propertyPath the target property path
	 * @return the XPath expression pointing to the target property
	 */
	public String buildAttributeXPath(TypeDefinition owningType, List<ChildContext> propertyPath) {

		List<String> pathSegments = new ArrayList<String>();
		for (int i = propertyPath.size() - 1; i >= 0; i--) {
			ChildContext childContext = propertyPath.get(i);
			// TODO: how to handle conditions?
			Integer contextId = childContext.getContextName();
			ChildDefinition<?> child = childContext.getChild();
			// only properties (not groups) are taken into account in building
			// the xpath expression
			if (child.asProperty() != null) {
				String namespaceURI = child.getName().getNamespaceURI();
				String prefix = child.getName().getPrefix();
				String name = child.getName().getLocalPart();

				Namespace ns = getOrCreateNamespace(namespaceURI, prefix);
				String path = ns.getPrefix() + ":" + name;
				if (contextId != null) {
					// XPath indices start from 1, whereas contextId starts from
					// 0 --> add 1
					path = String.format("%s[%d]", path, contextId + 1);
				}
				// insert path segment at the first position
				pathSegments.add(0, path);
			}
			if (child.getParentType() != null
					&& child.getParentType().getName().equals(owningType.getName())) {
				// I reached the owning type: stop walking the path
				break;
			}
		}

		String xPath = Joiner.on("/").join(pathSegments);

		return xPath;

	}

	/**
	 * Return the feature type mapping associated to the provided type.
	 * 
	 * <p>
	 * If a feature type mapping for the provided type already exists, it is
	 * returned; otherwise, a new one is created.
	 * </p>
	 * 
	 * @param targetType the target type
	 * @return the feature type mapping
	 */
	FeatureTypeMapping getOrCreateFeatureTypeMapping(TypeDefinition targetType) {
		return getOrCreateFeatureTypeMapping(targetType, null);
	}

	/**
	 * Return the feature type mapping associated to the provided type and
	 * mapping name.
	 * 
	 * <p>
	 * If a feature type mapping for the provided type and mapping name already
	 * exists, it is returned; otherwise, a new one is created.
	 * </p>
	 * 
	 * @param targetType the target type
	 * @param mappingName the mapping name
	 * @return the feature type mapping
	 */
	FeatureTypeMapping getOrCreateFeatureTypeMapping(TypeDefinition targetType, String mappingName) {
		if (targetType == null) {
			return null;
		}

		Integer hashKey = getFeatureTypeMappingHashKey(targetType, mappingName);
		if (!featureTypeMappings.containsKey(hashKey)) {
			// create
			FeatureTypeMapping featureTypeMapping = new FeatureTypeMapping();
			// initialize attribute mappings member
			featureTypeMapping.setAttributeMappings(new AttributeMappings());
			// TODO: how do I know the datasource from which data will be read?
			featureTypeMapping.setSourceDataStore(getDefaultDataStore().getId());
			// Retrieve namespace this feature type belongs to and prepend its
			// prefix to the feature type name; if a namespace with the same URI
			// already existed with a valid prefix, that will be used instead of
			// the one passed here
			Namespace ns = getOrCreateNamespace(targetType.getName().getNamespaceURI(), targetType
					.getName().getPrefix());
			// TODO: I'm getting the element name with
			// targetType.getDisplayName():
			// isn't there a more elegant (and perhaps more reliable) way to
			// know which element corresponds to a type?
			featureTypeMapping.setTargetElement(ns.getPrefix() + ":" + targetType.getDisplayName());
			if (mappingName != null && !mappingName.isEmpty()) {
				featureTypeMapping.setMappingName(mappingName);
			}

			appSchemaMapping.getTypeMappings().getFeatureTypeMapping().add(featureTypeMapping);
			featureTypeMappings.put(hashKey, featureTypeMapping);
			addToFeatureTypeMappings(targetType, featureTypeMapping);
		}
		return featureTypeMappings.get(hashKey);
	}

	private Integer getFeatureTypeMappingHashKey(TypeDefinition targetType, String mappingName) {
		String hashBase = targetType.getName().toString();
		if (mappingName != null && !mappingName.isEmpty()) {
			hashBase += "__" + mappingName;
		}

		return hashBase.hashCode();
	}

	private void addToFeatureTypeMappings(TypeDefinition targetType, FeatureTypeMapping typeMapping) {
		Map<String, Set<FeatureTypeMapping>> mappingsByTargetElement = null;
		if (AppSchemaMappingUtils.isFeatureType(targetType)) {
			mappingsByTargetElement = featureTypesByTargetElement;
		}
		else {
			mappingsByTargetElement = nonFeatureTypesByTargetElement;
		}

		if (!mappingsByTargetElement.containsKey(typeMapping.getTargetElement())) {
			mappingsByTargetElement.put(typeMapping.getTargetElement(),
					new HashSet<FeatureTypeMapping>());
		}
		mappingsByTargetElement.get(typeMapping.getTargetElement()).add(typeMapping);
	}

	/**
	 * Returns the value of the <code>&lt;targetElement&gt;</code> tag for all
	 * feature types in the mapping configuration.
	 * 
	 * @return the set of feature type element names
	 */
	public Set<String> getFeatureTypeElements() {
		return featureTypesByTargetElement.keySet();
	}

	/**
	 * Returns the value of the <code>&lt;targetElement&gt;</code> tag for all
	 * non-feature types in the mapping configuration.
	 * 
	 * @return the set of non-feature type element names
	 */
	public Set<String> getNonFeatureTypeElements() {
		return nonFeatureTypesByTargetElement.keySet();
	}

	/**
	 * Returns all configured mappings for the provided feature type.
	 * 
	 * @param featureTypeElement the feature type's element name
	 * @return the mappings
	 */
	public Set<FeatureTypeMapping> getFeatureTypeMappings(String featureTypeElement) {
		return getTypeMappingsByElement(featureTypesByTargetElement, featureTypeElement);
	}

	/**
	 * Returns all configured mappings for the provided non-feature type.
	 * 
	 * @param nonFeatureTypeElement the non-feature type's element name
	 * @return the mappings
	 */
	public Set<FeatureTypeMapping> getNonFeatureTypeMappings(String nonFeatureTypeElement) {
		return getTypeMappingsByElement(nonFeatureTypesByTargetElement, nonFeatureTypeElement);
	}

	private Set<FeatureTypeMapping> getTypeMappingsByElement(
			Map<String, Set<FeatureTypeMapping>> mappingsByTargetElement, String typeElement) {
		Set<FeatureTypeMapping> mappings = null;
		if (mappingsByTargetElement.containsKey(typeElement)) {
			mappings = mappingsByTargetElement.get(typeElement);
		}
		else {
			mappings = Collections.emptySet();
		}
		return mappings;
	}

	/**
	 * Returns the unique <code>FEATURE_LINK</code> attribute name for the
	 * specified feature type mapping.
	 * 
	 * <p>
	 * E.g. the first time the method is called, it will return
	 * <code>FEATURE_LINK[1]</code>; if it is called a second time, with the
	 * same input parameters, it will return <code>FEATURE_LINK[2]</code>, and
	 * so on.
	 * </p>
	 * 
	 * @param featureType the feature type
	 * @param mappingName the feature type's mapping name (may be
	 *            <code>null</code>)
	 * @return a unique <code>FEATURE_LINK[i]</code> attribute name
	 */
	String getUniqueFeatureLinkAttribute(TypeDefinition featureType, String mappingName) {
		Integer featureTypeKey = getFeatureTypeMappingHashKey(featureType, mappingName);
		if (!featureLinkCounter.containsKey(featureTypeKey)) {
			featureLinkCounter.put(featureTypeKey, 0);
		}
		Integer counter = featureLinkCounter.get(featureTypeKey);
		// update counter
		featureLinkCounter.put(featureTypeKey, ++counter);

		return String.format("%s[%d]", FEATURE_LINK_FIELD, counter);
	}

	/**
	 * Return the attribute mapping associated to the provided property.
	 * 
	 * <p>
	 * If an attribute mapping for the provided property already exists, it is
	 * returned; otherwise, a new one is created.
	 * </p>
	 * 
	 * @param owningType the type owning the property
	 * @param mappingName the mapping name
	 * @param propertyPath the property path
	 * @return the attribute mapping
	 */
	AttributeMappingType getOrCreateAttributeMapping(TypeDefinition owningType, String mappingName,
			List<ChildContext> propertyPath) {
		if (propertyPath == null || propertyPath.isEmpty()) {
			return null;
		}

		Integer hashKey = getAttruteMappingHashKey(owningType, propertyPath);
		if (!attributeMappings.containsKey(hashKey)) {
			// create
			AttributeMappingType attrMapping = new AttributeMappingType();
			// add to owning type mapping
			FeatureTypeMapping ftMapping = getOrCreateFeatureTypeMapping(owningType, mappingName);
			ftMapping.getAttributeMappings().getAttributeMapping().add(attrMapping);
			// put into internal map
			attributeMappings.put(hashKey, attrMapping);
		}
		return attributeMappings.get(hashKey);
	}

	private Integer getAttruteMappingHashKey(TypeDefinition owningType,
			List<ChildContext> propertyPath) {
		final String SEPARATOR = "__";
		StringBuilder pathBuilder = new StringBuilder();

		if (owningType != null) {
			pathBuilder.append(owningType.getName().toString()).append(SEPARATOR);
			for (ChildContext childContext : propertyPath) {
				pathBuilder.append(childContext.getChild().getName().toString());
				if (childContext.getContextName() != null) {
					pathBuilder.append(childContext.getContextName());
				}
				pathBuilder.append(SEPARATOR);
			}
		}
		else {
			throw new IllegalArgumentException("Could not find feature type owning property");
		}

		return pathBuilder.toString().hashCode();
	}

	/**
	 * @return a copy of the wrapped app-schema mapping
	 */
	public AppSchemaDataAccessType getAppSchemaMapping() {
		return cloneMapping(appSchemaMapping);
	}

	/**
	 * Returns true if the wrapped app-schema mapping configuration must be
	 * split in multiple files.
	 * 
	 * <p>
	 * The configuration will be split in a main file containing mappings for
	 * all top-level feature types, and a second file containing mappings for
	 * non-feature types (and alternative mappings for the feature types
	 * configured in the main file).
	 * </p>
	 * 
	 * @return true if multiple files are required to store the mapping
	 *         configuration, false otherwise
	 */
	public boolean requiresMultipleFiles() {
		// if non-feature type mappings are present, return true
		if (nonFeatureTypesByTargetElement.size() > 0) {
			return true;
		}

		// check whether multiple mappings of the same feature type are present
		for (String targetElement : featureTypesByTargetElement.keySet()) {
			if (featureTypesByTargetElement.get(targetElement).size() > 1) {
				return true;
			}
		}

		// don't need multiple files
		return false;
	}

	/**
	 * Returns the mapping configuration for the main mapping file.
	 * 
	 * <p>
	 * If the mapping does not require multiple files, this method is equivalent
	 * to {@link #getAppSchemaMapping()}.
	 * </p>
	 * 
	 * @return a copy of the main mapping configuration
	 */
	public AppSchemaDataAccessType getMainMapping() {
		AppSchemaDataAccessType mainMapping = cloneMapping(appSchemaMapping);

		if (requiresMultipleFiles()) {
			// add included types configuration
			mainMapping.getIncludedTypes().getInclude()
					.add(AppSchemaIO.INCLUDED_TYPES_MAPPING_FILE);

			Set<FeatureTypeMapping> toBeRemoved = new HashSet<FeatureTypeMapping>();
			Set<FeatureTypeMapping> toBeKept = new HashSet<FeatureTypeMapping>();
			groupTypeMappings(toBeKept, toBeRemoved);
			purgeTypeMappings(mainMapping, toBeRemoved);
		}

		return mainMapping;
	}

	/**
	 * Returns the mapping configuration for the included types mapping file.
	 * 
	 * <p>
	 * If the mapping does not require multiple files, <code>null</code> is
	 * returned.
	 * </p>
	 * 
	 * @return a copy of the included types mapping configuration, or
	 *         <code>null</code>
	 */
	public AppSchemaDataAccessType getIncludedTypesMapping() {
		if (requiresMultipleFiles()) {
			AppSchemaDataAccessType includedTypesMapping = cloneMapping(appSchemaMapping);

			Set<FeatureTypeMapping> toBeRemoved = new HashSet<FeatureTypeMapping>();
			Set<FeatureTypeMapping> toBeKept = new HashSet<FeatureTypeMapping>();
			groupTypeMappings(toBeRemoved, toBeKept);
			purgeTypeMappings(includedTypesMapping, toBeRemoved);

			return includedTypesMapping;
		}
		else {
			return null;
		}
	}

	private void groupTypeMappings(Set<FeatureTypeMapping> mainTypes,
			Set<FeatureTypeMapping> includedTypes) {
		// look for multiple mappings of the same feature type and determine
		// the top level feature type mappings
		for (Set<FeatureTypeMapping> ftMappings : featureTypesByTargetElement.values()) {
			if (ftMappings.size() > 1) {
				FeatureTypeMapping topLevelMapping = null;
				for (FeatureTypeMapping m : ftMappings) {
					if (topLevelMapping != null) {
						// top level mapping already found, drop the others
						includedTypes.add(m);
					}
					else {
						if (m.getMappingName() == null || m.getMappingName().trim().isEmpty()) {
							// use this as top level mapping
							// TODO: there's no guarantee this is the right one
							// to pick
							topLevelMapping = m;
						}
					}
				}
				if (topLevelMapping == null) {
					// pick the first one (it's pretty much a random choice)
					topLevelMapping = ftMappings.iterator().next();
				}
				mainTypes.add(topLevelMapping);
			}
			else {
				mainTypes.add(ftMappings.iterator().next());
			}
		}

		// non-feature type mappings go in the "included types" group
		for (Set<FeatureTypeMapping> ftMappings : nonFeatureTypesByTargetElement.values()) {
			includedTypes.addAll(ftMappings);
		}
	}

	private void purgeTypeMappings(AppSchemaDataAccessType mapping,
			Set<FeatureTypeMapping> toBeRemoved) {
		Set<String> usedStores = new HashSet<String>();
		Iterator<FeatureTypeMapping> featureIt = mapping.getTypeMappings().getFeatureTypeMapping()
				.iterator();
		while (featureIt.hasNext()) {
			FeatureTypeMapping ftMapping = featureIt.next();
			if (lookupTypeMapping(ftMapping, toBeRemoved) != null) {
				featureIt.remove();
			}
			else {
				usedStores.add(ftMapping.getSourceDataStore());
			}
		}

		// remove unnecessary DataStores
		Iterator<DataStore> storeIt = mapping.getSourceDataStores().getDataStore().iterator();
		while (storeIt.hasNext()) {
			if (!usedStores.contains(storeIt.next().getId())) {
				storeIt.remove();
			}
		}
	}

	private FeatureTypeMapping lookupTypeMapping(FeatureTypeMapping ftMapping,
			Set<FeatureTypeMapping> candidates) {
		for (FeatureTypeMapping candidate : candidates) {
			boolean sameElement = ftMapping.getTargetElement().equals(candidate.getTargetElement());
			boolean noMappingName = ftMapping.getMappingName() == null
					&& candidate.getMappingName() == null;
			boolean sameMappingName = false;
			if (!noMappingName) {
				sameMappingName = ftMapping.getMappingName() != null
						&& ftMapping.getMappingName().equals(candidate.getMappingName());
			}
			if (sameElement && (noMappingName || sameMappingName)) {
				return candidate;
			}
		}

		return null;
	}

	static AppSchemaDataAccessType cloneMapping(AppSchemaDataAccessType mapping) {
		AppSchemaDataAccessType clone = new AppSchemaDataAccessType();

		initMapping(clone);

		clone.setCatalog(mapping.getCatalog());
		clone.getIncludedTypes().getInclude().addAll(mapping.getIncludedTypes().getInclude());
		for (Namespace ns : mapping.getNamespaces().getNamespace()) {
			clone.getNamespaces().getNamespace().add(cloneNamespace(ns));
		}
		for (DataStore ds : mapping.getSourceDataStores().getDataStore()) {
			clone.getSourceDataStores().getDataStore().add(cloneDataStore(ds));
		}
		clone.getTargetTypes().getFeatureType().getSchemaUri()
				.addAll(mapping.getTargetTypes().getFeatureType().getSchemaUri());
		for (FeatureTypeMapping ftMapping : mapping.getTypeMappings().getFeatureTypeMapping()) {
			clone.getTypeMappings().getFeatureTypeMapping().add(cloneFeatureTypeMapping(ftMapping));
		}

		return clone;
	}

	static Namespace cloneNamespace(Namespace ns) {
		if (ns == null) {
			return null;
		}

		Namespace clone = new Namespace();
		clone.setPrefix(ns.getPrefix());
		clone.setUri(ns.getUri());

		return clone;
	}

	static DataStore cloneDataStore(DataStore ds) {
		DataStore clone = new DataStore();
		clone.setParameters(new Parameters());
		clone.setId(ds.getId());
		clone.setIdAttribute(ds.getIdAttribute());

		if (ds.getParameters() != null) {
			for (Parameter param : ds.getParameters().getParameter()) {
				Parameter paramClone = new Parameter();
				paramClone.setName(param.getName());
				paramClone.setValue(param.getValue());
				clone.getParameters().getParameter().add(paramClone);
			}
		}

		return clone;
	}

	static FeatureTypeMapping cloneFeatureTypeMapping(FeatureTypeMapping ftMapping) {
		FeatureTypeMapping clone = new FeatureTypeMapping();
		clone.setAttributeMappings(new AttributeMappings());
		if (ftMapping.getAttributeMappings() != null) {
			for (AttributeMappingType attrMapping : ftMapping.getAttributeMappings()
					.getAttributeMapping()) {
				clone.getAttributeMappings().getAttributeMapping()
						.add(cloneAttributeMapping(attrMapping));
			}
		}
		clone.setIsDenormalised(ftMapping.isIsDenormalised());
		clone.setIsXmlDataStore(ftMapping.isIsXmlDataStore());
		clone.setItemXpath(ftMapping.getItemXpath());
		clone.setMappingName(ftMapping.getMappingName());
		clone.setSourceDataStore(ftMapping.getSourceDataStore());
		clone.setSourceType(ftMapping.getSourceType());
		clone.setTargetElement(ftMapping.getTargetElement());

		return clone;
	}

	static AttributeMappingType cloneAttributeMapping(AttributeMappingType attrMapping) {
		AttributeMappingType clone = new AttributeMappingType();

		clone.setEncodeIfEmpty(attrMapping.isEncodeIfEmpty());
		clone.setIsList(attrMapping.isIsList());
		clone.setIsMultiple(attrMapping.isIsMultiple());
		for (ClientProperty clientProp : attrMapping.getClientProperty()) {
			ClientProperty clientPropClone = new ClientProperty();
			clientPropClone.setName(clientProp.getName());
			clientPropClone.setValue(clientProp.getValue());
			clone.getClientProperty().add(clientPropClone);
		}
		clone.setIdExpression(cloneAttributeExpression(attrMapping.getIdExpression()));
		clone.setInstancePath(attrMapping.getInstancePath());
		clone.setLabel(attrMapping.getLabel());
		clone.setParentLabel(attrMapping.getParentLabel());
		clone.setSourceExpression(cloneAttributeExpression(attrMapping.getSourceExpression()));
		clone.setTargetAttribute(attrMapping.getTargetAttribute());
		clone.setTargetAttributeNode(attrMapping.getTargetAttributeNode());
		clone.setTargetQueryString(attrMapping.getTargetQueryString());

		return clone;
	}

	static AttributeExpressionMappingType cloneAttributeExpression(
			AttributeExpressionMappingType attrExpression) {
		if (attrExpression == null) {
			return attrExpression;
		}

		AttributeExpressionMappingType clone = new AttributeExpressionMappingType();
		if (attrExpression.getExpression() != null) {
			clone.setExpression(new Expression());
			// TODO: Expression is xs:anyType, how can I make a copy of it?
			clone.getExpression().setExpression(attrExpression.getExpression().getExpression());
		}
		clone.setIndex(attrExpression.getIndex());
		clone.setInputAttribute(attrExpression.getInputAttribute());
		clone.setLinkElement(attrExpression.getLinkElement());
		clone.setLinkField(attrExpression.getLinkField());
		clone.setOCQL(attrExpression.getOCQL());

		return clone;
	}

	/**
	 * If necessary, initializes fields to minimize the risk of undesired NPEs.
	 * 
	 * @param mapping the mapping
	 */
	private static void initMapping(AppSchemaDataAccessType mapping) {
		if (mapping.getNamespaces() == null) {
			mapping.setNamespaces(new NamespacesPropertyType());
		}
		if (mapping.getSourceDataStores() == null) {
			mapping.setSourceDataStores(new SourceDataStoresPropertyType());
		}
		if (mapping.getIncludedTypes() == null) {
			mapping.setIncludedTypes(new IncludesPropertyType());
		}
		if (mapping.getTargetTypes() == null) {
			mapping.setTargetTypes(new TargetTypesPropertyType());
		}
		if (mapping.getTargetTypes().getFeatureType() == null) {
			mapping.getTargetTypes().setFeatureType(new FeatureType());
		}
		if (mapping.getTypeMappings() == null) {
			mapping.setTypeMappings(new TypeMappingsPropertyType());
		}
	}
}
