/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.cst.functions.groovy.helpers.util

import static org.junit.Assert.*

import org.junit.Test

/**
 * Tests for collector usage.
 * 
 * @author Simon Templer
 */
class CollectorTest {

	@Test
	void testList() {
		def c = new Collector()

		c.list << 'a'
		c.list << 'b'
		c['list'] << 'c'

		def list = c.list.values()
		checkList(list)

		list = c.list as List
		checkList(list)

		def newList = []
		c.list.each { newList << it }
		checkList(newList)

		assertEquals('a', c.list.value())

		list = c.list.clear()
		checkList(list)

		assertNull(c.list.value())
	}

	@Test
	void testConsumeList() {
		def c = new Collector()

		c.list << 'a'
		c.list << 'b'
		c['list'] << 'c'

		def newList = []
		c.list.consume { newList << it }
		checkList(newList)

		assertNull(c.list.value())
	}

	private void checkList(List<?> list) {
		assertEquals(3, list.size())
		assertTrue(list.contains('a'))
		assertTrue(list.contains('b'))
		assertTrue(list.contains('c'))
	}

	@Test
	void testNested() {
		def c = new Collector()

		c.l1.l2.a = 'a'
		c.l1['l2']['b'] = 'b'
		c.l1['l2'].c = 'c'

		def keyList = []
		def valueList = []

		c.l1.l2.each { key, values ->
			keyList << key
			assertEquals(1, values.size())
			valueList << values[0]
		}

		checkList(keyList)
		checkList(valueList)

		assertNull(c.l1.value())
	}

	@Test
	void testConsumeMap() {
		def c = new Collector()

		c.l1.l2.a = 'a'
		c.l1['l2']['b'] = 'b'
		c.l1['l2'].c = 'c'

		c.l1['l2'].c.x = 'x'

		assertNotNull(c.l1.l2.a.value())
		assertEquals('x', c.l1.l2.c.x.value())

		def keyList = []
		def valueList = []

		c.l1.l2.consume { key, values ->
			keyList << key
			assertEquals(1, values.size())
			valueList << values[0]
		}

		checkList(keyList)
		checkList(valueList)

		assertNull(c.l1.l2.a.value())
		assertEquals('x', c.l1.l2.c.x.value())
	}

	@Test
	void testIntKeys() {
		def c = new Collector()

		c.l1[0] = 'a'
		c.l1[1] = 'b'
		c.l1[2] = 'c'

		def keyList = []
		def valueList = []

		c.l1.consume { key, values ->
			keyList << key
			assertEquals(1, values.size())
			valueList << values[0]
		}

		assertEquals(3, keyList.size())
		assertEquals(0, keyList[0])
		assertEquals(1, keyList[1])
		assertEquals(2, keyList[2])

		checkList(valueList)
	}

	@Test
	void testMixedKeys() {
		def c = new Collector()

		def v1 = 'a'
		def v2 = URI.create('#')
		def v3 = 1.2f

		c.l1[v1] = 'a'
		c.l1[v2] = 'b'
		c.l1[v3] = 'c'

		c.l1[v3].x = 'x'

		def keyList = []
		def valueList = []

		c.l1.consume { key, values ->
			keyList << key
			assertEquals(1, values.size())
			valueList << values[0]
		}

		assertEquals(3, keyList.size())
		assertTrue(keyList.contains(v1))
		assertTrue(keyList.contains(v2))
		assertTrue(keyList.contains(v3))

		checkList(valueList)

		assertEquals('x', c.l1[v3].x.value())
	}

	@Test
	void testEachCollector() {
		def c = new Collector()

		def v1 = 'a'
		def v2 = URI.create('#')
		def v3 = 1.2f

		c.l1[v1] = 'a'
		c.l1[v2] = 'b'
		c.l1[v3] = 'c'

		def keyList = []
		def valueList = []

		c.l1.eachCollector { key, collector ->
			keyList << key
			assertTrue(collector instanceof Collector)
			assertEquals(1, collector.values().size())
			valueList << collector.values()[0]
		}

		assertEquals(3, keyList.size())
		assertTrue(keyList.contains(v1))
		assertTrue(keyList.contains(v2))
		assertTrue(keyList.contains(v3))

		checkList(valueList)
	}

	@Test
	void testGString() {
		def c = new Collector()

		def var = 'list'

		c.list << 'a'
		c["$var"] << 'b'
		c["list"] << 'c'

		def list = c.list.values()
		checkList(list)
	}
}
