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
		assertEquals('a', list[0])
		assertEquals('b', list[1])
		assertEquals('c', list[2])
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
}
