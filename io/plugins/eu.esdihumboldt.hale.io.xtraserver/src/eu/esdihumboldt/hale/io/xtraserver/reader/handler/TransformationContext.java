/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.reader.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import eu.esdihumboldt.hale.common.align.io.EntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.NamedEntityType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;

/**
 * 
 * 
 * @author zahnen
 */
public final class TransformationContext {

	private final TypeIndex sourceTypes;
	private final TypeIndex targetTypes;
	private final EntityResolver entityResolver;
	private final ProgressIndicator progress;
	private final IOReporter reporter;
	private Set<QName> currentSourceTableNames;
	private QName currentTargetTypeName;
	private FeatureTypeMapping currentFeatureTypeMapping;
	private ListMultimap<String, ParameterValue> currenTypeParameters;
	private ListMultimap<String, ParameterValue> currentPropertyParameters;
	private NamedEntityType currentSourcePropertyName;
	private NamedEntityType currentTargetPropertyName;

	/**
	 * Constructor Only the first schema is used
	 * 
	 * @param alignment the Alignment with all cells
	 * @param schemaspace the target schema
	 * @param transformationProperties Properties used in transformations
	 * @throws IOException if the schema cannot be read
	 */
	public TransformationContext(final TypeIndex sourceTypes, final TypeIndex targetTypes,
			final EntityResolver entityResolver, final ProgressIndicator progress,
			final IOReporter reporter) {
		this.sourceTypes = sourceTypes;
		this.targetTypes = targetTypes;
		this.entityResolver = entityResolver;
		this.progress = progress;
		this.reporter = reporter;
	}

	void nextTypeTransformation(String sourceType, FeatureTypeMapping featureTypeMapping) {
		this.currentSourceTableNames = new LinkedHashSet<>();
		addTable(sourceType);
		this.currentTargetTypeName = findTargetType(featureTypeMapping.getQName());
		this.currentFeatureTypeMapping = featureTypeMapping;
		this.currenTypeParameters = ArrayListMultimap.create();
	}

	void nextPropertyTransformation(List<QName> targetProperty) {

		nextPropertyTransformation(null, null, "", targetProperty);

	}

	void nextPropertyTransformation(String sourceType, String sourceProperty,
			List<QName> targetProperty) {

		nextPropertyTransformation(sourceType, sourceProperty, "", targetProperty);

	}

	void nextPropertyTransformation(String sourceType, String sourceProperty, String sourceVarName,
			List<QName> targetProperty) {

		if (sourceType != null && sourceProperty != null) {
			final QName sourceTableName = findCurrentSourceType(sourceType);

			// TODO: validate source and target types (or is the entitityResolver enough)

			this.currentSourcePropertyName = getNamedEntity(sourceTableName,
					new QName(sourceProperty), sourceVarName);
		}
		else {
			this.currentSourcePropertyName = null;
		}

		this.currentTargetPropertyName = getNamedEntity(currentTargetTypeName, targetProperty, "");

		this.currentPropertyParameters = ArrayListMultimap.create();
	}

	boolean hasCurrentSourceProperty() {
		return this.currentSourcePropertyName != null;
	}

	QName addTable(String name) {
		final QName qualifiedName = findSourceType(name);

		currentSourceTableNames.add(qualifiedName);

		return qualifiedName;
	}

	Set<QName> getCurrentSourceTableNames() {
		return this.currentSourceTableNames;
	}

	QName getCurrentTargetTypeName() {
		return this.currentTargetTypeName;
	}

	ListMultimap<String, ParameterValue> getCurrentTypeParameters() {
		return this.currenTypeParameters;
	}

	ListMultimap<String, ParameterValue> getCurrentPropertyParameters() {
		return this.currentPropertyParameters;
	}

	/**
	 * @return the currentFeatureTypeMapping
	 */
	FeatureTypeMapping getCurrentFeatureTypeMapping() {
		return currentFeatureTypeMapping;
	}

	IOReporter getReporter() {
		return reporter;
	}

	List<NamedEntityType> getCurrentSourceTableNamedEntities() {

		return currentSourceTableNames.stream().map(this::getNamedEntityType)
				.collect(Collectors.toList());
	}

	List<TypeEntityDefinition> getCurrentSourceTypeEntityDefinitions() {
		return currentSourceTableNames.stream()
				.map(sourceTableName -> new TypeEntityDefinition(
						new DefaultTypeDefinition(sourceTableName), SchemaSpaceID.SOURCE, null))
				.collect(Collectors.toList());
	}

	ListMultimap<String, Entity> getCurrentSourceTypeEntities() {

		return convertEntities(getCurrentSourceTableNamedEntities(), sourceTypes,
				SchemaSpaceID.SOURCE);
	}

	ListMultimap<String, Entity> getCurrentTargetTypeEntities() {

		return convertEntities(ImmutableList.of(getNamedEntity(currentTargetTypeName)), targetTypes,
				SchemaSpaceID.TARGET);
	}

	ListMultimap<String, Entity> getCurrentSourcePropertyEntities() {

		return convertEntities(ImmutableList.of(currentSourcePropertyName), sourceTypes,
				SchemaSpaceID.SOURCE);
	}

	ListMultimap<String, Entity> getCurrentTargetPropertyEntities() {

		return convertEntities(ImmutableList.of(currentTargetPropertyName), targetTypes,
				SchemaSpaceID.TARGET);
	}

	private ListMultimap<String, Entity> convertEntities(List<NamedEntityType> namedEntities,
			TypeIndex types, SchemaSpaceID schemaSpace) {
		if (namedEntities == null || namedEntities.isEmpty()) {
			return null;
		}

		ListMultimap<String, Entity> result = ArrayListMultimap.create();

		for (NamedEntityType namedEntity : namedEntities) {
			/**
			 * Resolve entity.
			 * 
			 * Possible results:
			 * <ul>
			 * <li>non-null entity - entity could be resolved</li>
			 * <li>null entity - entity could not be resolved, continue</li>
			 * <li>IllegalStateException - entity could not be resolved, reject cell</li>
			 * </ul>
			 */
			Entity entity = entityResolver.resolve(namedEntity.getAbstractEntity().getValue(),
					types, schemaSpace);

			if (entity != null) {
				result.put(namedEntity.getName(), entity);
			}
		}

		return result;
	}

	QName findSourceType(String name) {
		return sourceTypes.getTypes().stream().map(TypeDefinition::getName)
				.filter(type -> type.getLocalPart().equals(name)).findFirst().orElseThrow(
						() -> new IllegalArgumentException("Source type '" + name + "' not found"));
	}

	QName findTargetType(QName name) {
		if (name == null) {
			boolean b = true;
		}
		return targetTypes.getTypes().stream().map(TypeDefinition::getName).filter(name::equals)
				.findFirst().orElseThrow(
						() -> new IllegalArgumentException("Target type '" + name + "' not found"));
	}

	QName findCurrentSourceType(String name) {
		return currentSourceTableNames.stream()
				.filter(tableName -> name.equals(tableName.getLocalPart())).findFirst().orElseThrow(
						() -> new IllegalArgumentException("Source type '" + name + "' not found"));
	}

	NamedEntityType getNamedEntity(QName qname) {
		return getNamedEntity(qname, "");
	}

	NamedEntityType getNamedEntityType(QName qname) {
		return getNamedEntity(qname, "types");
	}

	NamedEntityType getNamedEntity(QName qname, String name) {
		ClassType.Type sourceQNT = new ClassType.Type();
		sourceQNT.setName(qname.getLocalPart());
		sourceQNT.setNs(qname.getNamespaceURI());
		ClassType sourceQN = new ClassType();
		sourceQN.setType(sourceQNT);
		NamedEntityType sourceType = new NamedEntityType();
		if (!name.isEmpty())
			sourceType.setName(name);
		sourceType.setAbstractEntity(
				new JAXBElement<ClassType>(new QName("type"), ClassType.class, sourceQN));

		return sourceType;
	}

	NamedEntityType getNamedEntity(QName qname, QName property) {
		return getNamedEntity(qname, property, "");
	}

	NamedEntityType getNamedEntity(QName qname, List<QName> properties) {
		return getNamedEntity(qname, properties, "");
	}

	private NamedEntityType getNamedEntity(QName qname, QName property, String name) {
		return getNamedEntity(qname, Lists.newArrayList(property), name);
	}

	private NamedEntityType getNamedEntity(QName qname, List<QName> properties, String name) {
		PropertyType.Type sourceQNT = new PropertyType.Type();
		sourceQNT.setName(qname.getLocalPart());
		sourceQNT.setNs(qname.getNamespaceURI());

		PropertyType sourceQN = new PropertyType();
		sourceQN.setType(sourceQNT);

		for (QName p : properties) {
			ChildContextType sourceP = new ChildContextType();
			sourceP.setName(p.getLocalPart());
			sourceP.setNs(p.getNamespaceURI());
			sourceQN.getChild().add(sourceP);
		}

		NamedEntityType sourceType = new NamedEntityType();
		if (!name.isEmpty())
			sourceType.setName(name);
		sourceType.setAbstractEntity(
				new JAXBElement<PropertyType>(new QName("type"), PropertyType.class, sourceQN));

		return sourceType;
	}

	PropertyEntityDefinition getEntityDefinition(QName qname, QName property) {
		TypeDefinition typeDefinition = new DefaultTypeDefinition(qname);

		ChildDefinition<PropertyConstraint> child = new DefaultPropertyDefinition(property,
				typeDefinition, typeDefinition);

		typeDefinition.addChild(child);

		List<ChildContext> path = new ArrayList<>();
		path.add(new ChildContext(child));

		return new PropertyEntityDefinition(typeDefinition, path, SchemaSpaceID.SOURCE, null);
	}
}
