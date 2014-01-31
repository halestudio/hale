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

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
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

		builder.'hsd:schema'(attributes) {
			//XXX what all to put in here? some kind of index? (all types, map. rel. types)
			//XXX for now just all types

			// add all types
			'hsd:types' {
				schema.types.each { TypeDefinition type ->
					typeToXml(builder, type)
				}
			}
		}
	}

	/**
	 * Create an XML representation of the given type definition.
	 *
	 * @param builder the XML builder
	 * @param type the type to serialize
	 * @return the builder return value for the type element
	 */
	static def typeToXml(def builder, TypeDefinition type) {
		builder.'hsd:type' {
			// definition content (QName, description, constraints)
			defToXml(builder, type)

			//TODO children (only declared children?!!)
			if (!type.declaredChildren.empty) {
				'hsd:declares' {
					type.declaredChildren.each { ChildDefinition<?> child ->
						if (child.asProperty() != null) {
							propertyToXml(builder, child.asProperty())
						}
						else if (child.asGroup() != null) {
							groupToXml(builder, child.asGroup())
						}
						else {
							throw new IllegalStateException('Unknown type of child definition encountered')
						}
					}
				}
			}

			//TODO super type
		}
	}

	/**
	 * Create an XML representation of the given property definition.
	 *
	 * @param builder the XML builder
	 * @param property the property to serialize
	 * @return the builder return value for the property element
	 */
	static def propertyToXml(def builder, PropertyDefinition property) {
		builder.'hsd:property' {
			// definition content (QName, description, constraints)
			defToXml(builder, property)

			//TODO property type
			//XXX what about parent type?
		}
	}

	/**
	 * Create an XML representation of the given group definition.
	 *
	 * @param builder the XML builder
	 * @param group the group property to serialize
	 * @return the builder return value for the group element
	 */
	static def groupToXml(def builder, GroupPropertyDefinition group) {
		builder.'hsd:group' {
			// definition content (QName, description, constraints)
			defToXml(builder, group)

			//TODO declared children
			//XXX what about parent type?
		}
	}

	/**
	 * Creates content (XML elements) for the XML representation of the
	 * definition.
	 *
	 * @param b the XML builder
	 * @param d the definition to serialize
	 */
	static void defToXml(def b, Definition<?> d) {
		// qualified name
		qNameToXml(b, d.name)
		// description
		if (d.description) {
			//TODO use Text object?
			b.'hsd:description'(d.description)
		}
		// constraints
		//TODO
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
