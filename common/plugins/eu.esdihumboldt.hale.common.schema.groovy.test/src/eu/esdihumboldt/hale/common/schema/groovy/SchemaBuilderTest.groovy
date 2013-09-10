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

import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition


/**
 * Test cases for the {@link SchemaBuilder}.
 * 
 * @author Simon Templer
 */
class SchemaBuilderTest extends GroovyTestCase {

	/**
	 * Creates a simple schema and checks if the correct names are assigned to
	 * types and properties.
	 */
	public void testSimpleSchemaNames() {
		def defaultNamespace = 'http://www.example.com'
		def ns1 = 'http://www.perpetummobile.org'
		def ns2 = 'org:standardout:test'
		def empty = ''

		Schema schema = new SchemaBuilder().schema(defaultNamespace) {
			T1 {
				a()
				b()
			}
			T2(namespace: ns1) {
				c(namespace: empty)
				d(namespace: ns1)
			}
			T3 { e(namespace: ns2) }
		}

		assertEquals "Number of types is incorrect", 3, schema.types.size()

		/*
		 * T1
		 */
		TypeDefinition type1 = schema.types.find {
			it.name.localPart == 'T1' && it.name.namespaceURI == defaultNamespace
		}
		assertNotNull type1

		assertEquals "Number of properties in T1 incorrect", 2, type1.children.size()

		def type1a = type1.children[0]
		assertNotNull type1a
		assertEquals 'a', type1a.name.localPart
		assertEquals defaultNamespace, type1a.name.namespaceURI

		def type1b = type1.children[1]
		assertNotNull type1b
		assertEquals 'b', type1b.name.localPart
		assertEquals defaultNamespace, type1b.name.namespaceURI

		/*
		 * T2
		 */
		TypeDefinition type2 = schema.types.find {
			it.name.localPart == 'T2' && it.name.namespaceURI == ns1
		}
		assertNotNull type2

		assertEquals "Number of properties in T2 incorrect", 2, type2.children.size()

		def type2c = type2.children[0]
		assertNotNull type2c
		assertEquals 'c', type2c.name.localPart
		assertEquals empty, type2c.name.namespaceURI

		def type2d = type2.children[1]
		assertNotNull type2d
		assertEquals 'd', type2d.name.localPart
		assertEquals ns1, type2d.name.namespaceURI

		/*
		 * T3
		 */
		TypeDefinition type3 = schema.types.find {
			it.name.localPart == 'T3' && it.name.namespaceURI == defaultNamespace
		}
		assertNotNull type3

		assertEquals "Number of properties in T3 incorrect", 1, type3.children.size()

		def type3e = type3.children[0]
		assertNotNull type3e
		assertEquals 'e', type3e.name.localPart
		assertEquals ns2, type3e.name.namespaceURI
	}
}
