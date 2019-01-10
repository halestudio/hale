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

package eu.esdihumboldt.util.config

import static org.junit.Assert.*

import org.junit.Test

/**
 * Tests for ConfigMerger.
 * 
 * @author Simon Templer
 */
class ConfigMergerTest {

	@Test
	void testSimple() {
		def c1 = new Config([test1: 'foo'])
		def c2 = new Config([test2: 'bar'])

		def expected = [:]
		expected.putAll(c1.asMap())
		expected.putAll(c2.asMap())

		// merge
		def merged = ConfigMerger.mergeConfigs(c1, c2)
		assertTrue(merged instanceof Config)

		assertEquals(expected, merged.asMap())
	}

	@Test
	void testSimpleOverride() {
		def c1 = new Config([test: 'foo'])
		def c2 = new Config([test: 'bar'])
		def c3 = new Config([test: 'test'])

		def expected = [test: 'test']

		// merge
		def merged = ConfigMerger.mergeConfigs(c1, c2, c3)
		assertTrue(merged instanceof Config)

		assertEquals(expected, merged.asMap())
	}

	@Test
	void testNested() {
		def c1 = new Config([test1: 'foo', sub: [x: 'y']])
		def c2 = new Config([test2: 'bar', sub: [y: 'x']])

		def expected = [test1: 'foo', test2: 'bar', sub: [x: 'y', y: 'x']]

		// merge
		def merged = ConfigMerger.mergeConfigs(c1, c2)
		assertTrue(merged instanceof Config)

		assertEquals(expected, merged.asMap())
	}

	@Test
	void testNestedOverride() {
		def c1 = new Config([test1: 'foo', sub: [search: 'bing']])
		def c2 = new Config([test2: 'bar', sub: [search: 'google']])

		def expected = [:]
		expected.putAll(c1.asMap())
		expected.putAll(c2.asMap())

		// merge
		def merged = ConfigMerger.mergeConfigs(c1, c2)
		assertTrue(merged instanceof Config)

		assertEquals(expected, merged.asMap())
	}

	@Test
	void testLists() {
		def c1 = new Config([list: ['foo']])
		def c2 = new Config([list: ['bar']])

		def expected = [list: ['foo', 'bar']]

		// merge
		def merged = ConfigMerger.mergeConfigs(c1, c2)
		assertTrue(merged instanceof Config)

		assertEquals(expected, merged.asMap())
	}

	@Test
	void testListAndValue() {
		def c1 = new Config([list: 'foo'])
		def c2 = new Config([list: ['bar']])

		def expected = [list: ['foo', 'bar']]

		// merge
		def merged = ConfigMerger.mergeConfigs(c1, c2)
		assertTrue(merged instanceof Config)

		assertEquals(expected, merged.asMap())
	}
}
