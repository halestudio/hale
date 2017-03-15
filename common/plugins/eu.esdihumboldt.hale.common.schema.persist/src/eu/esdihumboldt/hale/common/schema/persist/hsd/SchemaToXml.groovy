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

package eu.esdihumboldt.hale.common.schema.persist.hsd

import javax.xml.namespace.QName

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.core.io.DOMValueUtil
import eu.esdihumboldt.hale.common.core.io.Value
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
import eu.esdihumboldt.util.Pair
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import groovy.transform.CompileStatic


/**
 * Creates an XML representation from the HALE Schema Model.
 * 
 * @author Simon Templer
 */
@CompileStatic
class SchemaToXml extends SchemaEncoderBase implements HaleSchemaConstants {

	/**
	 * Create a default DOM builder to use with the *toXml methods for creating
	 * XML from the HALE schema model.
	 * 
	 * @return a new NSDOMBuilder instance with preconfigured namespace prefixes
	 */
	static NSDOMBuilder createBuilder() {
		NSDOMBuilder.newBuilder(HaleSchemaConstants.DEF_PREFIXES)
	}

	/**
	 * Create an XML representation of the given set of schemas.
	 * 
	 * @param builder the XML builder
	 * @param schemas the schemas to serialize
	 * @return the builder return value for the schemas element
	 */
	Element schemasToXml(NSDOMBuilder b, Iterable<? extends Schema> schemas) throws Exception {
		b 'hsd:schemas', {
			sortSchemas(schemas).each { Schema schema ->
				schemaToXml(b, schema)
			}
		}
	}

	/**
	 * Create an XML representation of the given schema.
	 *
	 * @param builder the XML builder
	 * @param schemas the schema to serialize
	 * @return the builder return value for the schema element
	 */
	Element schemaToXml(NSDOMBuilder b, Schema schema) throws Exception {
		def attributes = [:]
		if (schema.namespace) {
			attributes['namespace'] = schema.namespace
		}

		// organize types in a list
		List<TypeDefinition> types = getSchemaTypes(schema)

		Map<TypeDefinition, Value> typeIndex = [:]
		List<Integer> relevantTypes = []

		b 'hsd:schema', attributes, {
			// type names to index
			b 'hsd:type-index', {
				types.eachWithIndex { TypeDefinition type, int index ->
					// create type index and relevant types list
					typeIndex[type] = Value.simple(index as String)
					if (type.getConstraint(MappingRelevantFlag).enabled) {
						relevantTypes << index
					}

					// add index element
					b 'hsd:entry', [index: index], {
						qNameToXml(b, type.name)
					}
				}
			}

			// mapping relevant types index (list of indices)
			b 'hsd:mapping-relevant', relevantTypes.join(' ')

			// add all types
			b 'hsd:types', {
				types.each { TypeDefinition type ->
					typeToXml(b, type, new MapTypeReferenceBuilder(typeIndex))
				}
			}
		}
	}

	/**
	 * Create an XML representation of the given type definition.
	 *
	 * @param builder the XML builder
	 * @param type the type to serialize
	 * @param typeIndex the index that allows resolving type definitions
	 *   to an string index as reference
	 * @return the builder return value for the type element
	 */
	Element typeToXml(NSDOMBuilder b, TypeDefinition type, TypeReferenceBuilder typeIndex) throws Exception {
		// prepare attributes
		def attributes = [:]
		Optional<Value> ref = typeIndex.createReference(type)
		if (ref.isPresent()) {
			// assuming a string as type reference
			attributes.index = ref.get().as(String)
		}

		b 'hsd:type', attributes, {
			// definition content (QName, description, constraints)
			defToXml(b, type, typeIndex)

			// children (only declared!)
			defGroupToXml(b, type, typeIndex)

			// super type
			if (type.superType != null) {
				Optional<Value> superIndex = typeIndex.createReference(type.superType)
				if (superIndex.isPresent()) {
					// assuming a string as type reference
					b 'hsd:superType', [index: superIndex.get().as(String)]
				}
				else {
					//TODO warn?
				}
			}
		}
	}

	/**
	 * Create an XML representation of the given property definition.
	 *
	 * @param builder the XML builder
	 * @param property the property to serialize
	 * @param typeIndex the index that allows resolving type definitions
	 *   to an string index as reference
	 * @return the builder return value for the property element
	 */
	def propertyToXml(NSDOMBuilder b, PropertyDefinition property, TypeReferenceBuilder typeIndex) {
		b 'hsd:property', {
			// definition content (QName, description, constraints)
			defToXml(b, property, typeIndex)

			// property type
			Optional<Value> index = typeIndex.createReference(property.propertyType)
			if (index.isPresent()) {
				// assuming a string as type reference
				b 'hsd:propertyType', [index: index.get().as(String)]
			}
			else {
				// anonymous type (nested)
				b 'hsd:propertyType', {
					typeToXml(b, property.propertyType, typeIndex)
				}
			}

			//XXX what about parent type?
		}
	}

	/**
	 * Create an XML representation of the given group definition.
	 *
	 * @param builder the XML builder
	 * @param group the group property to serialize
	 * @param typeIndex the index that allows resolving type definitions
	 *   to an string index as reference
	 * @return the builder return value for the group element
	 */
	def groupToXml(NSDOMBuilder b, GroupPropertyDefinition group, TypeReferenceBuilder typeIndex) {
		b 'hsd:group', {
			// definition content (QName, description, constraints)
			defToXml(b, group, typeIndex)

			// declared children
			defGroupToXml(b, group, typeIndex)

			//XXX what about parent type?
		}
	}

	/**
	 * Creates content (XML elements) for the XML representation of the
	 * definition group.
	 *
	 * @param b the XML builder
	 * @param d the definition group to serialize
	 */
	def defGroupToXml(NSDOMBuilder b, DefinitionGroup group, TypeReferenceBuilder typeIndex) {
		if (!group.declaredChildren.empty) {
			b 'hsd:declares', {
				Collection<ChildDefinition<?>> children = (Collection<ChildDefinition<?>>) group.declaredChildren // Groovy CompileStatic can't deal properly with ? extends ...
				children.each { ChildDefinition<?> child ->
					if (child.asProperty() != null) {
						propertyToXml(b, child.asProperty(), typeIndex)
					}
					else if (child.asGroup() != null) {
						groupToXml(b, child.asGroup(), typeIndex)
					}
					else {
						throw new IllegalStateException('Unknown type of child definition encountered')
					}
				}
			}
		}
	}

	/**
	 * Creates content (XML elements) for the XML representation of the
	 * definition.
	 *
	 * @param b the XML builder
	 * @param d the definition to serialize
	 */
	void defToXml(NSDOMBuilder b, Definition<?> d, TypeReferenceBuilder typeIndex) {
		// qualified name
		qNameToXml(b, d.name)
		// description
		if (d.description) {
			//TODO use Text object?
			b 'hsd:description', d.description
		}

		getConstraints(d, typeIndex).each { Pair<String, Value> pair ->
			Value value = pair.second
			String id = pair.first
			if (value != null && value.value != null) {
				// add constraint definition represented as Value
				Element element = DOMValueUtil.valueTag(b, 'hsd:constraint', value)
				// add constraint type/id as attribute
				element.setAttribute('type', id)
			}
		}
	}

	/**
	 * Create an XML representation of a qualified name.
	 * 
	 * @param b the XML builder
	 * @param name the qualified name
	 * @return the builder return value for the name element
	 */
	def qNameToXml(NSDOMBuilder b, QName name) {
		if (name.namespaceURI) {
			b 'hsd:name', [namespace: name.namespaceURI], name.localPart
		}
		else {
			b 'hsd:name', name.localPart
		}
	}
}
