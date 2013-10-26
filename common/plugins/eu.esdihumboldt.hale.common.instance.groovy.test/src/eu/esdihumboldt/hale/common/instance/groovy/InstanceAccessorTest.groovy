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

}
