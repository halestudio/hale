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

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

import org.junit.Test

import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.io.json.JsonInstanceReader
import eu.esdihumboldt.hale.io.json.internal.JsonReadMode
import eu.esdihumboldt.util.io.StringInputSupplier

/**
 * Tests for Json instance reader.
 * 
 * @author Simon Templer
 */
class JsonInstanceReaderTest {

	// simple type and schema used in some tests
	TypeDefinition simpleType

	Schema simpleSchema = new SchemaBuilder().schema {
		simpleType = SimpleType {
			text(String)
		}
	}

	// default charset used in tests
	Charset charset = StandardCharsets.UTF_8

	// default test data for simple data
	def testDataSimple = '''
[
  {
    "text": "foo"
  },
  {
    "text": "bar"
  }
]
'''

	@Test
	void testReadModeDefault() {
		def reader = simpleJsonReader(testDataSimple)

		def collection = execute(reader)
		collection.iterator().withCloseable {
			def count = 0
			while (it.hasNext()) {
				def instance = it.next()

				checkSimpleInstance(instance, count)

				count++
			}

			// test expected feature count
			assertThat(count).isEqualTo(2)
		}
	}

	@Test
	void testReadModeSingleObject() {
		def testData = '''
{
  "text": "single"
}
'''
		def reader = simpleJsonReader(testData)
		reader.setReadMode(JsonReadMode.singleObject)

		def collection = execute(reader)
		collection.iterator().withCloseable {
			def count = 0
			while (it.hasNext()) {
				def instance = it.next()

				checkSimpleInstance(instance, 2)

				count++
			}

			// test expected feature count
			assertThat(count).isEqualTo(1)
		}
	}

	// helpers

	InstanceCollection execute(JsonInstanceReader reader) {
		def report = reader.execute(new LogProgressIndicator())
		assertThat(report.isSuccess()).isTrue()
		reader.getInstances()
	}

	JsonInstanceReader simpleJsonReader(String testData = testDataSimple) {
		JsonInstanceReader reader = new JsonInstanceReader()

		reader.setCharset(charset)
		def stringSource =  new StringInputSupplier(testData, charset)
		reader.setSource(new LocatableInputSupplier() {

					@Override
					public Object getInput() throws IOException {
						stringSource.getInput()
					}

					@Override
					public URI getLocation() {
						return null;
					}

					@Override
					public URI getUsedLocation() {
						return null;
					}
				})
		reader.setSourceSchema(simpleSchema)

		reader
	}

	/**
	 * Check an instance from default sample data in simple schema.
	 * @param instance the instance to check
	 * @param index the index of the instance in the data
	 */
	void checkSimpleInstance(Instance instance, int index) {
		if (index < 0 || index > 2) fail("Unexpected instance index " + index)

		def text = instance.p.text.value()
		String expectedText = null
		switch (index) {
			case 0:
				expectedText = 'foo'
				break
			case 1:
				expectedText = 'bar'
				break
			case 2:
				expectedText = 'single'
				break
		}

		assertThat(text).isEqualTo(expectedText)
	}
}
