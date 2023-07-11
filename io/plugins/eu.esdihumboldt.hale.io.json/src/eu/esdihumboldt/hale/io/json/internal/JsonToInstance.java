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

package eu.esdihumboldt.hale.io.json.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.node.ObjectNode;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Class to read instances from JSON.
 * 
 * @author Simon Templer
 */
public class JsonToInstance implements InstanceJsonConstants {

	private final NamespaceManager namespaces;
	private final boolean expectGeoJson;

	private final JsonInstanceBuilder builder;

	// GeoJson expects WGS 84 with lon/lat (see
	// https://tools.ietf.org/html/rfc7946)
	private final CRSDefinition sourceCrs = new CodeDefinition("EPSG:4326", true);

	private final TypeDefinition featureType;
	private final SimpleLog log;

	/**
	 * 
	 * @param expectGeoJson if the input is expected to be GeoJson
	 * @param featureType the feature type to use for all features or
	 *            <code>null</code>
	 */
	public JsonToInstance(boolean expectGeoJson, TypeDefinition featureType, SimpleLog log) {
		this(expectGeoJson, featureType, log,
				new IgnoreNamespaces() /* new JsonNamespaces() */);
	}

	/**
	 *
	 * @param expectGeoJson if the input is expected to be GeoJson
	 * @param featureType the feature type to use for all features or
	 *            <code>null</code>
	 * @param namespaces the namespace manager
	 */
	public JsonToInstance(boolean expectGeoJson, TypeDefinition featureType, SimpleLog log,
			NamespaceManager namespaces) {
		super();
		this.expectGeoJson = expectGeoJson;
		this.namespaces = namespaces;
		this.featureType = featureType;
		this.log = log;

		this.builder = new JsonInstanceBuilder(log, namespaces);

		if (expectGeoJson) {
			// XXX should GeoJson namespace be the namespace w/o prefix?
//      namespaces.setPrefix(NAMESPACE_GEOJSON, "");
		}
	}

	/**
	 * @return namespace
	 */
	public NamespaceManager getNamespaces() {
		return namespaces;
	}

	/**
	 * Initialize the parser to point at the right position to start reading
	 * instances.
	 * 
	 * @param parser the JSON parser
	 */
	public void init(JsonParser parser) throws JsonParseException, IOException {
		// proceed to first object

		JsonToken start = parser.nextToken();
		switch (start) {
		case START_ARRAY:
			// collection is an array, nothing to do
			break;
		case START_OBJECT:
			// expecting feature collection
			// -> move to "features" array
			proceedToField("features", parser);
			if (parser.getCurrentToken() != JsonToken.FIELD_NAME) {
				throw new IllegalStateException(
						"Did not find field \"features\" in FeatureCollection");
			}

			// proceed to array start
			if (parser.nextToken() != JsonToken.START_ARRAY) {
				throw new IllegalStateException("\"features\" expected to be an array");
			}
			break;
		default:
			throw new IllegalStateException("Unexpected start token " + start);
		}

		parser.nextToken(); // move to value start
	}

	private void proceedToField(String fieldName, JsonParser parser)
			throws JsonParseException, IOException {
		while (parser.nextToken() != null) {
			JsonToken current = parser.getCurrentToken();
			if (current == JsonToken.FIELD_NAME && fieldName.equals(parser.getCurrentName())) {
				// found field name
				return;
			}

			else if (current == JsonToken.START_ARRAY || current == JsonToken.START_OBJECT) {
				// skip child arrays and objects
				parser.skipChildren();
			}
		}
	}

	/**
	 * Read a single instance from the given parser. It is expected that the
	 * current position of the parser is at the start of an object.
	 * 
	 * @param parser the JSON parser
	 * @return the parsed instance
	 * @throws IOException if parsing the JSON fails
	 */
	public Instance readInstance(JsonParser parser) throws IOException {
		JsonToken current = parser.getCurrentToken();

		if (current != JsonToken.START_OBJECT) {
			throw new IllegalStateException(
					"Read instance: Expected object start but found " + current);
		}

		/*
		 * Note: It would be nice to use the streaming API to process the
		 * complete JSON, but in that case we would rely for some things on
		 * information being provided in a certain order, which we can't rely
		 * on.
		 * 
		 * Example cases where order does matter:
		 * 
		 * - detecting if GeoJson is used
		 * 
		 * - determining a schema type automatically (based on @type field or
		 * event structure) [not done yet]
		 */
		Map<String, JsonNode> fields = readFieldsAsTree(parser);

		boolean isGeoJson = false;

		// for GeoJson expect type = Feature
		JsonNode typeNode = fields.get("type");
		JsonNode geomNode = fields.get("geometry");
		JsonNode propNode = fields.get("properties");
		boolean hasFt = typeNode != null && "Feature".equals(typeNode.getTextValue());
		boolean hasGeometry = geomNode != null && geomNode.isObject();
		boolean hasProperties = propNode != null && propNode.isObject();

		if (expectGeoJson) {
			// if we expect GeoJson, one of the conditions is enough
			isGeoJson = hasFt || hasGeometry || hasProperties;
		}
		else {
			// if we do not expect it there should be at least the type and one
			// of the other fields defined
			isGeoJson = hasFt && (hasGeometry || hasProperties);
		}

		// move on to next token (beginning of next instance)
		parser.nextToken();

		// determine schema type

		/*
		 * Currently only configuration of a fixed type that should be assumed
		 * for all features is supported. This could be extended later with some
		 * form of type detection (e.g. using the information in @type in case
		 * the data was written with InstanceToJson)
		 */
		TypeDefinition type = featureType;

		// create instance

		if (isGeoJson) {
			// build from GeoJson feature structure
			ObjectNode geom = (geomNode != null && geomNode.isObject()) ? (ObjectNode) geomNode
					: null;
			ObjectNode props = (propNode != null && propNode.isObject()) ? (ObjectNode) propNode
					: null;
			Map<String, JsonNode> properties = new HashMap<>();
			if (props != null) {
				Iterator<Entry<String, JsonNode>> it = props.getFields();
				while (it.hasNext()) {
					Entry<String, JsonNode> entry = it.next();
					properties.put(entry.getKey(), entry.getValue());
				}
			}

			return builder.buildInstance(type, geom, properties);
		}
		else {
			// generic JSON instance
			return builder.buildInstance(type, null, fields);
		}
	}

	/**
	 * Read the fields of the current object as Json nodes.
	 * 
	 * @param parser the JSON parser
	 * @return the map of field names and and values as nodes
	 * @throws IOException if parsing the JSON fails
	 */
	private Map<String, JsonNode> readFieldsAsTree(JsonParser parser) throws IOException {
		Map<String, JsonNode> fields = new HashMap<>();
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			if (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
				parser.nextToken();
			}

			fields.put(parser.getCurrentName(), parser.readValueAsTree());

			// FIXME where is the token after reading as tree?!
		}
		return fields;
	}

	/**
	 * Skip a field value in the JSON parser.
	 * 
	 * @param parser the JSON parser
	 * @throws IOException if parsing the JSON fails
	 */
	public void skipValue(JsonParser parser) throws IOException {
		JsonToken current = parser.getCurrentToken();

		if (current == JsonToken.FIELD_NAME) {
			// skip field name
			current = parser.nextToken();
		}

		if (current == JsonToken.START_ARRAY || current == JsonToken.START_OBJECT) {
			// skip child arrays and objects
			parser.skipChildren();
			// skip end token
			parser.nextToken();
		}
		else {
			// skip value or end token
			parser.nextToken();
		}
	}

}
