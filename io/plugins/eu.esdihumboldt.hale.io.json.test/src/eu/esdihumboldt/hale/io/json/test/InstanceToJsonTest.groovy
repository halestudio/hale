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

import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.io.json.writer.InstanceToJson
import groovy.json.JsonSlurper

class InstanceToJsonTest {

	// schema with types for testing
	Schema schema = new SchemaBuilder().schema {
		SimpleType {
			id(String)
			name(String)
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

}
