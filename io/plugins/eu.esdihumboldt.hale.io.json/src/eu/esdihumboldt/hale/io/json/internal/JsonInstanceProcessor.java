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
import java.io.Reader;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interface for classes processing individual instances coming from Json.
 * 
 * @author Simon Templer
 * @param <T> the result type per instance
 */
public interface JsonInstanceProcessor<T> {

	/**
	 * Process Json instances.
	 * 
	 * @param <T> the processing result type per instance
	 * @param json the Json reader
	 * @param processor the instance processor
	 * @param handler a handler for individual instance processing results, may
	 *            be <code>null</code>
	 * @param limit the maximum limit of instances to process, negative for no
	 *            limit
	 * 
	 * @throws JsonParseException if the Json cannot be parsed
	 * @throws IOException if an error occurs processing the Json
	 */
	static <T> void process(final Reader json, final JsonInstanceProcessor<T> processor,
			final Consumer<T> handler, final int limit) throws JsonParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory jsonFactory = mapper.getFactory();
		try (JsonParser parser = jsonFactory.createParser(json)) {
			processor.init(parser);

			int num = 0;

			while (parser.getCurrentToken() == JsonToken.START_OBJECT) {
				num++;
				if (limit < 0 || limit <= num) {
					T instance = processor.readInstance(parser);
					if (handler != null) {
						handler.accept(instance);
					}
				}
			}
		}

	}

	/**
	 * Initialize the parser to point at the right position to start reading
	 * instances.
	 * 
	 * @param parser the JSON parser
	 */
	void init(JsonParser parser) throws JsonParseException, IOException;

	/**
	 * Read a single instance from the given parser. It is expected that the
	 * current position of the parser is at the start of an object.
	 * 
	 * @param parser the JSON parser
	 * @return the parsed instance
	 * @throws IOException if parsing the JSON fails
	 */
	T readInstance(JsonParser parser) throws IOException;

	/**
	 * Skip a field value in the JSON parser.
	 * 
	 * @param parser the JSON parser
	 * @throws IOException if parsing the JSON fails
	 */
	void skipValue(JsonParser parser) throws IOException;

}