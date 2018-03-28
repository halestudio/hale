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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.google.common.base.Strings;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AttributeMappingType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.NamespacesPropertyType.Namespace;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping;
import eu.esdihumboldt.hale.io.appschema.model.FeatureChaining;
import eu.esdihumboldt.hale.io.appschema.model.WorkspaceConfiguration;
import eu.esdihumboldt.hale.io.appschema.model.WorkspaceMetadata;
import eu.esdihumboldt.hale.io.appschema.writer.UniqueMappingNameGenerator;
import eu.esdihumboldt.hale.io.appschema.writer.internal.RandomUniqueMappingNameGenerator;

/**
 * Holds information about the mapping context, i.e. a reference to the mapping
 * wrapper object, a reference to the alignment, a list of the target types
 * relevant to the mapping and the feature chaining configuration.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AppSchemaMappingContext {

	private Alignment alignment;
	private Collection<? extends TypeDefinition> relevantTargetTypes;
	private FeatureChaining chainingConf;
	private WorkspaceConfiguration workspaceConf;
	private final AppSchemaMappingWrapper mappingWrapper;
	private final Map<QName, String> mappingNames = new HashMap<>();
	private final UniqueMappingNameGenerator mappingNameGenerator = new RandomUniqueMappingNameGenerator();

	/**
	 * Single argument constructor.
	 * 
	 * @param mappingWrapper the mapping wrapper
	 */
	public AppSchemaMappingContext(AppSchemaMappingWrapper mappingWrapper) {
		this.mappingWrapper = mappingWrapper;
		this.relevantTargetTypes = new HashSet<TypeDefinition>();
	}

	/**
	 * Two arguments constructor.
	 * 
	 * @param mappingWrapper the mapping wrapper
	 * @param alignment the alignment
	 */
	public AppSchemaMappingContext(AppSchemaMappingWrapper mappingWrapper, Alignment alignment) {
		this(mappingWrapper);
		this.alignment = alignment;
	}

	/**
	 * Three arguments constructor.
	 * 
	 * @param mappingWrapper the mapping wrapper
	 * @param alignment the aligment
	 * @param relevantTargetTypes the set of mapping relevant target types
	 */
	public AppSchemaMappingContext(AppSchemaMappingWrapper mappingWrapper, Alignment alignment,
			Collection<? extends TypeDefinition> relevantTargetTypes) {
		this(mappingWrapper, alignment);
		if (this.relevantTargetTypes != null) {
			this.relevantTargetTypes = relevantTargetTypes;
		}
	}

	/**
	 * Five arguments constructor.
	 * 
	 * @param mappingWrapper the mapping wrapper
	 * @param alignment the aligment
	 * @param relevantTargetTypes the set of mapping relevant target types
	 * @param chainingConf the feature chaining configuration
	 * @param workspaceConf the workspace configuration
	 */
	public AppSchemaMappingContext(AppSchemaMappingWrapper mappingWrapper, Alignment alignment,
			Collection<? extends TypeDefinition> relevantTargetTypes, FeatureChaining chainingConf,
			WorkspaceConfiguration workspaceConf) {
		this(mappingWrapper, alignment);
		if (this.relevantTargetTypes != null) {
			this.relevantTargetTypes = relevantTargetTypes;
		}
		this.chainingConf = chainingConf;
		this.workspaceConf = workspaceConf;
	}

	/**
	 * @return the mappingWrapper
	 */
	public AppSchemaMappingWrapper getMappingWrapper() {
		return mappingWrapper;
	}

	/**
	 * @return the alignment
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * Return a copy of the collection containing the mapping relevant target
	 * types.
	 * 
	 * @return the set of relevant target types
	 */
	public Collection<? extends TypeDefinition> getRelevantTargetTypes() {
		return new HashSet<TypeDefinition>(relevantTargetTypes);
	}

	/**
	 * @return the feature chaining configuration
	 */
	public FeatureChaining getFeatureChaining() {
		return chainingConf;
	}

	/**
	 * @return the workspace configuration
	 */
	public WorkspaceConfiguration getWorkspaceConf() {
		return workspaceConf;
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
	 * <p>
	 * This method checks if the workspace configuration contains a
	 * corresponding workspace (i.e. same namespace URI) and if it does uses its
	 * name as prefix, overriding the {@code prefix} argument. In any case, the
	 * implementation delegates to
	 * {@link AppSchemaMappingWrapper#getOrCreateNamespace(String, String)} to
	 * do the heavy lifting.
	 * </p>
	 * 
	 * @param namespaceURI the namespace URI
	 * @param prefix the namespace prefix
	 * @return the created namespace object
	 */
	public Namespace getOrCreateNamespace(String namespaceURI, String prefix) {
		if (Strings.isNullOrEmpty(namespaceURI)) {
			return null;
		}

		if (workspaceConf != null) {
			// honor user provided user provided prefixes in creating /
			// retrieving namespace
			WorkspaceMetadata workspace = workspaceConf.getWorkspace(namespaceURI);
			if (workspace != null) {
				return mappingWrapper.getOrCreateNamespace(namespaceURI, workspace.getName());
			}
		}
		return mappingWrapper.getOrCreateNamespace(namespaceURI, prefix);
	}

	/**
	 * Return the feature type mapping associated to the provided type.
	 * 
	 * <p>
	 * If a feature type mapping for the provided type already exists, it is
	 * returned; otherwise, a new one is created.
	 * </p>
	 * 
	 * <p>
	 * This method tests whether the specified type belongs to an isolated
	 * workspace (as indicated in the workspace configuration) and, if so,
	 * generates a unique mapping name to be associated with the feature type
	 * mapping, thus protecting it against name clashes.
	 * 
	 * The implementation then delegates to
	 * {@link AppSchemaMappingWrapper#getOrCreateFeatureTypeMapping(TypeDefinition, String)}
	 * , passing {@code null} as second argument if no mapping name was
	 * generated.
	 * </p>
	 * 
	 * @param targetType the target type
	 * @return the feature type mapping
	 */
	public FeatureTypeMapping getOrCreateFeatureTypeMapping(TypeDefinition targetType) {
		if (targetType == null) {
			return null;
		}

		QName typeName = new QName(targetType.getName().getNamespaceURI(),
				targetType.getDisplayName());
		String mappingName = null;

		if (mappingNames.containsKey(typeName)) {
			mappingName = mappingNames.get(typeName);
		}
		else {
			if (workspaceConf != null) {
				String namespaceUri = targetType.getName().getNamespaceURI();
				WorkspaceMetadata workspace = workspaceConf.getWorkspace(namespaceUri);
				if (workspace != null && workspace.isIsolated()) {
					mappingName = mappingNameGenerator.generateUniqueMappingName(typeName);
					mappingNames.put(typeName, mappingName);
				}
			}
		}

		return mappingWrapper.getOrCreateFeatureTypeMapping(targetType, mappingName);
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
	 * <p>
	 * If a not null nor empty mapping name is provided, this method simply
	 * delegates to
	 * {@link AppSchemaMappingWrapper#getOrCreateFeatureTypeMapping(TypeDefinition, String)}
	 * , without taking the workspace configuration into account; in other
	 * words, the mapping name provided by the caller is fully honored and thus
	 * it is the caller's responsibility to make sure it is really unique;
	 * otherwise, the method behaves exactly like
	 * {@link #getOrCreateFeatureTypeMapping(TypeDefinition)}.
	 * </p>
	 * 
	 * @param targetType the target type
	 * @param mappingName the mapping name
	 * @return the feature type mapping
	 */
	public FeatureTypeMapping getOrCreateFeatureTypeMapping(TypeDefinition targetType,
			String mappingName) {
		if (Strings.isNullOrEmpty(mappingName)) {
			return getOrCreateFeatureTypeMapping(targetType);
		}
		else {
			return mappingWrapper.getOrCreateFeatureTypeMapping(targetType, mappingName);
		}
	}

	/**
	 * Return the attribute mapping associated to the provided property.
	 * 
	 * <p>
	 * If an attribute mapping for the provided property already exists, it is
	 * returned; otherwise, a new one is created.
	 * </p>
	 * 
	 * <p>
	 * The implementation first retrieves (or creates, if necessary) the feature
	 * type mapping owning the attribute by calling
	 * {@link #getOrCreateFeatureTypeMapping(TypeDefinition, String)}, then
	 * delegates to
	 * {@link AppSchemaMappingWrapper#getOrCreateAttributeMapping(TypeDefinition, String, List)}
	 * .
	 * </p>
	 * 
	 * @param owningType the type owning the property
	 * @param mappingName the mapping name
	 * @param propertyPath the property path
	 * @return the attribute mapping
	 */
	public AttributeMappingType getOrCreateAttributeMapping(TypeDefinition owningType,
			String mappingName, List<ChildContext> propertyPath) {
		FeatureTypeMapping ftMapping = getOrCreateFeatureTypeMapping(owningType, mappingName);
		return mappingWrapper.getOrCreateAttributeMapping(owningType, ftMapping.getMappingName(),
				propertyPath);
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
	 * <p>
	 * The implementation first retrieves (or creates, if necessary) the feature
	 * type mapping owning the attribute by calling
	 * {@link #getOrCreateFeatureTypeMapping(TypeDefinition, String)}, then
	 * delegates to
	 * {@link AppSchemaMappingWrapper#getUniqueFeatureLinkAttribute(TypeDefinition, String)}
	 * .
	 * </p>
	 * 
	 * @param featureType the feature type
	 * @param mappingName the feature type's mapping name (may be
	 *            <code>null</code>)
	 * @return a unique <code>FEATURE_LINK[i]</code> attribute name
	 */
	public String getUniqueFeatureLinkAttribute(TypeDefinition featureType, String mappingName) {
		FeatureTypeMapping ftMapping = getOrCreateFeatureTypeMapping(featureType, mappingName);
		return mappingWrapper
				.getUniqueFeatureLinkAttribute(featureType, ftMapping.getMappingName());
	}
}
