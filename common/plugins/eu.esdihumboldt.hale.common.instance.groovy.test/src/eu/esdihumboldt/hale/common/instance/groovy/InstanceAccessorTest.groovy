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

package eu.esdihumboldt.hale.common.instance.groovy

import eu.esdihumboldt.hale.common.instance.model.Group
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.test.TestUtil


/**
 * Instance property accessor tests.
 * 
 * @author Simon Templer
 */
class InstanceAccessorTest extends GroovyTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp()

		TestUtil.startConversionService()
	}

	void testSchemaLess() {
		// build instance
		Instance instance = new InstanceBuilder().instance {
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
		}

		// test accessor

		// simple property
		assertEquals 'Max Mustermann', new InstanceAccessor(instance).name.first()

		// .properties access
		assertEquals 31, instance.properties.age.first()

		// .accessor() access
		assertEquals 31, instance.accessor().age.first()

		// nested
		def streets = instance.properties.address.street.list()
		assertEquals 2, streets.size()

		// group
		def addresses = instance.properties.address.list()
		assertEquals 2, addresses.size()
		addresses.each { assertTrue it instanceof Group }

		// each
		def numbers = []
		instance.properties.address.number.each { numbers << it }
		assertEquals 2, numbers.size()
		assertEquals 12, numbers[0]
		assertEquals 13, numbers[1]

		// instance value
		assertEquals 'father', instance.properties.relative.value()
		assertTrue instance.properties.relative.first() instanceof Instance
	}

	void testAccessNullProperties() {
		// build instance (schema less)
		Instance instance = new InstanceBuilder().instance { name null }

		// test access of null property
		assertNull new InstanceAccessor(instance, true).name.first()

		// test access of null child property
		assertNull new InstanceAccessor(instance, true).name.some.first()

		def list = new InstanceAccessor(instance, true).name.list()
		assertEquals(1, list.size())
		assertNull(list[0])
	}

	void testNullProperties() {
		// build instance (schema less)
		Instance instance = new InstanceBuilder().instance { name null }

		// test access of null property
		assertNull new InstanceAccessor(instance).name.first()

		// test access of null child property
		assertNull new InstanceAccessor(instance).name.some.first()

		def list = new InstanceAccessor(instance).name.list()
		assertEquals(0, list.size())
	}

	void testNull() {
		// test access on null object
		assertNull new InstanceAccessor(null).name.first()

		def list = new InstanceAccessor(null).name.list()
		assertEquals(0, list.size())
	}

	void testNullMixed() {
		// build instances (schema less)
		Instance instance1 = new InstanceBuilder().instance { name 'Lisa' }
		Instance instance2 = new InstanceBuilder().instance { name null }
		Instance instance3 = new InstanceBuilder().instance { name 'Bart' }

		def instances = [
			instance1,
			null,
			instance2,
			instance3,
			null
		]

		// test access to first property
		assertEquals 'Lisa', new InstanceAccessor(instances).name.first()

		// list w/o null access
		def list = new InstanceAccessor(instances).name.list()
		assertEquals(2, list.size())
		assertEquals('Lisa', list[0])
		assertEquals('Bart', list[1])

		// list w/ null access
		def list2 = new InstanceAccessor(instances, true).name.list()
		assertEquals(3, list2.size())
	}

	void testSchema() {
		String defaultNs = "http://www.my.namespace"

		// build schema
		Schema schema = new SchemaBuilder().schema(defaultNs) {
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
			}
		}

		// build instance
		Instance instance = new InstanceBuilder(types: schema).Person {
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
		}

		// test accessor
		assertEquals 'Max Mustermann', instance.properties.name.value()

		// namespace
		assertEquals 31, instance.properties.age(defaultNs).value()

		// instance
		def addresses = instance.properties.address.list()
		assertEquals 2, addresses.size()
		// must be instance due to schema
		addresses.each { assertTrue it instanceof Instance }

		// each
		def numbers = []
		instance.properties.address.number.each { numbers << it }
		assertEquals 2, numbers.size()
		// numbers are converted to string
		assertEquals '12', numbers[0]
		assertEquals '13', numbers[1]

		// instance value
		assertEquals 'father', instance.properties.relative.value()
		assertTrue instance.properties.relative.first() instanceof Instance
	}

	void testAllChildren() {
		// build instance
		Instance instance = new InstanceBuilder().instance {
			name 'Max Mustermann'
			age 31
			address {
				street 'Musterstrasse'
				number 12
				city 'Musterstadt'
			}
			address2 {
				street 'Taubengasse'
				number 13
			}
			address3 {
				street 'Lalaweg'
				number 7
			}
		}

		def streets = instance.p.'*'.street.values()
		assertEquals(new HashSet([
			'Musterstrasse',
			'Taubengasse',
			'Lalaweg'
		]), new HashSet(streets))

		def numbers = instance.p.''.number.values()
		assertEquals(new HashSet([7, 12, 13]), new HashSet(numbers))
	}
}
