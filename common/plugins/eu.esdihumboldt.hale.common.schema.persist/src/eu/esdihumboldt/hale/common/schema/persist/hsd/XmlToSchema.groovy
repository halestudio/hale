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

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.report.IOReporter
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.MapTypeResolver
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeResolver
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
	public static Schema parseSchema(Reader reader, ClassResolver resolver, IOReporter reporter = null) throws Exception {
		Element root = DOMBuilder.parse(reader, false, true).documentElement
		switch (root.localName) {
			case 'schemas':
				List<Element> schemas = NSDOMCategory.children(root, NS, 'schema')
				if (schemas.empty) {
					// empty schema
					return new DefaultSchema(null, null)
				}
				else if (schemas.size() > 1) {
					List<Schema> loaded = []
					for (Element element : schemas) {
						Schema schema = parseSchema(element, resolver, reporter)
						if (schema != null) {
							loaded << schema
						}
					}
					return HaleSchemaUtil.combineSchema(loaded, reporter)
				}
				else {
					return parseSchema(schemas[0], resolver, reporter)
				}
			case 'schema':
				return parseSchema(root, resolver, reporter)
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
	public static Schema parseSchema(Element schema, ClassResolver resolver, IOReporter reporter = null) {
		use (NSDOMCategory) {
			DefaultSchema result = new DefaultSchema(schema.'@namespace', null)

			// maps indices to type definitions
			Map<Value, DefaultTypeDefinition> types = [:]
			Element typeIndex = schema.firstChild(NS, 'type-index')
			if (!typeIndex) {
				throw new IllegalStateException('Schema document misses type index')
			}

			typeIndex.children(NS, 'entry').each { Element entry ->

				// create an 'empty' type definition for each type
				QName typeName = parseName(entry.firstChild(NS, 'name'))
				types[Value.simple(entry.'@index')] =  new DefaultTypeDefinition(typeName)
			}

			Element typesElem = schema.firstChild(NS, 'types')
			typesElem?.children(NS, 'type').eachWithIndex { Element typeElem, int index ->

				// retrieve 'empty' type
				// prefer index stated in XML
				String lookup = typeElem.'@index'
				if (lookup == null || lookup.isEmpty()) {
					// fall back to node index
					lookup = index as String;
				}
				DefaultTypeDefinition typeDef = types[Value.simple(lookup)]

				// populate type
				parseType(typeElem, typeDef, new MapTypeResolver(types), resolver, reporter)

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
			TypeResolver typeIndex, ClassResolver resolver, IOReporter reporter) {
		// common definition stuff (description etc.)
		populateDefinition(typeElem, typeDef, typeIndex, resolver, reporter)

		// declared children
		populateGroup(typeElem, typeDef, typeIndex, resolver, reporter)

		// super type
		typeElem.firstChild(NS, 'superType')?.with {
			String superIndex = it.'@index'
			if (superIndex) {
				typeDef.superType = typeIndex.resolve(Value.simple(superIndex)).get()
			}
		}
	}

	private static void populateDefinition(Element defElem,
			AbstractDefinition definition, TypeResolver typeIndex,
			ClassResolver resolver, IOReporter reporter) {
		// description
		defElem.firstChild(NS, 'description')?.with {
			definition.description = it.text()
		}

		// constraints
		defElem.children(NS, 'constraint').each { Element constraintElem ->
			String id = constraintElem.'@type'
			if (id) {
				ValueConstraintFactoryDescriptor desc = ValueConstraintExtension.INSTANCE.get(id)

				if (desc != null && desc.factory != null) {
					Value config = DOMValueUtil.fromTag(constraintElem)
					try {
						Object constraint = desc.getFactory().restore(config, definition, typeIndex, resolver)
						definition.setConstraint(constraint)
					} catch (Exception e) {
						reporter.error(new IOMessageImpl("Failed to restore constraint of type $id", e))
					}
				}
				else {
					reporter.error(new IOMessageImpl("Could not find factory for constraint with type $id"))
				}
			}
			else {
				reporter.error(new IOMessageImpl('No type ID provided for constraint', null))
			}
		}
	}

	private static void populateGroup(Element defElem, DefinitionGroup group,
			TypeResolver typeIndex, ClassResolver resolver, IOReporter reporter) {
		defElem.firstChild(NS, 'declares')?.children()?.each { child ->
			if (child instanceof Element) {
				switch (child.localName) {
					case 'property':
						parseProperty(child, group, typeIndex, resolver, reporter)
						break;
					case 'group':
						parseGroup(child, group, typeIndex, resolver, reporter)
						break;
				}
			}
		}
	}

	private static DefaultPropertyDefinition parseProperty(Element propertyElem,
			DefinitionGroup parent, TypeResolver typeIndex,
			ClassResolver resolver, IOReporter reporter) {
		// name
		QName name = parseName(propertyElem.firstChild(NS, 'name'))

		// property type
		TypeDefinition propertyType;
		Element propertyTypeElem = propertyElem.firstChild(NS, 'propertyType')
		if (propertyTypeElem.hasAttribute('index')) {
			// references a property type from the index
			String index = propertyTypeElem.'@index'
			propertyType = typeIndex.resolve(Value.simple(index)).get();
		}
		else {
			// defines an anonymous type
			Element typeElem = propertyTypeElem.firstChild(NS, 'type')

			// determine anonymous type name & create empty type def
			QName typeName = parseName(typeElem.firstChild(NS, 'name'))
			DefaultTypeDefinition typeDef = new DefaultTypeDefinition(typeName)

			// populate anonymous type
			parseType(typeElem, typeDef, typeIndex, resolver, reporter)

			propertyType = typeDef
		}

		DefaultPropertyDefinition property = new DefaultPropertyDefinition(name, parent,
				propertyType)

		// common definition stuff (description etc.)
		populateDefinition(propertyElem, property, typeIndex, resolver, reporter)

		property
	}

	private static DefaultGroupPropertyDefinition parseGroup(Element groupElem,
			DefinitionGroup parent, TypeResolver typeIndex,
			ClassResolver resolver, IOReporter reporter) {
		// name
		QName name = parseName(groupElem.firstChild(NS, 'name'));

		DefaultGroupPropertyDefinition group = new DefaultGroupPropertyDefinition(name, parent,
				false)

		// common definition stuff (description etc.)
		populateDefinition(groupElem, group, typeIndex, resolver, reporter)

		// declared children
		populateGroup(groupElem, group, typeIndex, resolver, reporter)

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
