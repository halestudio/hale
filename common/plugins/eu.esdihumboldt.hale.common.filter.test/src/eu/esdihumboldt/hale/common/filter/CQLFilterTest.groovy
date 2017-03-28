/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.filter;

import static org.junit.Assert.*

import org.junit.Test

import eu.esdihumboldt.hale.common.instance.model.Filter
import eu.esdihumboldt.hale.common.instance.model.Instance

/**
 * Tests for CQL filter.
 * 
 * @author Simon Templer
 */
class CQLFilterTest extends AbstractFilterTest {

	Filter filter(String expr) {
		new FilterGeoCqlImpl(expr)
	}

	@Test
	void testNullSchema() {
		assertTrue(filter("legalStatus IS NULL").match(max))
		assertFalse(filter("legalStatus IS NOT NULL").match(max))
	}

	@Test
	void testNullNoSchema() {
		assertTrue(filter("legalStatus IS NULL").match(maxNoSchema))
		assertFalse(filter("legalStatus IS NOT NULL").match(maxNoSchema))
	}

	@Test
	void testLikeSchema() {
		assertTrue(filter("name LIKE 'Max %'").match(max))
		assertFalse(filter("name LIKE 'Martha %'").match(max))
	}

	@Test
	void testLikeNoSchema() {
		assertTrue(filter("name LIKE 'Max %'").match(maxNoSchema))
		assertFalse(filter("name LIKE 'Martha %'").match(maxNoSchema))
	}
}
