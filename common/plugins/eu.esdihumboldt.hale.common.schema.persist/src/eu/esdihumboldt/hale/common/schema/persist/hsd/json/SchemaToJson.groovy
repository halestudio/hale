/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.persist.hsd.json

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.core.io.JsonValueUtil
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.ValueList
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.MapTypeReferenceBuilder
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeReferenceBuilder
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag
import eu.esdihumboldt.hale.common.schema.persist.hsd.SchemaEncoderBase
import eu.esdihumboldt.util.Pair
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder
import groovy.transform.CompileStatic


/**
 * Creates a JSON representation from the HALE Schema Model.
 * 
 * @author Simon Templer
 */
@CompileStatic
class SchemaToJson extends SchemaEncoderBase {

	/**
	 * Create a JSON representation of the given set of schemas.
	 * 
	 * @param b the JSON builder
	 * @param schemas the schemas to serialize
	 * @param refBuilder the type reference builder
	 * @return the builder return value for the schemas element
	 */
	void schemasToJson(JsonStreamBuilder b, Iterable<? extends Schema> schemas,
			TypeReferenceBuilder refBuilder) throws Exception {
		b {
			sortSchemas(schemas).each { Schema schema ->
				b 'schemas[]', { schemaToJson(b, schema, refBuilder) }
			}
		}
	}

	/**
	 * Create a JSON representation of the given schema.
	 *
	 * @param b the JSON builder
	 * @param schemas the schema to serialize
	 * @param refBuilder the type reference builder
	 * @return the builder return value for the schema element
	 */
	void schemaToJson(JsonStreamBuilder b, Schema schema, TypeReferenceBuilder refBuilder) throws Exception {
		// organize types in a list
		List<TypeDefinition> types = getSchemaTypes(schema)

		Map<TypeDefinition, Value> typeIndex = [:]
		ValueList relevantTypes = new ValueList()

		b {
			if (schema.namespace) {
				b 'namespace', schema.namespace
			}
			// type names to index
			types.eachWithIndex { TypeDefinition type, int index ->
				// create type index and relevant types list
				Value ref
				if (refBuilder) {
					ref = refBuilder.createReference(type).orElseGet(null)
				}
				else {
					ref = Value.simple(index)
					typeIndex[type] = ref
				}

				if (ref) {
					// add index element
					b 'typeIndex[]', {
						b 'ref', JsonValueUtil.valueJson(ref)
						qNameToJson(b, type.name)
					}

					if (type.getConstraint(MappingRelevantFlag).enabled) {
						relevantTypes << ref
					}
				} else {
					//TODO report error?
				}
			}

			// mapping relevant types index (list of refs)
			b 'mappingRelevant', JsonValueUtil.valueJson(relevantTypes.toValue())

			// add all types
			types.each { TypeDefinition type ->
				b 'types[]', {
					typeToJson(b, type, refBuilder ?: new MapTypeReferenceBuilder(typeIndex))
				}
			}
		}
	}

	/**
	 * Create a JSON representation of the given type definition.
	 *
	 * @param b the JSON builder
	 * @param type the type to serialize
	 * @param refBuilder the type reference builder
	 * @return the builder return value for the type element
	 */
	void typeToJson(JsonStreamBuilder b, TypeDefinition type, TypeReferenceBuilder refBuilder) throws Exception {
		b {
			// id / reference related to index
			Optional<Value> ref = refBuilder.createReference(type)
			if (ref.isPresent()) {
				// assuming a string as type reference
				b 'id', JsonValueUtil.valueJson(ref.get())
			}

			// add location information
			//XXX instead add for all definitions?
			if (type.location) {
				b 'location', type.location.toString()
			}

			// definition content (QName, description, constraints)
			defToJson(b, type, refBuilder)

			// children (only declared!)
			defGroupToJson(b, type, refBuilder)

			// super type
			if (type.superType != null) {
				Optional<Value> superRef = refBuilder.createReference(type.superType)
				if (superRef.isPresent()) {
					b 'superType', {
						b 'ref', JsonValueUtil.valueJson(superRef.get())
					}
				}
				else {
					//TODO warn?
				}
			}
		}
	}

	/**
	 * Create a JSON representation of the given property definition.
	 *
	 * @param b the XML builder
	 * @param property the property to serialize
	 * @param refBuilder the type reference builder
	 * @return the builder return value for the property element
	 */
	def propertyToJson(JsonStreamBuilder b, PropertyDefinition property, TypeReferenceBuilder refBuilder) {
		b {
			// definition content (QName, description, constraints)
			defToJson(b, property, refBuilder)

			// property type
			Optional<Value> ref = refBuilder.createReference(property.propertyType)
			if (ref.isPresent()) {
				// assuming a string as type reference
				b 'propertyType', {
					b 'ref', JsonValueUtil.valueJson(ref.get())
				}
			}
			else {
				// anonymous type (nested)
				b 'propertyType', {
					typeToJson(b, property.propertyType, refBuilder)
				}
			}

			//XXX what about parent type?
		}
	}

	/**
	 * Create a JSON representation of the given group definition.
	 *
	 * @param b the JSON builder
	 * @param group the group property to serialize
	 * @param refBuilder the type reference builder
	 * @return the builder return value for the group element
	 */
	def groupToJson(JsonStreamBuilder b, GroupPropertyDefinition group, TypeReferenceBuilder refBuilder) {
		b {
			// mark as group?
			//b 'group', true

			// definition content (QName, description, constraints)
			defToJson(b, group, refBuilder)

			// declared children
			defGroupToJson(b, group, refBuilder)

			//XXX what about parent type?
		}
	}

	/**
	 * Creates content for the JSON representation of the definition group.
	 *
	 * @param b the JSON builder
	 * @param group the definition group to serialize
	 * @param refBuilder the type reference builder
	 */
	def defGroupToJson(JsonStreamBuilder b, DefinitionGroup group, TypeReferenceBuilder refBuilder) {
		if (!group.declaredChildren.empty) {
			Collection<ChildDefinition<?>> children = (Collection<ChildDefinition<?>>) group.declaredChildren // Groovy CompileStatic can't deal properly with ? extends ...
			children.each { ChildDefinition<?> child ->
				b 'declares[]', {
					if (child.asProperty() != null) {
						propertyToJson(b, child.asProperty(), refBuilder)
					}
					else if (child.asGroup() != null) {
						groupToJson(b, child.asGroup(), refBuilder)
					}
					else {
						throw new IllegalStateException('Unknown type of child definition encountered')
					}
				}
			}
		}
	}

	/**
	 * Creates content for the JSON representation of the definition.
	 *
	 * @param b the JSON builder
	 * @param d the definition to serialize
	 * @param refBuilder the type reference builder
	 */
	void defToJson(JsonStreamBuilder b, Definition<?> d, TypeReferenceBuilder refBuilder) {
		// qualified name
		qNameToJson(b, d.name)
		// description
		if (d.description) {
			b 'description', d.description
		}

		b 'constraints', {
			getConstraints(d, refBuilder).each { Pair<String, Value> pair ->
				Value value = pair.second
				String id = pair.first
				if (value != null && value.value != null) {
					b id, JsonValueUtil.valueJson(value)
				}
			}
		}
	}

	/**
	 * Create a JSON representation of a qualified name as content for an object.
	 * 
	 * @param b the JSON builder
	 * @param name the qualified name
	 * @return the builder return value for the name element
	 */
	def qNameToJson(JsonStreamBuilder b, QName name) {
		b 'name', name.localPart
		if (name.namespaceURI) {
			b 'namespace', name.namespaceURI
		}
	}
}
