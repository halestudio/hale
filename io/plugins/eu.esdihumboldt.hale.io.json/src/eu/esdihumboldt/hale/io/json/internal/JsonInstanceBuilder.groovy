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

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.feature.FeatureJSON
import org.geotools.geojson.geom.GeometryJSON
import org.geotools.referencing.CRS
import org.locationtech.jts.geom.Geometry
import org.opengis.feature.simple.SimpleFeature
import org.opengis.referencing.crs.CoordinateReferenceSystem

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode

import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag
import eu.esdihumboldt.hale.io.json.writer.NamespaceManager

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

						// Iterate over the elements of the array
						value.each { element ->
							JsonNode item = element
							def itemValue = translateValue(item, property)

							if (itemValue != null) {
								builder.createProperty(property.name.localPart, property.name.namespaceURI, itemValue)
							}
						}
					} else {
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
		def candidate = properties.find {
			it.name.localPart == name
		}

		candidate
	}

	public Object translateValue(JsonNode value, PropertyDefinition property) {
		def type = property.getPropertyType()

		if (type.getConstraint(GeometryType).isGeometry()) {
			translateGeometry(value, property)
		} else if (type.children.empty) {
			// no children -> no group or instance

			if (type.getConstraint(HasValueFlag)) {
				// handle simple value
				//TODO conversion necessary?
				//TODO support for specific types needed? (e.g. dates, timestamps, etc.)
				extractNodeValue(value)
			}
		} else {
			//FIXME handle complex properties?
			//XXX for now ignore
			// children -> group or instance

			def children = type.children;

			try {
				children.each { arrayElement ->
					arrayElement.displayName
				}
			} catch(Exception e) {
			}
		}
	}


	private extractNodeValue(JsonNode value) {
		if (value.isValueNode()) {
			if (value.isBoolean()) {
				value.booleanValue()
			} else if (value.isTextual()) {
				value.textValue()
			} else if (value.isBigDecimal()) {
				value.bigDecimal()
			} else if (value.isBigInteger()) {
				value.bigIntegerValue()
			} else if (value.isBinary()) {
				value.binaryValue()
			} else if (value.isDouble()) {
				value.doubleValue()
			} else if (value.isFloat()) {
				value.floatValue()
			} else if (value.isFloatingPointNumber()) {
				value.floatingPointNumber()
			} else if (value.isInt()) {
				value.intValue()
			} else if (value.isIntegralNumber()) {
				value.integralNumber()
			} else if (value.isLong()) {
				value.longValue()
			} else if (value.isNumber()) {
				value.numberValue()
			} else if (value.isShort()) {
				value.shortValue()
			}
			//FIXME add all other cases
			else {
				// unhandled type
				// TODO log warning/error?
				value.asText()
			}
		} else {
			//XXX what to do in this case? For now use string representation
			value.toString()
		}
	}

	public GeometryProperty translateGeometry(JsonNode jsonNode, PropertyDefinition property) {
		//TODO implement geometry parsing using Geotools functionality
		//XXX for now return null

		// Extract the geometry from the JsonNode
		def geometry = geometryJson.read(jsonNode.toString())

		// Set the SRID for the geometry (Replace 4326 with the appropriate SRID for your data)
		geometry.setSRID(4326)

		// Get the SRID of the geometry
		int srid = geometry.getSRID()

		// Get the CoordinateReferenceSystem from the SRID
		CoordinateReferenceSystem crs = CRS.decode("EPSG:$srid")
		CRSDefinition sourceCrs = new CodeDefinition("EPSG:4326", true);

		DefaultGeometryProperty defaultGeometryProperty = new DefaultGeometryProperty<Geometry>(sourceCrs, geometry);
		return defaultGeometryProperty;


		// Create a FeatureJSON instance
		FeatureJSON featureJSON = new FeatureJSON();

		// Parse the JsonNode and get the feature source
		SimpleFeatureSource featureSource = featureJSON.readFeatureCollection(jsonNode);

		// Get an iterator over the features in the GeoJSON
		SimpleFeatureIterator featureIterator = featureSource.getFeatures().features();


		DefaultGeometryProperty value;

		// Process each feature in the GeoJSON
		while (featureIterator.hasNext()) {
			SimpleFeature feature = featureIterator.next();
			Geometry geometry1 = (Geometry) feature.getDefaultGeometry();

			// Use the parsed geometry as needed
			System.out.println("Parsed Geometry: " + geometry1);
			CoordinateReferenceSystem crsDef1 = feature.getFeatureType().getCoordinateReferenceSystem();
			value = new DefaultGeometryProperty<Geometry>(crsDef1, geometry1);
		}

		// Close the iterator
		featureIterator.close();
		return value
	}
}
