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
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintExtension
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintFactoryDescriptor
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition
import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory



/**
 * Reads HALE Schema Definitions back to the HALE schema model.
 * 
 * @author Simon Templer
 */
class XmlToSchema {

	/**
	 * Read a schema from the given reader.
	 * 
	 * @param reader the reader, it's the callers responsibility to close it
	 * @return the schema
	 * @throws Exception if an error occurs
	 */
	static Schema parseSchema(Reader reader) throws Exception {
		Element root = DOMBuilder.parse(reader, false, true);
		use (DOMCategory) {
			switch (root.name()) {
				case 'schemas':
					int size = root.schema.size()
					if (size == 0) {
						// empty schema
						return new DefaultSchema(null, null)
					} else if (size > 1) {
						//FIXME report? combine? what?
					}
					return parseSchema(root.schema[0])
				case 'schema':
					return parseSchema(root)
			}
		}
	}

	/**
	 * Parse a schema from a HSD schema element.
	 * 
	 * @param schema the schema element
	 * @return the created schema
	 */
	static Schema parseSchema(Element schema) {
		use (DOMCategory) {
			DefaultSchema result = new DefaultSchema(schema.'@namespace', null)

			// maps indices to type definitions
			Map<String, DefaultTypeDefinition> types = [:]
			schema.'type-index'.entry.each { Element entry ->
				// create an 'empty' type definition for each type
				QName typeName = parseName(entry.name[0])
				types[entry.'@index' as String] = new DefaultTypeDefinition(typeName)
			}

			schema.types.type.eachWithIndex { Element typeElem, int index ->
				// retrieve 'empty' type
				// prefer index stated in XML
				def lookup = typeElem.'@index'
				if (!lookup) {
					// fall back to node index
					lookup = index
				}
				def typeDef = types[index as String]

				// populate type
				parseType(typeElem, typeDef, types)

				result.addType(typeDef)
			}

			result
		}
	}

	/**
	 * Parse the given type element and populate the given type definition.
	 * 
	 * @param typeElem the element defining the type
	 * @param typeDef the type definition to populate
	 * @param typeIndex the type index mapping index identifiers to type
	 *   definitions 
	 */
	static void parseType(Element typeElem, DefaultTypeDefinition typeDef,
			Map<String, DefaultTypeDefinition> typeIndex) {
		// common definition stuff (description etc.)
		populateDefinition(typeElem, typeDef)

		// declared children
		populateGroup(typeElem, typeDef, typeIndex)

		// super type
		use(DOMCategory) {
			def superIndex = typeElem.'superType'.list().first()?.'@index'
			if (superIndex) {
				typeDef.superType = typeIndex[superIndex as String]
			}
		}
	}

	static void populateDefinition(Element defElem, AbstractDefinition<?> definition) {
		// description
		with (defElem.description) {
			if (!empty) {
				definition.description = getAt(0).text()
			}
		}

		// constraints
		defElem.'constraint'.each {
			String id = it.'@type'

			if (id != null) {
				ValueConstraintFactoryDescriptor desc = ValueConstraintExtension.INSTANCE.get(id)

				if (desc != null && desc.factory != null) {
					Value config = ValueListType.fromTag(it)
					// try {
					def constraint = desc.factory.restore(config)
					definition.setConstraint(constraint)
					// } catch (e) {
					// log
					// }
				}
				else {
					//TODO log?
				}
			}
			else {
				//TODO log?
			}
		}
	}

	static void populateGroup(Element defElem, AbstractDefinition<?> definition) {
		//TODO
	}

	static QName parseName(Element name) {
		use (DOMCategory) {
		}
	}
}
