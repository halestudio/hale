/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.common.schema.groovy

import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import groovy.test.GroovyTestCase



/**
 * Tests for the {@link DefinitionAccessor}.
 * 
 * @author Simon Templer
 */
class DefinitionAccessorTest extends GroovyTestCase {

	private static final String MAIN_NS = 'http://www.example.com'

	private static final String PROPERTY_NS = 'http://www.example.com/property'

	private static final String SPECIAL_NS = 'totally-different-namespace'

	private TypeDefinition itemType;

	private TypeDefinition orderType;

	@Override
	protected void setUp() throws Exception {
		new SchemaBuilder(defaultPropertyTypeNamespace: PROPERTY_NS).schema(MAIN_NS) {
			itemType = Item {
				name()
				price(Double)
				alternateNames { name(cardinality: '*') }
				_(cardinality: 0..1) {
					name(namespace: SPECIAL_NS)
					link(URI)
				}
			}

			orderType = Order {
				_(cardinality: '+') {
					item(itemType)
					quantity(Integer)
				}
			}
		}
	}

	/**
	 * Tests invoking the accessor through the meta class.
	 */
	void testMetaClass() {
		Definition definition = itemType.accessor().price as Definition
		assertNotNull definition
	}

	/**
	 * Simple test w/ all found paths.
	 */
	void testSimpleAll() {
		def names = new DefinitionAccessor(itemType).name.all()

		assertNotNull names
		assertEquals 2, names.size()

		assertEquals 2, names[0].elements.size()
		Definition directName = names[0].elements.last()
		assertNotNull directName

		assertEquals 3, names[1].elements.size()
		Definition groupName = names[1].elements.last()
		assertNotNull groupName
	}

	/**
	 * Simple test w/ all found paths.
	 */
	void testNestingAll() {
		def itemPrices = new DefinitionAccessor(orderType).item.price.all()

		assertNotNull itemPrices
		assertEquals 1, itemPrices.size()

		assertEquals 4, itemPrices[0].elements.size()
		Definition price = itemPrices[0].elements.last()
		assertNotNull price
	}

	/**
	 * Test w/ a fixed namespace when expecting a unique result.
	 */
	void testNamespace() {
		Definition name = new DefinitionAccessor(itemType).name(SPECIAL_NS) as Definition
		assertNotNull name
	}

	/**
	 * Simple test where property access should result in an exception when
	 * expecting a unique result.
	 */
	void testSimpleSingleStrictFail() {
		try {
			def name = new DefinitionAccessor(itemType).name.eval()
		} catch (IllegalStateException e) {
			return
		}

		fail()
	}

	/**
	 * Simple test w/ getting a single result from a non-unique result.
	 */
	void testSimpleSingleLax() {
		Definition name = new DefinitionAccessor(itemType).name.eval(false).elements.last()
		assertNotNull name
	}
}
