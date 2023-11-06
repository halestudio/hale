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

package eu.esdihumboldt.hale.io.json.internal.schema;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.json.internal.InstanceJsonConstants;

/**
 * Represents a specific type of Json object.
 * 
 * @author Simon Templer
 */
public class JsonType {

	/**
	 * Name of the GeoJson geometry property in a type.
	 */
	public static final QName GEOJSON_GEOMETRY_NAME = new QName(
			InstanceJsonConstants.NAMESPACE_GEOJSON, "geometry");

	private final QName name;

	private final SimpleLog log;

	/**
	 * @param name the name of the type
	 * @param log the log
	 */
	public JsonType(QName name, SimpleLog log) {
		this.name = name;
		this.log = log;
	}

	private final Map<String, JsonProperty> properties = new LinkedHashMap<>();

	private final JsonProperty geoJsonGeometry = new JsonProperty();

	/**
	 * Build the type definition from the collected information.
	 * 
	 * @param context the schema building context
	 * 
	 * @return the type definition
	 */
	public TypeDefinition buildType(SchemaBuilderContext context) {
		DefaultTypeDefinition type = new DefaultTypeDefinition(name);

		if (!geoJsonGeometry.getGeometryTypes().isEmpty()) {
			geoJsonGeometry.buildProperty(GEOJSON_GEOMETRY_NAME, type, context);
		}

		properties.forEach((name, property) -> {
			// build property and add to type
			property.buildProperty(new QName(name), type, context);
		});

		type.setConstraint(MappableFlag.ENABLED);
		type.setConstraint(MappingRelevantFlag.ENABLED);

		return type;
	}

	/**
	 * Add a GeoJson geometry node.
	 * 
	 * @param geom the geometry node
	 */
	public void addGeoJsonGeometry(ObjectNode geom) {
		geoJsonGeometry.add(geom, true, log);
	}

	/**
	 * Add a property based on a Json node.
	 * 
	 * @param name the property name
	 * @param value the Json value
	 */
	public void addProperty(String name, JsonNode value) {
		properties.computeIfAbsent(name, n -> new JsonProperty()).add(value, false, log);
	}

}
