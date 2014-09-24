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

package eu.esdihumboldt.hale.common.align.groovy.accessor

import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition



/**
 * Tests for the {@link DefinitionAccessor}.
 * 
 * @author Simon Templer
 */
class EntityAccessorTest extends GroovyTestCase {

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
		TypeEntityDefinition itemEntity = new TypeEntityDefinition(itemType, SchemaSpaceID.SOURCE, null)

		EntityDefinition definition = itemEntity.accessor().price as EntityDefinition
		assertNotNull definition
	}

	/**
	 * Simple test w/ all found paths.
	 */
	void testSimpleAll() {
		TypeEntityDefinition itemEntity = new TypeEntityDefinition(itemType, SchemaSpaceID.SOURCE, null)

		def names = new EntityAccessor(itemEntity).name.all()

		assertNotNull names
		assertEquals 2, names.size()

		assertEquals 2, names[0].elements.size()
		PathElement directName = names[0].elements.last()
		assertNotNull directName

		assertEquals 3, names[1].elements.size()
		PathElement groupName = names[1].elements.last()
		assertNotNull groupName
	}

	/**
	 * Simple test w/ all found paths.
	 */
	void testNesting() {
		TypeEntityDefinition orderEntity = new TypeEntityDefinition(orderType, SchemaSpaceID.SOURCE, null)
		EntityDefinition itemPrices = new EntityAccessor(orderEntity).item(filter: 'parent.quantity > 1').price as EntityDefinition

		assertNotNull itemPrices

		assertEquals 3, itemPrices.propertyPath.size()

		ChildContext itemContext = itemPrices.propertyPath[1]
		assertNotNull itemContext.condition.filter
	}

	/**
	 * Test w/ a fixed namespace and an index context when expecting a unique result.
	 */
	void testNamespaceIndex() {
		TypeEntityDefinition itemEntity = new TypeEntityDefinition(itemType, SchemaSpaceID.SOURCE, null)
		EntityDefinition name = new EntityAccessor(itemEntity).name(SPECIAL_NS, 0) as EntityDefinition
		assertNotNull name

		assertEquals 2, name.propertyPath.size()

		ChildContext nameContext = name.propertyPath[1]
		assertNull nameContext.condition
		assertNull nameContext.contextName
		assertEquals 0, nameContext.index
	}

	/**
	 * Test w/ a fixed namespace and an index context when expecting a unique result.
	 */
	void testNamespaceIndexChildContext() {
		ChildContext testContext = new ChildContext(null, 0, null, null)

		TypeEntityDefinition itemEntity = new TypeEntityDefinition(itemType, SchemaSpaceID.SOURCE, null)
		EntityDefinition name = new EntityAccessor(itemEntity).name(SPECIAL_NS, testContext) as EntityDefinition
		assertNotNull name

		assertEquals 2, name.propertyPath.size()

		ChildContext nameContext = name.propertyPath[1]
		assertNull nameContext.condition
		assertNull nameContext.contextName
		assertEquals 0, nameContext.index
	}

	/**
	 * Test w/ a fixed namespace and an index context when expecting a unique result.
	 */
	void testNamespaceIndexChildContextType() {
		ChildContextType testContext = new ChildContextType()
		testContext.index = 0

		TypeEntityDefinition itemEntity = new TypeEntityDefinition(itemType, SchemaSpaceID.SOURCE, null)
		EntityDefinition name = new EntityAccessor(itemEntity).name(SPECIAL_NS, testContext) as EntityDefinition
		assertNotNull name

		assertEquals 2, name.propertyPath.size()

		ChildContext nameContext = name.propertyPath[1]
		assertNull nameContext.condition
		assertNull nameContext.contextName
		assertEquals 0, nameContext.index
	}

	/**
	 * Simple test where property access should result in an exception when
	 * expecting a unique result.
	 */
	void testSimpleSingleStrictFail() {
		try {
			TypeEntityDefinition itemEntity = new TypeEntityDefinition(itemType, SchemaSpaceID.SOURCE, null)
			def name = new EntityAccessor(itemEntity).name.eval()
		} catch (IllegalStateException e) {
			return
		}

		fail()
	}
}
