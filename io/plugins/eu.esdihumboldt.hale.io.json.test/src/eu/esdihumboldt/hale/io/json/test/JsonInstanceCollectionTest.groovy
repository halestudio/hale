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

import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.io.json.internal.JsonInstanceCollection
import eu.esdihumboldt.hale.io.json.reader.JsonToInstance
import eu.esdihumboldt.util.io.StringInputSupplier

/**
 * Tests for translating collections of Json/GeoJson objects to hale instance model.
 * 
 * @author Simon Templer
 */
class JsonInstanceCollectionTest {

	// simple type and schema used in some tests
	TypeDefinition simpleType

	Schema simpleSchema = new SchemaBuilder().schema {
		simpleType = SimpleType {
			id(String)
			name(String)
			geometry(GeometryProperty)
		}
	}

	// default charset used in tests
	Charset charset = StandardCharsets.UTF_8

	// default test data for simple data in GeoJson FeatureCollection
	def testDataSimpleFc = '''
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "properties": {
        "id": "line",
        "name": "Line feature",
		"geometry": {
	        "type": "LineString",
	        "coordinates": [
	          [102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]
	        ]
      }
      }
    },
    {
      "type": "Feature",
      "properties": {
        "id": "poly",
        "name": "Polygon feature",
		"geometry": {
	        "type": "Polygon",
	        "coordinates": [
	          [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0],
	            [100.0, 1.0], [100.0, 0.0] ]
	        ]
	      }	
      }
    }
  ]
}
'''

	@Test
	void testReadFeatureCollection() {
		def translate = new JsonToInstance(true, simpleType, SimpleLog.CONSOLE_LOG)
		def collection = new JsonInstanceCollection(translate, new StringInputSupplier(testDataSimpleFc, charset), charset)
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
	void testSkipFeatureCollection() {
		def translate = new JsonToInstance(true, simpleType, SimpleLog.CONSOLE_LOG)
		def collection = new JsonInstanceCollection(translate, new StringInputSupplier(testDataSimpleFc, charset), charset)
		collection.iterator().withCloseable {
			def count = 0
			while (it.hasNext()) {
				it.skip()
				count++
			}

			// test expected feature count
			assertThat(count).isEqualTo(2)
		}
	}

	@Test
	void testReadGeoJsonArray() {
		def translate = new JsonToInstance(true, simpleType, SimpleLog.CONSOLE_LOG)

		def testData = '''
[
  {
    "type": "Feature",
    "geometry": {
      "type": "LineString",
      "coordinates": [
        [102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]
      ]
    },
    "properties": {
      "id": "line",
      "name": "Line feature",
	  "geom" : {"type":"MultiPolygon","coordinates":[[[[8.7959897,49.975944],[8.7960823,49.9759079],[8.7958611,49.9755884],[8.7958741,49.9754369],[8.7960504,49.9754394],[8.7963574,49.9753247],[8.7978263,49.9747766],[8.79809,49.9748239],[8.7989398,49.9744998],[8.7990762,49.974392],[8.7994221,49.9745296],[8.7995683,49.9745878],[8.7997145,49.9746521],[8.7986201,49.9750836],[8.7985447,49.9751133],[8.797687,49.9754516],[8.7964701,49.9758961],[8.796438,49.9759395],[8.796394,49.9759811],[8.7959897,49.975944]]]]}
      
    }
  },
  {
    "type": "Feature",
    "geometry": {
      "type": "Polygon",
      "coordinates": [
        [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0],
          [100.0, 1.0], [100.0, 0.0] ]
      ]
    },
    "properties": {
      "id": "poly",
      "name": "Polygon feature"
    }
  }
]
'''
		def collection = new JsonInstanceCollection(translate, new StringInputSupplier(testData, charset), charset)
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
	void testReadJsonArray() {
		def translate = new JsonToInstance(false, simpleType, SimpleLog.CONSOLE_LOG)

		def testData = '''
[
  {
    "geometry": {
      "type": "LineString",
      "coordinates": [
        [102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]
      ]
    },
    "id": "line",
    "name": "Line feature"
  },
  {
    "geometry": {
      "type": "Polygon",
      "coordinates": [
        [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0],
          [100.0, 1.0], [100.0, 0.0] ]
      ]
    },
    "id": "poly",
    "name": "Polygon feature"
  }
]
'''
		def collection = new JsonInstanceCollection(translate, new StringInputSupplier(testData, charset), charset)
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

	// helper methods

	/**
	 * Check an instance from default sample data in simple schema.
	 * @param instance the instance to check
	 * @param index the index of the instance in the data
	 */
	void checkSimpleInstance(Instance instance, int index) {
		if (index < 0 || index > 1) fail("Unexpected instance index " + index)

		def id = instance.p.id.value()
		def name = instance.p.name.value()

		def expectedId = index == 0 ? "line" : "poly"
		def expectedName = index == 0 ? "Line feature" : "Polygon feature"

		assertThat(id).isEqualTo(expectedId)
		assertThat(name).isEqualTo(expectedName)

		//FIXME also check geometry (also requires extending schema)
	}
}
