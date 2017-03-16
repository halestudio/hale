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

package eu.esdihumboldt.hale.common.schema.persist.hsd.json;

import javax.xml.namespace.QName

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.core.io.JsonValueUtil
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.report.IOReporter
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.MapTypeProvider
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeProvider
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintExtension
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintFactoryDescriptor
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultGroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition
import groovy.json.JsonSlurper

/**
 * Reads HALE Schema Definitions back to the HALE schema model.
 * 
 * @author Simon Templer
 */
public class JsonToSchema {

	private static final ALogger log = ALoggerFactory.getLogger(JsonToSchema.class);

	private final IOReporter reporter

	private TypeProvider typeProvider

	private final ClassResolver classResolver

	/**
	 * Create a new Json schema reader.
	 * 
	 * @param typeProvider the type provider, <code>null</code> to use the default provider if possible
	 * @param classResolver 
	 * @param reporter the reporter, may be <code>null</code> if not available
	 */
	public JsonToSchema(TypeProvider typeProvider, ClassResolver classResolver, IOReporter reporter) {
		super();
		this.reporter = reporter
		this.typeProvider = typeProvider
		this.classResolver = classResolver
	}

	protected void error(String message, Throwable e = null) {
		if (reporter) {
			reporter.error(new IOMessageImpl(message, e))
		}
		else {
			log.error(message, e)
		}
	}

	/**
	 * Read schemas from the given reader.
	 * 
	 * @param reader the reader, it's the callers responsibility to close it
	 * @return the schemas
	 * @throws Exception if an error occurs
	 */
	Iterable<Schema> parseSchemas(Reader reader) throws Exception {
		def root = new JsonSlurper().parse(reader)
		if (root.schemas) {
			// multiple schemas in an array
			root.schemas.collect { parseSchema(it) }
		}
		else {
			// single schema
			[parseSchema(root)]
		}
	}

	/**
	 * Parse a schema from a HSD Json schema element.
	 * 
	 * @param schema the schema element
	 * @return the created schema
	 */
	Schema parseSchema(def schema) {
		DefaultSchema result = new DefaultSchema(schema.namespace, null)

		// maps references to type definitions
		Map<Value, DefaultTypeDefinition> types = [:]

		if (!typeProvider) {
			def typeIndex = schema.typeIndex
			if (typeIndex) {
				typeIndex.each { def entry ->
					// create an 'empty' type definition for each type
					QName typeName = parseName(entry)
					types[JsonValueUtil.fromJson(entry.ref)] =  new DefaultTypeDefinition(typeName)
				}

				typeProvider = new MapTypeProvider(types)
			}
			else {
				throw new IllegalStateException('Schema document misses type index')
			}
		}

		schema.types?.each { def type ->
			// retrieve 'empty' type if it was created
			Value id = JsonValueUtil.fromJson(type.id)
			DefaultTypeDefinition typeDef = types[id]

			// populate and add type
			result.addType(parseType(type, typeDef))
		}

		result
	}

	/**
	 * Parse the given type element and populate the given type definition.
	 * 
	 * @param typeElem the element defining the type
	 * @param typeDef the type definition to populate or <code>null</code>
	 * @param typeIndex the type index mapping index identifiers to type
	 *            definitions
	 */
	TypeDefinition parseType(def type, DefaultTypeDefinition typeDef = null) {
		if (!typeDef) {
			Value id = JsonValueUtil.fromJson(type.id)
			QName name = parseName(type)
			if (typeProvider && id && !id.empty) {
				typeDef = typeProvider.getOrCreateType(name, id)
			}
			if (!typeDef) {
				typeDef = new DefaultTypeDefinition(name)
			}
		}

		// location
		if (type.location) {
			try {
				typeDef.location = URI.create(type.location)
			} catch (Exception e) {
				error('Could not assign location to type', e)
			}
		}

		// common definition stuff (description etc.)
		populateDefinition(type, typeDef)

		// declared children
		populateGroup(type, typeDef)

		// super type
		if (type.superType) {
			Value ref = JsonValueUtil.fromJson(type.superType.ref)
			Optional<TypeDefinition> superType = typeProvider.resolve(ref)
			if (superType.isPresent()) {
				typeDef.superType = superType.get()
			}
		}

		typeDef
	}

	private void populateDefinition(def obj, AbstractDefinition definition) {
		// description
		definition.description = obj.description

		// constraints
		obj.constraints?.each { String id, def value ->
			if (id) {
				ValueConstraintFactoryDescriptor desc = ValueConstraintExtension.INSTANCE.get(id)

				if (desc != null && desc.factory != null) {
					Value config = JsonValueUtil.fromJson(value)
					try {
						Object constraint = desc.getFactory().restore(config, definition, typeProvider, classResolver)
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
				error('No type ID provided for constraint')
			}
		}
	}

	private void populateGroup(def obj, DefinitionGroup group) {
		obj.declares?.each { child ->
			//XXX different way to detect?
			boolean isProperty = !!child.propertyType

			if (isProperty) {
				parseProperty(child, group)
			}
			else {
				parseGroup(child, group)
			}
		}
	}

	private DefaultPropertyDefinition parseProperty(def property,
			DefinitionGroup parent) {
		// name
		QName name = parseName(property)

		// property type
		TypeDefinition propertyType
		if (property.propertyType) {
			if (property.propertyType.ref != null) {
				// references a property type
				Value ref = JsonValueUtil.fromJson(property.propertyType.ref)
				Optional<TypeDefinition> type = typeProvider.resolve(ref)
				if (type.isPresent()) {
					propertyType = type.get()
				}
			}
			else {
				// anonymous type
				QName typeName = parseName(property.propertyType)
				DefaultTypeDefinition typeDef = new DefaultTypeDefinition(typeName)

				// populate anonymous type
				parseType(property.propertyType, typeDef)

				propertyType = typeDef
			}
		}

		DefaultPropertyDefinition propertyDef = new DefaultPropertyDefinition(name, parent,
				propertyType)

		// common definition stuff (description etc.)
		populateDefinition(property, propertyDef)

		propertyDef
	}

	private DefaultGroupPropertyDefinition parseGroup(def obj,
			DefinitionGroup parent) {
		// name
		QName name = parseName(obj);

		DefaultGroupPropertyDefinition group = new DefaultGroupPropertyDefinition(name, parent,
				false)

		// common definition stuff (description etc.)
		populateDefinition(obj, group)

		// declared children
		populateGroup(obj, group)

		group
	}

	private QName parseName(def obj) {
		if (obj?.name == null) {
			throw new IllegalStateException('No qualified name provided for definition')
		}

		if (obj.namespace) {
			new QName(obj.namespace, obj.name)
		}
		else {
			new QName(obj.name)
		}
	}
}
