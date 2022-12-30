/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.instance.groovy

import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.test.TestUtil


/**
 * Family instance links accessor tests.
 * 
 * @author Simon Templer
 */
class InstanceFamilyAccessorTest extends GroovyTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp()

		TestUtil.startConversionService()
	}

	void testSchema() {
		String defaultNs = "http://www.my.namespace"

		// build schema
		Schema schema = new SchemaBuilder().schema(defaultNs) {
			PersonType {
				name()
				age(Integer)
				addressId(cardinality: '0..n') {
				}
			}

			AddressType(display: 'Address') {
				id()
				street()
				number()
				city()
			}
		}

		// build instances
		Instance person = new InstanceBuilder(types: schema).PersonType {
			name 'Max Mustermann'
			age 31
			addressId('1')
			addressId('2')
		}
		def addresses = new InstanceBuilder(types: schema).createCollection {
			AddressType {
				id('1')
				street 'Musterstrasse'
				number 12
				city 'Musterstadt'
			}
			AddressType {
				id('2')
				street 'Taubengasse'
				number 13
			}
		}

		// build family
		FamilyInstance family = new FamilyInstanceImpl(person)
		def iterator = addresses.iterator()
		while (iterator.hasNext()) {
			family.addChild(new FamilyInstanceImpl(iterator.next()))
		}
		iterator.close()

		// test instance accessor
		assertEquals 'Max Mustermann', family.properties.name.value()

		// children size (with different addressing modes)
		assertEquals 2, family.links.AddressType(defaultNs).list().size()
		assertEquals 2, family.links.AddressType.list().size()
		assertEquals 2, family.links.Address(defaultNs).list().size()
		assertEquals 2, family.links.Address.list().size()

		// instance
		def children = family.links.Address.list()
		// must be family instance
		children.each { assertTrue it instanceof FamilyInstance }

		// each
		def ids = []
		family.links.Address.each { address ->
			ids << address.p.id.value()
		}
		assertEquals 2, ids.size()
		assertEquals '1', ids[0]
		assertEquals '2', ids[1]

		// chaining with Instance accessor
		ids = []
		family.links.Address.properties.id.each { ids << it }
		assertEquals 2, ids.size()
		assertEquals '1', ids[0]
		assertEquals '2', ids[1]

		// alternative calls
		assertEquals 2, family.links.Address.properties.id.list().size()
		assertEquals 2, family.links.Address.p.id.list().size()
		assertEquals 2, family.children().Address.accessor().id.list().size()
	}
}
