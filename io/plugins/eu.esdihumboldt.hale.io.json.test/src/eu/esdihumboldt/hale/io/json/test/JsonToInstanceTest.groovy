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

import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.Test

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.ObjectMapper

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

		def instance = readInstance(json, type, null)

		assertThat(instance.p.text.value()).isEqualTo('This is a test text')
	}

	/**
	 * Test reading a boolean property.
	 */
	@Test
	void testReadBoolean() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				value(Boolean)
			}
		}

		def json = '''{
  "value": true
}'''

		def instance = readInstance(json, type, null)

		assertThat(instance.p.value.value()).isEqualTo(true)
	}

	/**
	 * Test reading a numeric integer property.
	 */
	@Test
	void testReadInteger() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				number(Integer)
			}
		}

		def json = '''{
  "number": 12
}'''

		def instance = readInstance(json, type, null)

		assertThat(instance.p.number.value()).isEqualTo(12)
	}

	/**
	 * Test reading a numeric floating point property.
	 */
	@Test
	void testReadDouble() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				number(Double)
			}
		}

		def json = '''{
  "number": 12.34
}'''

		def instance = readInstance(json, type, null)

		def value = instance.p.number.value()
		double expected = 12.34
		assertThat(value)
				.asInstanceOf(InstanceOfAssertFactories.DOUBLE)
				.isEqualTo(expected, within(0.001))
	}

	/**
	 * Test reading an instance and specifying the type via the <code>@type</code> field.
	 */
	@Test
	void testSpecifyType() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				text(String)
			}

			OtherType {
				text(String)
			}
		}

		def json = '''{
  "@type": "OtherType",
  "text": "This is a test text"
}'''

		def instance = readInstance(json, type, schema)

		assertThat(instance.p.text.value()).isEqualTo('This is a test text')
		assertThat(instance.getDefinition().name.localPart).isEqualTo('OtherType')
	}

	/**
	 * Test reading an instance and specifying the type via the <code>@type</code> field.
	 */
	@Test
	void testSpecifyTypeGeojson() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				text(String)
			}

			OtherType {
				text(String)
			}
		}

		def json = '''{
  "@type": "OtherType",
  "properties": {
    "text": "This is a test text"
  }
}'''

		def instance = readInstance(json, type, schema, true)

		assertThat(instance.p.text.value()).isEqualTo('This is a test text')
		assertThat(instance.getDefinition().name.localPart).isEqualTo('OtherType')
	}

	/*
	 * FIXME add additional tests for specific cases:
	 * - complex properties / arrays
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
	Instance readInstance(String json, TypeDefinition type, Schema schema, boolean expectGeoJson = false) {
		def translate = new JsonToInstance(expectGeoJson, type, schema, SimpleLog.CONSOLE_LOG)
		JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json)
		assertThat(parser.nextToken()).isEqualTo(JsonToken.START_OBJECT)
		translate.readInstance(parser)
	}
}
