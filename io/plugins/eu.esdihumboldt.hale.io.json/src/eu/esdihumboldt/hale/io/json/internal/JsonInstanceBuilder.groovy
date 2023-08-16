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

import javax.xml.namespace.QName

import org.geotools.geojson.geom.GeometryJSON
import org.locationtech.jts.geom.Geometry

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode

import eu.esdihumboldt.hale.common.align.helper.EntityFinder
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition
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
	private final CRSDefinition defaultCrs

	private final Map<TypeDefinition, QName> cachedGeometryProperty = [:]

	/**
	 * Default constructor.
	 * @param namespaces 
	 */
	public JsonInstanceBuilder(SimpleLog log, NamespaceManager namespaces, CRSDefinition defaultCrs) {
		super();
		this.log = log
		this.namespaces = namespaces
		this.geometryJson = new GeometryJSON()
		this.defaultCrs = defaultCrs

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

		return builder.createInstance(type) {
			/*
			 * Handle explicitly provided geometry:
			 *
			 * 1. Determine geometry property in type that should be used
			 *
			 * 2. Process value for identified property (and skip property in further processing)
			 */
			Set<QName> skipProperty = new HashSet<>()
			if (geom != null) {
				QName geomProperty = getGeometryProperty(type)
				if (geomProperty != null) {
					GeometryProperty value = translateGeometry(geom)
					if (value != null) {
						builder.createProperty(geomProperty.localPart, geomProperty.namespaceURI, value)
						skipProperty.add(geomProperty)
					}
				}
				else {
					log.error("Could not identify geometry property for type {0}", type.name)
				}
			}

			properties.each { name, value ->
				// determine property
				//TODO also support groups/choices etc.?
				PropertyDefinition property = determineProperty(type, name)

				if (property == null) {
					log.warn("Could not find property with name {0} in type {1}", name, type.name)
				}
				else if (skipProperty.contains(property.name)) {
					log.warn("Skipping value for property  {0} in type {1}", name, type.name)
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

	private QName getGeometryProperty(TypeDefinition type) {
		return cachedGeometryProperty.computeIfAbsent(type) { TypeDefinition t ->
			// allow for geometry property types with choices
			int checkLevels = 3

			// create finder for geometry properties
			EntityFinder finder = new EntityFinder({ EntityDefinition entity ->
				// determine if the property classifies as
				if (entity.getDefinition() instanceof PropertyDefinition) {
					def propertyType = ((PropertyDefinition) entity.getDefinition()).getPropertyType()

					boolean isGeometry = propertyType.getConstraint(GeometryType).isGeometry()
					if (isGeometry) {
						return true
					}
				}

				false
			}, checkLevels)

			def parents = DefinitionUtil.getAllProperties(type).collect { PropertyDefinition p ->
				AlignmentUtil.createEntityFromDefinitions(type, [p], SchemaSpaceID.SOURCE, null)
			}

			def candidates = finder.find(parents)

			if (candidates.empty) {
				null
			}
			else {
				// select candidate

				// extract main property names; order matters because of traversal order for finding the candidates
				Set<QName> names = new LinkedHashSet(candidates*.propertyPath[0]*.child*.name)

				def preferred = null //XXX are there any ways we could prefer one candidate over the other?

				if (preferred == null) {
					// otherwise use first one
					preferred = names.iterator().next()
				}

				log.info("Identified property $preferred as geometry property for type ${type.name.localPart}")

				preferred
			}
		}
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

				extractNodeValue(value)
			}
		}
		else {
			//FIXME handle complex properties?
			//XXX for now ignore
			null
		}
	}

	private extractNodeValue(JsonNode value) {
		//TODO conversion necessary?
		//TODO support for specific types needed? (e.g. dates, timestamps, etc.)

		if (value.isValueNode()) {
			if (value.isBoolean()) {
				value.booleanValue()
			} else if (value.isTextual()) {
				value.textValue()
			} else if (value.isNumber()) {
				value.numberValue()
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

	public GeometryProperty translateGeometry(JsonNode value) {
		//TODO check if this is a valid geometry node?

		// geometry json works on a Json string, so encode again
		String geomJson = value.toString()

		Geometry geom = geometryJson.read(geomJson)
		if (geom != null) {
			new DefaultGeometryProperty(defaultCrs, geom)
		}
		else {
			null
		}
	}
}
