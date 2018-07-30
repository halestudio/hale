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
 * Tests for Config
 * 
 * @author Simon Templer
 */
class ConfigTest {

	@Test
	void testNestedKey() {
		def c = new Config()

		c['x.y.z'] = 'foo'

		// correctly represented in map
		assertEquals('foo', c.asMap().x.y.z)

		// access works
		assertEquals('foo', c['x.y.z'])
	}

	@Test(expected = IllegalStateException)
	void testConflict() {
		def c = new Config()

		c['x.y.z'] = 'foo'

		// cannot assign value here
		c['x.y.z.a'] = 'bar'
	}

	@Test(expected = IllegalArgumentException)
	void testInvalidKey1() {
		def c = new Config()

		c[''] = 'foo'
	}

	@Test(expected = IllegalArgumentException)
	void testInvalidKey2() {
		def c = new Config()

		def value = c['']
	}

	void testInvalidKey3() {
		def c = new Config()

		c['c..x'] = 'foo'
	}
}
