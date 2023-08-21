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

import java.util.function.Consumer

import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.Test
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.ObjectMapper

import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
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

	/**
	 * Test reading a GeoJson object with a point geometry.
	 */
	@Test
	void testReadPointGeoJson() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				geom(GeometryProperty)
			}
		}

		def json = '''{
  "geometry": {
    "type": "Point",
    "coordinates": [30, 10]
  }
}'''

		def instance = readInstance(json, type, null, true)

		def value = instance.p.geom.value()
		assertThat(value)
				.isNotNull()
				.isInstanceOf(GeometryProperty)
				.satisfies({ GeometryProperty p ->
					// CRS is present
					assertThat(p.CRSDefinition)
							.isNotNull()

					// geometry type
					assertThat(p.geometry)
							.isNotNull()
							.isInstanceOf(Point)

					// coordinates
					assertThat(p.geometry.coordinates)
							.containsExactly(new Coordinate(30, 10))
				} as Consumer)
	}

	/**
	 * Test reading a Json object with a point geometry property.
	 */
	@Test
	void testReadPoint() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				geom(GeometryProperty)
			}
		}

		def json = '''{
  "geom": {
    "type": "Point",
    "coordinates": [30, 10]
  }
}'''

		def instance = readInstance(json, type, null, true)

		def value = instance.p.geom.value()
		assertThat(value)
				.isNotNull()
				.isInstanceOf(GeometryProperty)
				.satisfies({ GeometryProperty p ->
					// CRS is present
					assertThat(p.CRSDefinition)
							.isNotNull()

					// geometry type
					assertThat(p.geometry)
							.isNotNull()
							.isInstanceOf(Point)

					// coordinates
					assertThat(p.geometry.coordinates)
							.containsExactly(new Coordinate(30, 10))
				} as Consumer)
	}

	/**
	 * Test reading a Json object with a line string geometry property.
	 */
	@Test
	void testReadLineString() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				geom(GeometryProperty)
			}
		}

		def json = '''{
  "geom": {
    "type": "LineString",
    "coordinates": [
      [30, 10], [10, 30], [40, 40]
    ]
  }
}'''

		def instance = readInstance(json, type, null, true)

		def value = instance.p.geom.value()
		assertThat(value)
				.isNotNull()
				.isInstanceOf(GeometryProperty)
				.satisfies({ GeometryProperty p ->
					// CRS is present
					assertThat(p.CRSDefinition)
							.isNotNull()

					// geometry type
					assertThat(p.geometry)
							.isNotNull()
							.isInstanceOf(LineString)

					// coordinates
					assertThat(p.geometry.coordinates)
							.containsExactly(new Coordinate(30, 10), new Coordinate(10, 30), new Coordinate(40, 40))
				} as Consumer)
	}

	/**
	 * Test reading a Json object with a simple polygon geometry property (no holes).
	 */
	@Test
	void testReadSimplePolygon() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				geom(GeometryProperty)
			}
		}

		def json = '''{
  "geom": {
    "type": "Polygon",
    "coordinates": [
      [[30, 10], [40, 40], [20, 40], [10, 20], [30, 10]]
    ]
  }
}'''

		def instance = readInstance(json, type, null, true)

		def value = instance.p.geom.value()
		assertThat(value)
				.isNotNull()
				.isInstanceOf(GeometryProperty)
				.satisfies({ GeometryProperty p ->
					// CRS is present
					assertThat(p.CRSDefinition)
							.isNotNull()

					// geometry type
					assertThat(p.geometry)
							.isNotNull()
							.isInstanceOf(Polygon)

					// shell
					assertThat((p.geometry as Polygon).shell.coordinates)
							.containsExactly(new Coordinate(30, 10), new Coordinate(40, 40), new Coordinate(20, 40), new Coordinate(10, 20), new Coordinate(30, 10))
					// holes
					assertThat((p.geometry as Polygon).holes).isNullOrEmpty()
				} as Consumer)
	}

	/**
	 * Test reading a Json object with a polygon geometry property where the polygon has a hole.
	 */
	@Test
	void testReadPolygonWithHole() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				geom(GeometryProperty)
			}
		}

		def json = '''{
  "geom": {
    "type": "Polygon",
    "coordinates": [
      [[35, 10], [45, 45], [15, 40], [10, 20], [35, 10]],
      [[20, 30], [35, 35], [30, 20], [20, 30]]
    ]
  }
}'''

		def instance = readInstance(json, type, null, true)

		def value = instance.p.geom.value()
		assertThat(value)
				.isNotNull()
				.isInstanceOf(GeometryProperty)
				.satisfies({ GeometryProperty p ->
					// CRS is present
					assertThat(p.CRSDefinition)
							.isNotNull()

					// geometry type
					assertThat(p.geometry)
							.isNotNull()
							.isInstanceOf(Polygon)

					// shell
					assertThat((p.geometry as Polygon).shell.coordinates)
							.containsExactly(new Coordinate(35, 10), new Coordinate(45, 45), new Coordinate(15, 40), new Coordinate(10, 20), new Coordinate(35, 10))
					// hole
					assertThat((p.geometry as Polygon).holes).hasSize(1)
					assertThat((p.geometry as Polygon).holes[0].coordinates)
							.containsExactly(new Coordinate(20, 30), new Coordinate(35, 35), new Coordinate(30, 20), new Coordinate(20, 30))
				} as Consumer)
	}

	/**
	 * Test reading a Json object with a point geometry property.
	 */
	@Test
	void testReadMultiPoint() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				geom(GeometryProperty)
			}
		}

		def json = '''{
  "geom": {
    "type": "MultiPoint",
    "coordinates": [
      [10, 40], [40, 30], [20, 20], [30, 10]
    ]
  }
}'''

		def instance = readInstance(json, type, null, true)

		def value = instance.p.geom.value()
		assertThat(value)
				.isNotNull()
				.isInstanceOf(GeometryProperty)
				.satisfies({ GeometryProperty p ->
					// CRS is present
					assertThat(p.CRSDefinition)
							.isNotNull()

					// geometry type
					assertThat(p.geometry)
							.isNotNull()
							.isInstanceOf(MultiPoint)

					// coordinates
					assertThat(p.geometry.coordinates)
							.containsExactly(new Coordinate(10, 40), new Coordinate(40, 30), new Coordinate(20, 20), new Coordinate(30, 10))
				} as Consumer)
	}

	/**
	 * Test reading a Json object with nested properties.
	 */
	@Test
	void testReadComplex() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			def nameType = NameType {
				en(String)
				de(String)
			}

			type = TestType {
				id(String)
				name(nameType)
			}
		}

		def json = '''{
  "id": "test",
  "name": {
    "en": "corn",
    "de": "Mais"
  }
}'''

		def instance = readInstance(json, type, null, true)

		def id = instance.p.id.value()
		assertThat(id).isEqualTo('test')

		def name = instance.p.name.first()
		assertThat(name).isInstanceOf(Instance)

		assertThat(name.p.en.value()).isEqualTo('corn')
		assertThat(name.p.de.value()).isEqualTo('Mais')
	}

	/**
	 * Test reading a Json object with multiple layers of nested properties.
	 */
	@Test
	void testReadComplexNested() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				product {
					name {
						en(String)
						de(String)
					}
				}
			}
		}

		def json = '''{
  "product": {
    "name": {
      "en": "corn",
      "de": "Mais"
    }
  }
}'''

		def instance = readInstance(json, type, null, true)

		def product = instance.p.product.first()
		assertThat(product).isInstanceOf(Instance)

		def name = product.p.name.first()
		assertThat(name).isInstanceOf(Instance)

		assertThat(name.p.en.value()).isEqualTo('corn')
		assertThat(name.p.de.value()).isEqualTo('Mais')
	}

	/**
	 * Test reading a Json object with a simple array property.
	 */
	@Test
	void testReadSimpleArray() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				item(cardinality: '1..n', String)
			}
		}

		def json = '''{
  "item": [
    "foo",
    "bar"
  ]
}'''

		def instance = readInstance(json, type, null, true)

		List values = instance.p.item.values()
		assertThat(values).containsExactly('foo', 'bar')
	}

	/**
	 * Test reading a Json object with multiple layers of nested properties.
	 */
	@Test
	void testReadArrayNested() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				product(cardinality: '0..n') {
					name {
						en(String)
						de(String)
					}
				}
			}
		}

		def json = '''{
  "product": [
    {
      "name": {
        "en": "corn",
        "de": "Mais"
      }
    },
    {
      "name": {
        "en": "cucumber",
        "de": "Grüne Banane"
      }
    }
  ]
}'''

		def instance = readInstance(json, type, null, true)

		List products = instance.p.product.list()
		assertThat(products).hasSize(2)

		assertThat(products[0].p.name.en.value()).isEqualTo('corn')
		assertThat(products[0].p.name.de.value()).isEqualTo('Mais')

		assertThat(products[1].p.name.en.value()).isEqualTo('cucumber')
		assertThat(products[1].p.name.de.value()).isEqualTo('Grüne Banane')
	}

	/**
	 * Test reading a Json object with multiple layers of nested properties.
	 */
	@Test
	void testReadSimpleChoice() {
		TypeDefinition type
		Schema schema = new SchemaBuilder().schema {
			type = TestType {
				product(cardinality: '0..n') {
					_(choice: true) {
						name_en(String)
						name_de(String)
					}
				}
			}
		}

		def json = '''{
  "product": [
    {
      "name_de": "Mais"
    },
    {
      "name_en": "cucumber"
    }
  ]
}'''

		def instance = readInstance(json, type, null, true)

		List products = instance.p.product.list()
		assertThat(products).hasSize(2)

		assertThat(products[0].p.name_de.value()).isEqualTo('Mais')
		assertThat(products[0].p.name_en.value()).isNull()

		assertThat(products[1].p.name_de.value()).isNull()
		assertThat(products[1].p.name_en.value()).isEqualTo('cucumber')
	}

	/*
	 * TODO add additional tests for specific cases:
	 * - any specific bindings that should be supported?
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
		def translate = new JsonToInstance(expectGeoJson, type, false, schema, SimpleLog.CONSOLE_LOG)
		JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json)
		assertThat(parser.nextToken()).isEqualTo(JsonToken.START_OBJECT)
		translate.readInstance(parser)
	}
}
