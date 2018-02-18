/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.util.groovy.collector

import static org.junit.Assert.*
import groovy.json.JsonOutput

import org.junit.Test

/**
 * Tests for statistics collector.
 * 
 * @author Simon Templer
 */
class StatsCollectorTest {

	private StatsCollector createTestCollector() {
		def c = new StatsCollector()

		def counter = c.test.counter
		counter++
		c.test.a = 'a'
		c.test.b.a = 'ba'
		c.test.b.b = 'bb'
		c.test.c.c << 1
		c.test.c.c << 2
		c.test.c.c.x = 'x'

		return c
	}

	@Test
	void testSerialize() {
		def c = createTestCollector()

		def json = c.saveToJson(false)
		println(JsonOutput.prettyPrint(json))
		assertNotNull(json)
		assertFalse(json.empty)

		def c2 = new StatsCollector()
		c2.loadFromJson(json)
		def json2 = c2.saveToJson(false)

		assertEquals(json, json2)
	}

	@Test
	void testSerializeCompact() {
		def c = createTestCollector()

		def json = c.saveToJson(true)
		println(JsonOutput.prettyPrint(json))
		assertNotNull(json)
		assertFalse(json.empty)

		def c2 = new StatsCollector()
		c2.loadFromJson(json)
		def json2 = c2.saveToJson(true)

		assertEquals(json, json2)
	}
}
