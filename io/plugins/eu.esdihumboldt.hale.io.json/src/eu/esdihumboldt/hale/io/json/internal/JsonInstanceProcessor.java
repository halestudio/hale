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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

/**
 * Interface for classes processing individual instances coming from Json.
 * 
 * @author Simon Templer
 * @param <T> the result type per instance
 */
public interface JsonInstanceProcessor<T> {

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