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

import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.impl.ValueListType
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintExtension
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintFactoryDescriptor
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder


/**
 * Creates an XML representation from the HALE Schema Model.
 * 
 * @author Simon Templer
 */
class SchemaToXml implements HaleSchemaConstants {

	/**
	 * Create a default DOM builder to use with the *toXml methods for creating
	 * XML from the HALE schema model.
	 * 
	 * @return a new NSDOMBuilder instance with preconfigured namespace prefixes
	 */
	static NSDOMBuilder createBuilder() {
		NSDOMBuilder.newInstance(DEF_PREFIXES)
	}

	/**
	 * Create an XML representation of the given set of schemas.
	 * 
	 * @param builder the XML builder
	 * @param schemas the schemas to serialize
	 * @return the builder return value for the schemas element
	 */
	static def schemasToXml(def builder, Iterable<? extends Schema> schemas) {
		builder.'hsd:schemas' {
			for (Schema schema in schemas) {
				schemaToXml(builder, schema)
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
	static def schemaToXml(def builder, Schema schema) {
		def attributes = [:]
		if (schema.namespace) {
			attributes['namespace'] = schema.namespace
		}

		// organize types in a list
		List types = []+ schema.types
		// sort to have a reproducible order (e.g. for versioning)
		types.sort()

		Map<TypeDefinition, String> typeIndex = [:]
		List<Integer> relevantTypes = []

		builder.'hsd:schema'(attributes) {
			// type names to index
			'hsd:type-index' {
				types.eachWithIndex { TypeDefinition type, int index ->
					// create type index and relevant types list
					typeIndex[type] = index as String
					if (type.getConstraint(MappingRelevantFlag).enabled) {
						relevantTypes << index
					}

					// add index element
					'hsd:entry'(index: index) {
						qNameToXml(builder, type.name)
					}
				}
			}

			// mapping relevant types index (list of indices)
			'hsd:mapping-relevant'(relevantTypes.join(' '))

			// add all types
			'hsd:types' {
				types.each { TypeDefinition type ->
					typeToXml(builder, type, typeIndex)
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
	 *   to an integer index as reference
	 * @return the builder return value for the type element
	 */
	static def typeToXml(def builder, TypeDefinition type, Map<TypeDefinition, String> typeIndex) {
		// prepare attributes
		def attributes = [:]
		String index = typeIndex.get(type)
		if (index != null) {
			attributes.index = index
		}

		builder.'hsd:type'(attributes) {
			// definition content (QName, description, constraints)
			defToXml(builder, type, typeIndex)

			// children (only declared!)
			defGroupToXml(builder, type, typeIndex)

			// super type
			if (type.superType != null) {
				Integer superIndex = typeIndex.get(type.superType)
				if (superIndex != null) {
					'hsd:superType'(index: superIndex)
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
	 *   to an integer index as reference
	 * @return the builder return value for the property element
	 */
	static def propertyToXml(def builder, PropertyDefinition property, Map<TypeDefinition, String> typeIndex) {
		builder.'hsd:property' {
			// definition content (QName, description, constraints)
			defToXml(builder, property, typeIndex)

			// property type
			String index = typeIndex.get(property.propertyType)
			if (index != null) {
				// type reference
				'hsd:propertyType'(index: index)
			}
			else {
				// anonymous type (nested)
				'hsd:propertyType' {
					typeToXml(builder, property.propertyType, typeIndex)
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
	 *   to an integer index as reference
	 * @return the builder return value for the group element
	 */
	static def groupToXml(def builder, GroupPropertyDefinition group, Map<TypeDefinition, String> typeIndex) {
		builder.'hsd:group' {
			// definition content (QName, description, constraints)
			defToXml(builder, group, typeIndex)

			// declared children
			defGroupToXml(builder, group, typeIndex)

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
	static def defGroupToXml(def builder, DefinitionGroup group, Map<TypeDefinition, String> typeIndex) {
		if (!group.declaredChildren.empty) {
			builder.'hsd:declares' {
				group.declaredChildren.each { ChildDefinition<?> child ->
					if (child.asProperty() != null) {
						propertyToXml(builder, child.asProperty(), typeIndex)
					}
					else if (child.asGroup() != null) {
						groupToXml(builder, child.asGroup(), typeIndex)
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
	static void defToXml(def b, Definition<?> d, Map<TypeDefinition, String> typeIndex) {
		// qualified name
		qNameToXml(b, d.name)
		// description
		if (d.description) {
			//TODO use Text object?
			b.'hsd:description'(d.description)
		}
		// constraints
		d.explicitConstraints.each { def constraint ->
			// get value constraint factory, if possible
			ValueConstraintFactoryDescriptor desc = ValueConstraintExtension.INSTANCE.getForConstraint(constraint)
			if (desc != null && desc.factory != null) {
				// determine value representation of constraint
				Value value = desc.factory.store(constraint, typeIndex)
				String id = desc.id
				if (value != null && value.value != null) {
					// add constraint definition represented as Value
					Element element = ValueListType.valueTag(b, 'hsd:constraint', value)
					// add constraint type/id as attribute
					element.setAttribute('type', id)
				}
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
	static def qNameToXml(def b, QName name) {
		if (name.namespaceURI) {
			b.'hsd:name'(namespace: name.namespaceURI,name.localPart)
		}
		else {
			b.'hsd:name'(name.localPart)
		}
	}
}
