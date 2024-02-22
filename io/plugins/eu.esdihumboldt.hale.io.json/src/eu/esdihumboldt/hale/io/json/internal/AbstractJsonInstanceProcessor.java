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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Abstract class for processing instances from JSON.
 * 
 * @author Simon Templer
 */
public abstract class AbstractJsonInstanceProcessor<T>
		implements InstanceJsonConstants, JsonInstanceProcessor<T> {

	private final boolean expectGeoJson;

	private final JsonReadMode mode;

	/**
	 * 
	 * @param expectGeoJson if the input is expected to be GeoJson
	 */
	public AbstractJsonInstanceProcessor(boolean expectGeoJson) {
		this(JsonReadMode.auto, expectGeoJson);
	}

	/**
	 * 
	 * @param mode the mode for reading the Json, supporting different kinds of
	 *            document structures
	 * @param expectGeoJson if the input is expected to be GeoJson
	 */
	public AbstractJsonInstanceProcessor(JsonReadMode mode, boolean expectGeoJson) {
		super();

		this.expectGeoJson = expectGeoJson;
		this.mode = mode;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.json.internal.JsonInstanceProcessor#init(com.fasterxml.jackson.core.JsonParser)
	 */
	@Override
	public void init(JsonParser parser) throws JsonParseException, IOException {
		JsonToken start = parser.nextToken();

		// proceed to first object
		switch (mode) {
		case auto:
			// auto-detect if array or FeatureCollection

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
			break;
		case singleObject:
			// read single object
			if (start != JsonToken.START_OBJECT) {
				throw new IllegalStateException("Json does not start with object");
			}
			break;
		case firstArray:
			// use first array encountered
			proceedToArray(parser);
			if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
				// no array found
				throw new IllegalStateException("No Json array found");
			}
			parser.nextToken(); // move to value start
			break;
		default:
			throw new IllegalStateException("Unrecognized read mode " + mode);
		}
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
	 * Proceed to the first array found.
	 * 
	 * @param parser the Json parser
	 */
	private void proceedToArray(JsonParser parser) throws JsonParseException, IOException {
		while (parser.nextToken() != null) {
			JsonToken current = parser.getCurrentToken();
			if (current == JsonToken.START_ARRAY) {
				// found array
				return;
			}
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.io.json.internal.JsonInstanceProcessor#readInstance(com.fasterxml.jackson.core.JsonParser)
	 */
	@Override
	public T readInstance(JsonParser parser) throws IOException {
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

		// move on to next token (beginning of next instance)
		parser.nextToken();

		return processInstanceFields(fields);
	}

	/**
	 * Process an instance given its root object fields.
	 * 
	 * The default implementation determines whether the fields represent a
	 * GeoJson object and continue processing based on that.
	 * 
	 * @param fields the object's root fields
	 * @return the processing result
	 */
	protected T processInstanceFields(Map<String, JsonNode> fields) {
		boolean isGeoJson = false;

		// for GeoJson expect type = Feature
		JsonNode typeNode = fields.get("type");
		JsonNode geomNode = fields.get("geometry");
		JsonNode propNode = fields.get("properties");
		boolean hasFt = typeNode != null && "Feature".equals(typeNode.textValue());
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

		// process

		if (isGeoJson) {
			// build from GeoJson feature structure
			ObjectNode geom = (geomNode != null && geomNode.isObject()) ? (ObjectNode) geomNode
					: null;
			ObjectNode props = (propNode != null && propNode.isObject()) ? (ObjectNode) propNode
					: null;
			Map<String, JsonNode> properties = new HashMap<>();
			if (props != null) {
				Iterator<Entry<String, JsonNode>> it = props.fields();
				while (it.hasNext()) {
					Entry<String, JsonNode> entry = it.next();
					properties.put(entry.getKey(), entry.getValue());
				}
			}

			return processInstance(fields, geom, properties);
		}
		else {
			// generic JSON instance
			return processInstance(fields, null, fields);
		}
	}

	/**
	 * Process an instance after determining if it is a GeoJson object
	 * 
	 * @param fields the object's root fields
	 * @param geom the GeoJson geometry node or null
	 * @param properties the instance properties
	 * @return the processing result
	 */
	protected abstract T processInstance(Map<String, JsonNode> fields, ObjectNode geom,
			Map<String, JsonNode> properties);

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
	 * @see eu.esdihumboldt.hale.io.json.internal.JsonInstanceProcessor#skipValue(com.fasterxml.jackson.core.JsonParser)
	 */
	@Override
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
