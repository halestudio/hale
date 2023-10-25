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

package eu.esdihumboldt.hale.io.json.internal.schema

import javax.xml.namespace.QName

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.common.collect.HashMultiset
import com.google.common.collect.Multiset

import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition
import groovy.transform.Canonical
import groovy.transform.CompileStatic

/**
 * Information collected on a Json property across different instances.
 * 
 * @author Simon Templer
 */
@CompileStatic
@Canonical
class JsonProperty {
	/**
	 * Element type in case of an array (otherwise null)
	 */
	JsonProperty elements
	/**
	 * Children in case of an object (otherwise empty)
	 */
	final Map<String, JsonProperty> children = new LinkedHashMap<>()
	/**
	 * Primitive value types encountered 
	 */
	final Multiset<JsonValueType> types = HashMultiset.create();
	/**
	 * Geometry value types encountered
	 */
	final Multiset<Class<? extends Geometry>> geometryTypes = HashMultiset.create();

	private int valueCount = 0;

	/**
	 * @param value
	 */
	public void add(JsonNode value, boolean requireGeometry, SimpleLog log) {
		if (value == null || value.isMissingNode()) {
			return;
		}

		if (value.isObject()) {
			ObjectNode object = (ObjectNode) value

			// determine if this is a geometry
			JsonNode typeNode = object.get("type")
			Class<Geometry> geometryType = null
			if (typeNode != null && typeNode.isTextual()) {
				String type = typeNode.asText()

				JsonNode coordinatesNode = object.get("coordinates")
				boolean hasCoordinates = coordinatesNode != null && coordinatesNode.isArray()

				switch (type) {
					case "Point":
						if (hasCoordinates) {
							geometryType = Point.class
						}
						break;
					case "MultiPoint":
						if (hasCoordinates) {
							geometryType = MultiPoint.class
						}
						break;
					case "LineString":
						if (hasCoordinates) {
							geometryType = LineString.class
						}
						break;
					case "MultiLineString":
						if (hasCoordinates) {
							geometryType = MultiLineString.class
						}
						break;
					case "Polygon":
						if (hasCoordinates) {
							geometryType = Polygon.class
						}
						break;
					case "MultiPolygon":
						if (hasCoordinates) {
							geometryType = MultiPolygon.class
						}
						break;
					case "GeometryCollection":
						JsonNode geometriesNode = object.get("geometries")
						boolean hasGeometries = geometriesNode != null && geometriesNode.isArray()
						if (hasGeometries) {
							geometryType = GeometryCollection.class
						}
						break;
				}
			}

			if (geometryType != null) {
				geometryTypes.add(geometryType)
				valueCount++
			}
			else if (requireGeometry) {
				log.warn("Expected GeoJson geometry but object is not a valid geometry")
			}
			else {
				// handle generic object

				object.fields().forEachRemaining {
					// add child for each field
					children.computeIfAbsent(it.key) { new JsonProperty() }.add(it.value, false, log)
				}

				valueCount++
			}
		}
		else {
			if (requireGeometry) {
				log.warn("Unexpected node type {0}, expected GeoJson geometry", value.getClass().getSimpleName())
				return;
			}

			if (value.isArray()) {
				// handle array
				ArrayNode array = (ArrayNode) value

				array.elements().forEachRemaining {
					if (elements == null) {
						elements = new JsonProperty()
					}

					elements.add(it, false, log)
				}

				valueCount++
			}
			else if (value.isValueNode()) {
				JsonValueType valueType = JsonValueType.STRING
				if (value.isNumber()) {
					valueType = JsonValueType.NUMBER
				}
				else if (value.isBoolean()) {
					valueType = JsonValueType.BOOLEAN
				}
				//TODO more types / more fine-grained support?

				types.add(valueType);

				valueCount++
			}
		}
	}
	/**
	 * Build a property definition.
	 */
	public DefaultPropertyDefinition buildProperty(QName propertyName, DefinitionGroup parent, SchemaBuilderContext context) {
		// check if array
		if (elements != null) {
			// Note: directly nested arrays are currently not supported

			DefaultPropertyDefinition property = elements.buildProperty(propertyName, parent, context);

			// for arrays allow any number of elements by default
			property.setConstraint(Cardinality.CC_ANY_NUMBER);

			return property;
		}

		boolean primitive = false

		// determine property type
		TypeDefinition propertyType

		// 1. check if geometry
		if (!geometryTypes.isEmpty()) {
			propertyType = context.getGeometryType(mergeGeometryTypes(geometryTypes));
		}
		// 2. check if object
		else if (!children.isEmpty()) {
			QName nestedTypeName = new QName(parent.getIdentifier() + "/" + propertyName.getLocalPart(), "AnonymousType")
			propertyType = new DefaultTypeDefinition(nestedTypeName)

			children.forEach { name, property ->
				// build property and add to type
				property.buildProperty(new QName(name), propertyType, context);
			}
		}
		// 3. primitive type
		else {
			propertyType = context.getType(mergeValueTypes(types))
			primitive = true
		}

		// create and add property
		DefaultPropertyDefinition property = new DefaultPropertyDefinition(propertyName, parent, propertyType)

		// set constraints
		property.setConstraint(Cardinality.CC_OPTIONAL);
		if (primitive) {
			property.setConstraint(NillableFlag.ENABLED);
		}

		return property;
	}

	private Class<? extends Geometry> mergeGeometryTypes(Multiset<Class<? extends Geometry>> types) {
		if (types == null || types.empty) {
			// default to generic geometry
			Geometry.class
		}
		else if (types.elementSet().size() == 1) {
			// only one type
			types.iterator().next()
		}
		else {
			// mixed types
			boolean isMulti = types.contains(GeometryCollection.class) || types.contains(MultiPoint.class) ||
					types.contains(MultiLineString.class) || types.contains(MultiPolygon.class)
			if (isMulti) {
				GeometryCollection.class
			}
			else {
				Geometry.class
			}
		}
	}

	private JsonValueType mergeValueTypes(Multiset<JsonValueType> types) {
		if (types == null || types.empty) {
			// default to string
			JsonValueType.STRING
		}
		else if (types.elementSet().size() == 1) {
			// only one type
			types.iterator().next()
		}
		else {
			// mixed types
			//TODO use neutral binding?
			//XXX for now use string for mixed
			JsonValueType.STRING
		}
	}
}
