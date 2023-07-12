/*
 * Copyright (c) 2023 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */
package eu.esdihumboldt.hale.io.json.internal

import org.geotools.geojson.geom.GeometryJSON

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode

import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag

/**
 * Instance builder for Json/GeoJson.
 * 
 * @author Simon Templer
 */
class JsonInstanceBuilder {

	private final SimpleLog log
	private final InstanceBuilder builder
	private final GeometryJSON geometryJson
	private final NamespaceManager namespaces

	/**
	 * Default constructor.
	 * @param namespaces 
	 */
	public JsonInstanceBuilder(SimpleLog log, NamespaceManager namespaces) {
		super();
		this.log = log
		this.namespaces = namespaces
		this.geometryJson = new GeometryJSON()

		builder = new InstanceBuilder(
				strictBinding: false,
				strictValueFlags: false // allow setting values even if no value is expected (mainly for use with XML schema and geometries)
				)
	}

	/**
	 * Build an instance of the given type.
	 *
	 * @param type the type definition or <code>null</code> if without schema
	 * @param geom a dedicated (GeoJson) geometry object or <code>null</code>
	 * @param properties map of properties or <code>null</code>
	 * @return the created instance
	 */
	public Instance buildInstance(TypeDefinition type, ObjectNode geom,
			Map<String, JsonNode> properties) {
		if (type == null) {
			// TODO implement schema-less mode
			throw new UnsupportedOperationException("Schema-less mode not implemented yet");
		}

		/*
		 * TODO Handle explicitly provided geometry:
		 * 
		 * 1. Determine geometry property in type that should be used
		 * 
		 * 2. Process value for identified property (XXX takes precedence over otherwise provided value? or should it be dropped if there is a value in properties)
		 */

		return builder.createInstance(type) {
			properties.each { name, value ->
				// determine property
				//TODO also support groups/choices etc.?
				PropertyDefinition property = determineProperty(type, name)

				if (property == null) {
					log.warn("Could not find property with name {0} in type {1}", name, type.name)
				}
				else {
					if (value.isArray()) {
						// add each value
						def iterator = value.getElements()
						while (iterator.hasNext()) {
							JsonNode item = iterator.next()
							def itemValue = translateValue(item, property)

							if (itemValue != null) {
								builder.createProperty(property.name.localPart, property.name.namespaceURI, itemValue)
							}
						}
					}
					else {
						// add individual value
						def itemValue = translateValue(value, property)

						if (itemValue != null) {
							builder.createProperty(property.name.localPart, property.name.namespaceURI, itemValue)
						}
					}
				}
			}
		}
	}

	public PropertyDefinition determineProperty(DefinitionGroup parent, String name) {
		//TODO should ideally extract namespace prefix and respect namespace mapping (not supported for now)

		def properties = DefinitionUtil.getAllProperties(parent)
		//XXX for now only a simple condition, matching the field name with the local property name
		def candidate = properties.find { it.name.localPart == name }

		candidate
	}

	public Object translateValue(JsonNode value, PropertyDefinition property) {
		def type = property.getPropertyType()

		if (type.getConstraint(GeometryType).isGeometry()) {
			translateGeometry(value)
		}
		else if (type.children.empty) {
			// no children -> no group or instance

			if (type.getConstraint(HasValueFlag)) {
				// handle simple value

				//TODO conversion necessary?
				//TODO support for specific types needed? (e.g. dates, timestamps, etc.)

				if (value.isValueNode()) {
					if (value.isBoolean()) {
						value.booleanValue()
					}
					else if (value.isTextual()) {
						value.textValue()
					}
					//FIXME add all other cases
					else {
						// unhandled type
						// TODO log warning/error?
						value.asText()
					}
				}
				else {
					//XXX what to do in this case? For now use string representation
					value.toString()
				}
			}
		}
		else {
			//FIXME handle complex properties?
			//XXX for now ignore
			null
		}
	}

	public GeometryProperty translateGeometry(JsonNode value) {
		//TODO implement geometry parsing using Geotools functionality
		//XXX for now return null
		null
	}
}
