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

import org.junit.Before
import org.junit.Test

import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema

/**
 * TODO Type description
 * 
 * @author simon
 */
public class CQLFilterTest {

	private static final String defaultNs = "http://www.my.namespace"

	private Instance maxNoSchema

	private Schema schema
	private Instance max

	@Before
	void setup() {
		// build instance
		maxNoSchema = new InstanceBuilder().instance {
			name 'Max Mustermann'
			age 31
			address {
				street 'Musterstrasse'
				number 12
				city 'Musterstadt'
			}
			address {
				street 'Taubengasse'
				number 13
			}
			relative('father') {
				name 'Markus Mustermann'
				age 56
			}
			legalStatus null
		}



		// build schema
		schema = new SchemaBuilder().schema(defaultNs) {
			Person {
				name()
				age(Integer)
				address(cardinality: '0..n') {
					street()
					number()
					city()
				}
				relative(cardinality: '0..n', String) {
					name()
					age(Integer)
				}
				legalStatus(String)
			}
		}

		// build instance
		max = new InstanceBuilder(types: schema).Person {
			name 'Max Mustermann'
			age 31
			address {
				street 'Musterstrasse'
				number 12
				city 'Musterstadt'
			}
			address {
				street 'Taubengasse'
				number 13
			}
			relative('father') {
				name 'Markus Mustermann'
				age 56
			}
			legalStatus null
		}
	}

	@Test
	void testNullSchema() {
		assertTrue(new FilterGeoCqlImpl("legalStatus IS NULL").match(max))
		assertFalse(new FilterGeoCqlImpl("legalStatus IS NOT NULL").match(max))
	}

	@Test
	void testNullNoSchema() {
		assertTrue(new FilterGeoCqlImpl("legalStatus IS NULL").match(maxNoSchema))
		assertFalse(new FilterGeoCqlImpl("legalStatus IS NOT NULL").match(maxNoSchema))
	}

	@Test
	void testLikeSchema() {
		assertTrue(new FilterGeoCqlImpl("name LIKE 'Max %'").match(max))
		assertFalse(new FilterGeoCqlImpl("name LIKE 'Martha %'").match(max))
	}

	@Test
	void testLikeNoSchema() {
		assertTrue(new FilterGeoCqlImpl("name LIKE 'Max %'").match(maxNoSchema))
		assertFalse(new FilterGeoCqlImpl("name LIKE 'Martha %'").match(maxNoSchema))
	}

}
