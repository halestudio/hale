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

import java.util.Map;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.io.json.internal.AbstractJsonInstanceProcessor;
import eu.esdihumboldt.hale.io.json.internal.JsonReadMode;
import eu.esdihumboldt.hale.io.json.internal.JsonToInstance;
import eu.esdihumboldt.hale.io.json.internal.NamespaceManager;

/**
 * Creates a schema definition from Json objects.
 * 
 * @author Simon Templer
 */
public class JsonToSchema extends AbstractJsonInstanceProcessor<Void> {

	/**
	 * Default namespace for schemas and types.
	 */
	public static final String DEFAULT_NAMESPACE = "http://www.esdi-humboldt.eu/hale/json";

	private DefaultSchema schema;

	private final String typeNameHint;

	private final NamespaceManager namespaces;

	private final JsonTypes types;

	private final TypeIndex sharedTypes;

	/**
	 * Constructor.
	 * 
	 * @param mode the read mode for instances
	 * @param expectGeoJson if GeoJson is expected
	 * @param typeNameHint the name for the created type used if no other
	 *            information on the type can be found
	 * @param namespaces the namespace manager
	 * @param log the log
	 * @param sharedTypes the shared types
	 */
	public JsonToSchema(JsonReadMode mode, boolean expectGeoJson, String typeNameHint,
			NamespaceManager namespaces, SimpleLog log, TypeIndex sharedTypes) {
		super(mode, expectGeoJson);

		this.typeNameHint = typeNameHint;
		this.namespaces = namespaces;
		this.types = new JsonTypes(log);
		this.sharedTypes = sharedTypes;
	}

	@Override
	protected Void processInstance(Map<String, JsonNode> fields, ObjectNode geom,
			Map<String, JsonNode> properties) {
		// determine type name
		QName typeName;

		// extract @type information if present
		JsonNode typeField = fields.get("@type");
		if (typeField != null && typeField.isTextual()) {
			typeName = JsonToInstance.extractName(typeField.textValue(), namespaces);
		}
		else {
			typeName = new QName(DEFAULT_NAMESPACE, typeNameHint);
		}

		JsonType type = types.getType(typeName);

		if (geom != null) {
			type.addGeoJsonGeometry(geom);
		}

		properties.forEach((name, value) -> {
			type.addProperty(name, value);
		});

		return null;
	}

	/**
	 * @return the loaded schema
	 */
	public Schema getSchema() {
		if (schema == null) {
			schema = buildSchema();
		}
		return schema;
	}

	/**
	 * @return a schema built from the collected information
	 */
	private DefaultSchema buildSchema() {
		DefaultSchema schema = new DefaultSchema(DEFAULT_NAMESPACE, null);

		SchemaBuilderContext context = new SchemaBuilderContext(DEFAULT_NAMESPACE, sharedTypes);

		// build types
		types.buildTypes(context).forEach(t -> schema.addType(t));

		// add common types from context
		context.getCommonTypes().forEach(t -> schema.addType(t));

		return schema;
	}

}
