/*
 * Copyright (c) 2022 wetransform GmbH
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

import static org.junit.Assert.*

import org.junit.Ignore
import org.junit.Test
import org.locationtech.jts.geom.Geometry

import eu.esdihumboldt.cst.functions.geometric.GeometryHelperFunctions
import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.io.json.internal.InstanceToJson
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class InstanceToJsonTest {

	static GeometryProperty<? extends Geometry> createGeometry(String wkt, Object crs) {
		return GeometryHelperFunctions._with([geometry: wkt, crs: crs])
	}

	// schema with types for testing
	Schema schema = new SchemaBuilder().schema {
		SimpleType {
			id(String)
			name(String)
		}
	}

	// schema with types for testing
	Schema schemaGeometry = new SchemaBuilder().schema {
		SimpleTypeGeometry {
			id(String)
			name(String)
			geometry(Geometry)
		}
	}

	@Test
	void testJsonSimple() {
		def instance = new InstanceBuilder(types: schema).createInstance('SimpleType') {
			id '123'
			name 'Peter Parker'
		}

		def jsonStr = new InstanceToJson(false).toJsonString(instance, SimpleLog.CONSOLE_LOG)
		println jsonStr

		def json = new JsonSlurper().parseText(jsonStr)

		// should (only) contain instance properties
		assertEquals('Peter Parker', json.name)
		assertEquals('123', json.id)
		assertEquals(2, json.size())
		// namespaces are expected to be defined separately in this case
	}

	@Test
	@Ignore('Expecting a GeoJson namespace to be defined w/ empty prefix')
	void testGeoJsonSimple() {
		def instance = new InstanceBuilder(types: schema).createInstance('SimpleType') {
			id '123'
			name 'Peter Parker'
		}

		def converter = new InstanceToJson(true)
		def jsonStr = converter.toJsonString(instance, SimpleLog.CONSOLE_LOG)
		println jsonStr

		def json = new JsonSlurper().parseText(jsonStr)

		// build expected object
		def prefix = converter.namespaces.getPrefix("")
		assertNotNull(prefix)
		prefix = prefix + ':'
		def expected = [
			type: 'Feature',
			'@type': prefix + 'SimpleType',
			geometry: null,
			properties: [
				(prefix + 'name'): 'Peter Parker',
				(prefix + 'id'): '123'
			],
			'@namespaces': converter.namespaces.getNamespaces()
		]

		assertEquals(expected, json)
	}

	@Test
	void testGeoJsonSimple_2() {
		def instance = new InstanceBuilder(types: schema).createInstance('SimpleType') {
			id '123'
			name 'Peter Parker'
		}

		def converter = new InstanceToJson(true)

		def jsonStr = converter.toJsonString(instance, SimpleLog.CONSOLE_LOG)
		println jsonStr

		def json = new JsonSlurper().parseText(jsonStr)

		// build expected object
		def expected = [
			type: 'Feature',
			'@type': 'SimpleType',
			geometry: null,
			properties: [
				('name'): 'Peter Parker',
				('id'): '123'
			],
			'@namespaces': [:]
		]

		assertEquals(expected, json)
	}

	@Test
	void testGeoJsonSimple_7_andLess() {

		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				population(Integer)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				population 158254
				location( createGeometry('POINT(49.8728337 8.651)', 4326) )
			}

			city {
				name 'München'
				population 1471508
				location( createGeometry('POINT(48.13722 11.5755)', 4326) )
			}
		}


		def converter = new InstanceToJson(true, 14)

		def jsonStr = converter.toJsonString(instances, SimpleLog.CONSOLE_LOG)
		println jsonStr

		def json = new JsonSlurper().parseText(jsonStr)

		// build expected object
		def expected = [
			type: "FeatureCollection",
			features: [
				[
					type: "Feature",
					"@type": "city",
					geometry: [
						type: "MultiPoint",
						coordinates: [
							[
								49.8728337,
								8.651
							]
						]
					],
					properties: [
						population: 158254,
						name: "Darmstadt",
						location: [
							type: "Point",
							coordinates: [
								49.8728337,
								8.651
							]
						]
					]
				],
				[
					type: "Feature",
					"@type": "city",
					geometry: [
						type: "MultiPoint",
						coordinates: [
							[
								48.13722,
								11.5755
							]
						]
					],
					properties: [
						population: 1471508,
						name: "München",
						location: [
							type: "Point",
							coordinates: [
								48.13722,
								11.5755
							]
						]
					]
				]
			],
			"@namespaces": [:]
		]

		// Convert the Groovy map to a JSON string
		def jsonString = JsonOutput.toJson(json)


		assertEquals(expected, json)
	}

	@Test
	void testGeoJsonSimple_14() {

		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				population(Integer)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				population 158254
				location( createGeometry('POINT(49.8728337891123 8.65122278912346)', 4326) )
			}

			city {
				name 'München'
				population 1471508
				location( createGeometry('POINT(48.13722289876544 11.57555687654323)', 4326) )
			}
		}


		def converter = new InstanceToJson(true, 14)

		def jsonStr = converter.toJsonString(instances, SimpleLog.CONSOLE_LOG)
		println jsonStr

		def json = new JsonSlurper().parseText(jsonStr)

		// build expected object
		def expected = [
			type: "FeatureCollection",
			features: [
				[
					type: "Feature",
					"@type": "city",
					geometry: [
						type: "MultiPoint",
						coordinates: [
							[
								49.8728337891123,
								8.65122278912346
							]
						]
					],
					properties: [
						population: 158254,
						name: "Darmstadt",
						location: [
							type: "Point",
							coordinates: [
								49.8728337891123,
								8.65122278912346
							]
						]
					]
				],
				[
					type: "Feature",
					"@type": "city",
					geometry: [
						type: "MultiPoint",
						coordinates: [
							[
								48.13722289876544,
								11.57555687654323
							]
						]
					],
					properties: [
						population: 1471508,
						name: "München",
						location: [
							type: "Point",
							coordinates: [
								48.13722289876544,
								11.57555687654323
							]
						]
					]
				]
			],
			"@namespaces": [:]
		]

		// Convert the Groovy map to a JSON string
		def jsonString = JsonOutput.toJson(json)


		assertEquals(expected, json)
	}

	@Test
	void testGeoJsonSimpleMoreThan_14() {

		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				population(Integer)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				population 158254
				location( createGeometry('POINT(49.8728337891123456789 8.651222789123456789)', 4326) )
			}

			city {
				name 'München'
				population 1471508
				location( createGeometry('POINT(48.13722289876543 11.57555687654323)', 4326) )
			}
		}


		def converter = new InstanceToJson(true, 14)

		def jsonStr = converter.toJsonString(instances, SimpleLog.CONSOLE_LOG)
		println jsonStr

		def json = new JsonSlurper().parseText(jsonStr)

		// build expected object more than 14 truncated to 14
		def expected = [
			type: "FeatureCollection",
			features: [
				[
					type: "Feature",
					"@type": "city",
					geometry: [
						type: "MultiPoint",
						coordinates: [
							[
								49.87283378911236,
								8.65122278912346
							]
						]
					],
					properties: [
						population: 158254,
						name: "Darmstadt",
						location: [
							type: "Point",
							coordinates: [
								49.87283378911236,
								8.65122278912346
							]
						]
					]
				],
				[
					type: "Feature",
					"@type": "city",
					geometry: [
						type: "MultiPoint",
						coordinates: [
							[
								48.13722289876544,
								11.57555687654323
							]
						]
					],
					properties: [
						population: 1471508,
						name: "München",
						location: [
							type: "Point",
							coordinates: [
								48.13722289876544,
								11.57555687654323
							]
						]
					]
				]
			],
			"@namespaces": [:]
		]

		// Convert the Groovy map to a JSON string
		def jsonString = JsonOutput.toJson(json)


		assertEquals(expected, json)
	}

	@Test
	void testGeoJsonSimpleMixed_7_and_14() {

		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				population(Integer)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				population 158254
				location( createGeometry('POINT(49.8728337891123 8.651222789123)', 4326) )
			}

			city {
				name 'München'
				population 1471508
				location( createGeometry('POINT(48.137222 11.575556)', 4326) )
			}
		}


		def converter = new InstanceToJson(true, 14)

		def jsonStr = converter.toJsonString(instances, SimpleLog.CONSOLE_LOG)
		println jsonStr

		def json = new JsonSlurper().parseText(jsonStr)

		// build expected object
		def expected = [
			type: "FeatureCollection",
			features: [
				[
					type: "Feature",
					"@type": "city",
					geometry: [
						type: "MultiPoint",
						coordinates: [
							[
								49.8728337891123,
								8.651222789123
							]
						]
					],
					properties: [
						population: 158254,
						name: "Darmstadt",
						location: [
							type: "Point",
							coordinates: [
								49.8728337891123,
								8.651222789123
							]
						]
					]
				],
				[
					type: "Feature",
					"@type": "city",
					geometry: [
						type: "MultiPoint",
						coordinates: [[48.137222, 11.575556]]
					],
					properties: [
						population: 1471508,
						name: "München",
						location: [
							type: "Point",
							coordinates: [48.137222, 11.575556]
						]
					]
				]
			],
			"@namespaces": [:]
		]

		// Convert the Groovy map to a JSON string
		def jsonString = JsonOutput.toJson(json)


		assertEquals(expected, json)
	}
}
