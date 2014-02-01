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

package eu.esdihumboldt.hale.common.schema.persist.hsd;

import javax.xml.namespace.QName

import org.w3c.dom.Element

import de.cs3d.util.logging.ALogger
import de.cs3d.util.logging.ALoggerFactory
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.impl.ValueListType
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintExtension
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintFactoryDescriptor
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultGroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory
import groovy.transform.TypeChecked
import groovy.xml.DOMBuilder

/**
 * Reads HALE Schema Definitions back to the HALE schema model.
 * 
 * @author Simon Templer
 */
public class XmlToSchema implements HaleSchemaConstants {

	private static final ALogger log = ALoggerFactory.getLogger(XmlToSchema.class);

	/**
	 * Read a schema from the given reader.
	 * 
	 * @param reader the reader, it's the callers responsibility to close it
	 * @return the schema
	 * @throws Exception if an error occurs
	 */
	@TypeChecked
	public static Schema parseSchema(Reader reader) throws Exception {
		Element root = DOMBuilder.parse(reader, false, true).documentElement
		switch (root.localName) {
			case 'schemas':
				List<Element> schemas = NSDOMCategory.children(root, NS, 'schema')
				if (schemas.empty) {
					// empty schema
					return new DefaultSchema(null, null)
				}
				else if (schemas.size() > 1) {
					// FIXME report? combine? what?
				}
				return parseSchema(schemas[0])
			case 'schema':
				return parseSchema(root)
			default:
				throw new IllegalStateException('No schema element found in the document')
		}
	}

	/**
	 * Parse a schema from a HSD schema element.
	 * 
	 * @param schema the schema element
	 * @return the created schema
	 */
	public static Schema parseSchema(Element schema) {
		use (NSDOMCategory) {
			DefaultSchema result = new DefaultSchema(schema.'@namespace', null)

			// maps indices to type definitions
			Map<String, DefaultTypeDefinition> types = [:]
			Element typeIndex = schema.child(NS, 'type-index')
			if (!typeIndex) {
				throw new IllegalStateException('Schema document misses type index')
			}

			typeIndex.children(NS, 'entry').each { Element entry ->

				// create an 'empty' type definition for each type
				QName typeName = parseName(entry.child(NS, 'name'))
				types[entry.'@index'] =  new DefaultTypeDefinition(typeName)
			}

			Element typesElem = schema.child(NS, 'types')
			typesElem?.children(NS, 'type').eachWithIndex { Element typeElem, int index ->

				// retrieve 'empty' type
				// prefer index stated in XML
				String lookup = typeElem.'@index'
				if (lookup == null || lookup.isEmpty()) {
					// fall back to node index
					lookup = index as String;
				}
				DefaultTypeDefinition typeDef = types[lookup]

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
	 *            definitions
	 */
	private static void parseType(Element typeElem, DefaultTypeDefinition typeDef,
			Map<String, DefaultTypeDefinition> typeIndex) {
		// common definition stuff (description etc.)
		populateDefinition(typeElem, typeDef)

		// declared children
		populateGroup(typeElem, typeDef, typeIndex)

		// super type
		typeElem.child(NS, 'superType')?.with {
			String superIndex = it.'@index'
			if (superIndex) {
				typeDef.superType = typeIndex[superIndex]
			}
		}
	}

	private static void populateDefinition(Element defElem,
			AbstractDefinition definition) {
		// description
		defElem.child(NS, 'description')?.with {
			definition.description = it.text()
		}

		// constraints
		defElem.children(NS, 'constraint').each { Element constraintElem ->
			String id = constraintElem.'@type'
			if (id) {
				ValueConstraintFactoryDescriptor desc = ValueConstraintExtension.INSTANCE.get(id)

				if (desc != null && desc.factory != null) {
					Value config = ValueListType.fromTag(constraintElem)
					try {
						Object constraint = desc.getFactory().restore(config)
						definition.setConstraint(constraint)
					} catch (Exception e) {
						log.error("Failed to restore constraint of type $id", e)
						// TODO report?
					}
				}
				else {
					log.error("Could not find factory for constraint with type $id")
					// TODO report?
				}
			}
			else {
				log.warn('No type ID provided for constraint')
				// TODO report?
			}
		}
	}

	private static void populateGroup(Element defElem, DefinitionGroup group,
			Map<String, DefaultTypeDefinition> typeIndex) {
		defElem.child(NS, 'declares')?.children()?.each { child ->
			if (child instanceof Element) {
				switch (child.localName) {
					case 'property':
						parseProperty(child, group, typeIndex);
						break;
					case 'group':
						parseGroup(child, group, typeIndex);
						break;
				}
			}
		}
	}

	private static DefaultPropertyDefinition parseProperty(Element propertyElem,
			DefinitionGroup parent, Map<String, DefaultTypeDefinition> typeIndex) {
		// name
		QName name = parseName(propertyElem.child(NS, 'name'))

		// property type
		TypeDefinition propertyType;
		Element propertyTypeElem = propertyElem.child(NS, 'propertyType')
		if (propertyTypeElem.hasAttribute('index')) {
			// references a property type from the index
			String index = propertyTypeElem.'@index'
			propertyType = typeIndex.get(index);
		}
		else {
			// defines an anonymous type
			Element typeElem = propertyTypeElem.child(NS, 'type')

			// determine anonymous type name & create empty type def
			QName typeName = parseName(typeElem.child(NS, 'name'))
			DefaultTypeDefinition typeDef = new DefaultTypeDefinition(typeName)

			// populate anonymous type
			parseType(typeElem, typeDef, typeIndex)

			propertyType = typeDef
		}

		DefaultPropertyDefinition property = new DefaultPropertyDefinition(name, parent,
				propertyType)

		// common definition stuff (description etc.)
		populateDefinition(propertyElem, property)

		property
	}

	private static DefaultGroupPropertyDefinition parseGroup(Element groupElem,
			DefinitionGroup parent, Map<String, DefaultTypeDefinition> typeIndex) {
		// name
		QName name = parseName(groupElem.child(NS, 'name'));

		DefaultGroupPropertyDefinition group = new DefaultGroupPropertyDefinition(name, parent,
				false)

		// common definition stuff (description etc.)
		populateDefinition(groupElem, group)

		// declared children
		populateGroup(groupElem, group, typeIndex)

		group
	}

	private static QName parseName(Element name) {
		if (name == null) {
			throw new IllegalStateException('No qualified name provided for definition')
		}

		if (name.hasAttribute('namespace')) {
			new QName(name.'@namespace', name.text())
		}
		else {
			new QName(name.text())
		}
	}
}
