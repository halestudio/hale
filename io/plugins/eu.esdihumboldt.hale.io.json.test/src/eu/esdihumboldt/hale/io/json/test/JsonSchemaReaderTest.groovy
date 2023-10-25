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
import java.util.function.Consumer

import javax.xml.namespace.QName

import org.junit.Test
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point

import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType
import eu.esdihumboldt.hale.io.json.JsonSchemaReader
import eu.esdihumboldt.hale.io.json.internal.schema.JsonType
import eu.esdihumboldt.util.io.StringInputSupplier

/**
 * Tests for reading a schema from Json data.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
//@CompileStatic - disabled because it causes problems in Maven Tycho build
class JsonSchemaReaderTest {

	// default charset used in tests
	Charset charset = StandardCharsets.UTF_8

	@Test
	void testStringProperty() {
		def json = '''
[
  {
    "name": "Henry"
  }
]
'''

		def reader = jsonStringReader(json)
		def schema = execute(reader)

		assertThat(schema.getMappingRelevantTypes())
				.hasSize(1)

		def type = firstType(schema)

		def properties = DefinitionUtil.getAllProperties(type)
		assertThat(properties).hasSize(1)

		def property = type.getChild(new QName('name')).asProperty()
		assertThat(property)
				.isNotNull()
		assertThat(property.getConstraint(Cardinality.class))
				.satisfies({
					assertThat(it.minOccurs).isEqualTo(0)
					assertThat(it.maxOccurs).isEqualTo(1)
				} as Consumer<Cardinality>)
		assertThat(property.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(String.class)
	}

	@Test
	void testNumberProperty() {
		def json = '''
[
  {
    "age": 12
  },
  {
    "age": 1.2
  }
]
'''

		def reader = jsonStringReader(json)
		def schema = execute(reader)

		assertThat(schema.getMappingRelevantTypes())
				.hasSize(1)

		def type = firstType(schema)

		def properties = DefinitionUtil.getAllProperties(type)
		assertThat(properties).hasSize(1)

		def property = type.getChild(new QName('age')).asProperty()
		assertThat(property)
				.isNotNull()
		assertThat(property.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(BigDecimal.class)
	}

	@Test
	void testBooleanProperty() {
		def json = '''
[
  {
    "yes": true
  },
  {
    "yes": false
  }
]
'''

		def reader = jsonStringReader(json)
		def schema = execute(reader)

		assertThat(schema.getMappingRelevantTypes())
				.hasSize(1)

		def type = firstType(schema)

		def properties = DefinitionUtil.getAllProperties(type)
		assertThat(properties).hasSize(1)

		def property = type.getChild(new QName('yes')).asProperty()
		assertThat(property)
				.isNotNull()
		assertThat(property.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(Boolean.class)
	}

	@Test
	void testMixedProperty() {
		def json = '''
[
  {
    "age": 12
  },
  {
    "age": "12"
  }
]
'''

		def reader = jsonStringReader(json)
		def schema = execute(reader)

		assertThat(schema.getMappingRelevantTypes())
				.hasSize(1)

		def type = firstType(schema)

		def properties = DefinitionUtil.getAllProperties(type)
		assertThat(properties).hasSize(1)

		def property = type.getChild(new QName('age')).asProperty()
		assertThat(property)
				.isNotNull()
		assertThat(property.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(String.class)
	}

	@Test
	void testMultipleProperties() {
		def json = '''
[
  {
    "age": 12
  },
  {
    "name": "Henry"
  },
  {
    "name": "Peter",
    "address": "Peter's home"
  }
]
'''

		def reader = jsonStringReader(json)
		def schema = execute(reader)

		assertThat(schema.getMappingRelevantTypes())
				.hasSize(1)

		def type = firstType(schema)

		def properties = DefinitionUtil.getAllProperties(type)
		assertThat(properties).hasSize(3)

		def ageProperty = type.getChild(new QName('age')).asProperty()
		assertThat(ageProperty)
				.isNotNull()
		assertThat(ageProperty.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(BigDecimal.class)

		def nameProperty = type.getChild(new QName('name')).asProperty()
		assertThat(nameProperty)
				.isNotNull()
		assertThat(nameProperty.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(String.class)

		def addressProperty = type.getChild(new QName('address')).asProperty()
		assertThat(addressProperty)
				.isNotNull()
	}

	@Test
	void testNested() {
		def json = '''
[
  {
    "person": {
      "name": "Henry"
    }
  },
  {
    "person": {
      "age": 12
    }
  }
]
'''

		def reader = jsonStringReader(json)
		def schema = execute(reader)

		assertThat(schema.getMappingRelevantTypes())
				.hasSize(1)

		def type = firstType(schema)

		def properties = DefinitionUtil.getAllProperties(type)
		assertThat(properties).hasSize(1)

		def personProperty = type.getChild(new QName('person')).asProperty()
		assertThat(personProperty)
				.isNotNull()

		def children = DefinitionUtil.getAllProperties(personProperty.propertyType)
		assertThat(children).hasSize(2)

		def nameProperty = personProperty.propertyType.getChild(new QName('name')).asProperty()
		assertThat(nameProperty)
				.isNotNull()
		assertThat(nameProperty.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(String.class)

		def ageProperty = personProperty.propertyType.getChild(new QName('age')).asProperty()
		assertThat(ageProperty)
				.isNotNull()
		assertThat(ageProperty.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(BigDecimal.class)
	}

	@Test
	void testArray() {
		def json = '''
[
  {
    "people": [
      {
        "name": "Henry"
      },
      {
        "name": "Peter"
      }
    ]
  }
]
'''

		def reader = jsonStringReader(json)
		def schema = execute(reader)

		assertThat(schema.getMappingRelevantTypes())
				.hasSize(1)

		def type = firstType(schema)

		def properties = DefinitionUtil.getAllProperties(type)
		assertThat(properties).hasSize(1)

		def peopleProperty = type.getChild(new QName('people')).asProperty()
		assertThat(peopleProperty)
				.isNotNull()
		assertThat(peopleProperty.getConstraint(Cardinality.class))
				.satisfies({
					assertThat(it.minOccurs).isEqualTo(0)
					assertThat(it.maxOccurs).isEqualTo(Cardinality.UNBOUNDED)
				} as Consumer<Cardinality>)

		def children = DefinitionUtil.getAllProperties(peopleProperty.propertyType)
		assertThat(children).hasSize(1)

		def nameProperty = peopleProperty.propertyType.getChild(new QName('name')).asProperty()
		assertThat(nameProperty)
				.isNotNull()
		assertThat(nameProperty.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(String.class)
	}

	@Test
	void testPointGeometry() {
		def json = '''
[
  {
    "geom": {
      "type": "Point",
      "coordinates": [30, 10]
    } 
  }
]
'''

		def reader = jsonStringReader(json)
		def schema = execute(reader)

		assertThat(schema.getMappingRelevantTypes())
				.hasSize(1)

		def type = firstType(schema)

		def properties = DefinitionUtil.getAllProperties(type)
		assertThat(properties).hasSize(1)

		def property = type.getChild(new QName('geom')).asProperty()
		assertThat(property)
				.isNotNull()
		assertThat(property.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(GeometryProperty.class)
		assertThat(property.propertyType.getConstraint(GeometryType.class))
				.satisfies({
					assertThat(it.geometry).isTrue()
					assertThat(it.binding).isEqualTo(Point.class)
				} as Consumer<GeometryType>)
	}

	@Test
	void testGeoJson() {
		def json = '''
{
  "type": "FeatureCollection",
  "features": [
    {
      "geometry": {
        "type": "LineString",
        "coordinates": [
          [102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]
        ]
      },
      "properties": {
        "name": "Somewhere"
      }
    }
  ]
}
'''

		def reader = jsonStringReader(json)
		def schema = execute(reader)

		assertThat(schema.getMappingRelevantTypes())
				.hasSize(1)

		def type = firstType(schema)

		def properties = DefinitionUtil.getAllProperties(type)
		assertThat(properties).hasSize(2)

		def geomProperty = type.getChild(JsonType.GEOJSON_GEOMETRY_NAME).asProperty()
		assertThat(geomProperty)
				.isNotNull()
		assertThat(geomProperty.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(GeometryProperty.class)
		assertThat(geomProperty.propertyType.getConstraint(GeometryType.class))
				.satisfies({
					assertThat(it.geometry).isTrue()
					assertThat(it.binding).isEqualTo(LineString.class)
				} as Consumer<GeometryType>)

		def nameProperty = type.getChild(new QName('name')).asProperty()
		assertThat(nameProperty)
				.isNotNull()
		assertThat(nameProperty.propertyType.getConstraint(Binding.class).binding)
				.isEqualTo(String.class)
	}

	@Test
	void testMultiType() {
		def json = '''
{
  "type": "FeatureCollection",
  "features": [
    {
      "@type": "Place",
      "properties": {
        "name": "Somewhere"
      }
    },
    {
      "@type": "Person",
      "properties": {
        "name": "Henry"
      }
    }
  ]
}
'''

		def reader = jsonStringReader(json)
		def schema = execute(reader)

		assertThat(schema.getMappingRelevantTypes())
				.hasSize(2)

		assertThat(schema.getMappingRelevantTypes()
				.collect { TypeDefinition t -> t.name.localPart })
				.containsExactlyInAnyOrder("Person", "Place")

		schema.getMappingRelevantTypes().each { TypeDefinition type ->
			def properties = DefinitionUtil.getAllProperties(type)
			assertThat(properties).hasSize(1)

			def nameProperty = type.getChild(new QName('name')).asProperty()
			assertThat(nameProperty)
					.isNotNull()
			assertThat(nameProperty.propertyType.getConstraint(Binding.class).binding)
					.isEqualTo(String.class)
		}
	}

	// helpers

	TypeDefinition firstType(Schema schema) {
		assertThat(schema.getMappingRelevantTypes())
				.isNotEmpty()

		schema.mappingRelevantTypes.iterator().next()
	}

	Schema execute(JsonSchemaReader reader) {
		def report = reader.execute(new LogProgressIndicator())
		assertThat(report.isSuccess()).isTrue()
		reader.getSchema()
	}

	JsonSchemaReader jsonStringReader(String testData) {
		JsonSchemaReader reader = new JsonSchemaReader()

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
		reader
	}
}
