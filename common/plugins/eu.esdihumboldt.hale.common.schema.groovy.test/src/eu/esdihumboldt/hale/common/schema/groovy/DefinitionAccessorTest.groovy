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

import eu.esdihumboldt.hale.common.schema.groovy.DefinitionAccessor.Mode
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition



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
	 * Simple test w/ {@link Mode#ALL}.
	 */
	void testSimpleAll() {
		DefinitionAccessor type = new DefinitionAccessor(itemType)
		type.accessorMode = Mode.ALL

		def names = type.name

		assertNotNull names
		assertEquals 2, names.size()

		Definition directName = names[0] as Definition
		assertNotNull directName

		Definition groupName = names[1] as Definition
		assertNotNull groupName
	}

	/**
	 * Simple test w/ {@link Mode#ALL}.
	 */
	void testNestingAll() {
		DefinitionAccessor order = new DefinitionAccessor(orderType)
		order.accessorMode = Mode.ALL

		def itemPrices = order.item[0].price

		assertNotNull itemPrices
		assertEquals 1, itemPrices.size()

		Definition price = itemPrices[0] as Definition
		assertNotNull price
	}

	/**
	 * Test w/ a fixed namespace in {@link Mode#SINGLE_STRICT}.
	 */
	void testNamespace() {
		DefinitionAccessor type = new DefinitionAccessor(itemType)

		Definition name = type.name(SPECIAL_NS) as Definition
		assertNotNull name
	}

	/**
	 * Simple test where property access should result in an exception w/
	 * {@link Mode#SINGLE_STRICT}.
	 */
	void testSimpleSingleStrictFail() {
		DefinitionAccessor type = new DefinitionAccessor(itemType)
		try {
			def names = type.name
		} catch (IllegalStateException e) {
			return
		}

		fail()
	}

	/**
	 * Simple test w/ {@link Mode#SINGLE_LAX}
	 */
	void testSimpleSingleLax() {
		DefinitionAccessor type = new DefinitionAccessor(itemType)
		type.accessorMode = Mode.SINGLE_LAX

		Definition name = type.name.toDefinition()
		assertNotNull name
	}
}
