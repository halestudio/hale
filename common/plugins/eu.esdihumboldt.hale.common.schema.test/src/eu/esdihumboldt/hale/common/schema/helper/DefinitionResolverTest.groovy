/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.helper

import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.paths.DefinitionResolver
import eu.esdihumboldt.util.groovy.paths.Path
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode



/**
 * Definition resolver tests.
 * 
 * @author Simon Templer
 */
@CompileStatic
class DefinitionResolverTest extends GroovyTestCase {

	private static final String MAIN_NS = 'http://www.example.com'

	private static final String PROPERTY_NS = 'http://www.example.com/property'

	private static final String SPECIAL_NS = 'totally-different-namespace'

	private TypeDefinition itemType;

	private TypeDefinition orderType;

	@CompileStatic(TypeCheckingMode.SKIP)
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
	 * Tests finding properties w/o a fixed namespace.
	 */
	void testFindPropertyIgnoreNamespace() {
		// item.name
		List<Path<Definition<?>>> paths = DefinitionResolver.findProperty(itemType, 'name', null);
		assertEquals 2, paths.size()

		// direct property
		Path<Definition<?>> path1 = paths[0]
		assertEquals 1, path1.elements.size()

		// inside group
		Path<Definition<?>> path2 = paths[1]
		assertEquals 2, path2.elements.size()
		assertTrue path2.elements[0] instanceof GroupPropertyDefinition

		// order.name
		paths = DefinitionResolver.findProperty(orderType, 'name', null);
		assertEquals 0, paths.size()

		// order.item
		paths = DefinitionResolver.findProperty(orderType, 'item', null);
		assertEquals 1, paths.size()
	}

	/**
	 * Tests finding properties w/o a fixed namespace and using the caching constraint.
	 */
	void testFindPropertyCached() {
		// item.name
		List<Path<Definition<?>>> paths = DefinitionResolver.findPropertyCached(itemType, 'name', null);
		assertEquals 2, paths.size()

		// direct property
		Path<Definition<?>> path1 = paths[0]
		assertEquals 1, path1.elements.size()

		// inside group
		Path<Definition<?>> path2 = paths[1]
		assertEquals 2, path2.elements.size()
		assertTrue path2.elements[0] instanceof GroupPropertyDefinition
	}

	/**
	 * Tests finding properties w/ a fixed namespace.
	 */
	void testFindPropertyNamespace() {
		// item.name
		List<Path<Definition<?>>> paths = DefinitionResolver.findProperty(itemType, 'name', SPECIAL_NS);
		assertEquals 1, paths.size()

		// inside group
		Path<Definition<?>> path1 = paths[0]
		assertEquals 2, path1.elements.size()
		assertTrue path1.elements[0] instanceof GroupPropertyDefinition
	}
}
