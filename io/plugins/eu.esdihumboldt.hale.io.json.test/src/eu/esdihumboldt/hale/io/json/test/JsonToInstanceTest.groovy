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

package eu.esdihumboldt.hale.io.json.test

import static org.assertj.core.api.Assertions.*

import org.codehaus.jackson.JsonParser
import org.codehaus.jackson.JsonToken
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test

import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.io.json.internal.JsonToInstance

/**
 * Tests for translating Json/GeoJson to hale instance model.
 * 
 * @author Simon Templer
 */
class JsonToInstanceTest {

	/**
	 * Test reading a string property.
	 */
	@Test
	void testReadString() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				text(String)
			}
		}

		def json = '''{
  "text": "This is a test text"
}'''

		def instance = readInstance(json, type)

		assertThat(instance.p.text.value()).isEqualTo('This is a test text')
	}

	/*
	 * FIXME add additional tests for specific cases:
	 * - different Json value types (boolean, numeric, etc.)
	 * - geometries
	 * - any specific bindings that should be supported?
	 * Later:
	 */

	// helper methods

	/**
	 * Read an instance from a JSON string containing a single object.
	 * 
	 * @param json the JSON string
	 * @param type the type the instance should be loaded with
	 * @param expectGeoJson if the JSON object is expected to be in GeoJSON encoding
	 * @return the parsed instance
	 */
	Instance readInstance(String json, TypeDefinition type, boolean expectGeoJson = false) {
		def translate = new JsonToInstance(expectGeoJson, type, SimpleLog.CONSOLE_LOG)
		JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json)
		assertThat(parser.nextToken()).isEqualTo(JsonToken.START_OBJECT)
		translate.readInstance(parser)
	}
}
