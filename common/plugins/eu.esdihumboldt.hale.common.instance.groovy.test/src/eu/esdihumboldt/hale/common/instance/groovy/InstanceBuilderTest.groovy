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

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.instance.model.Group
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.test.TestUtil
import groovy.test.GroovyTestCase
import groovy.transform.CompileStatic


/**
 * Tests for {@link InstanceBuilder}.
 * 
 * @author Simon Templer
 */
class InstanceBuilderTest extends GroovyTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp()

		TestUtil.startConversionService()
	}

	/**
	 * Test creating a single instance w/o associated schema.
	 */
	void testSchemaLessInstance() {
		Instance instance = new InstanceBuilder().instance {
			id(12)
			name('test')
			name('test2')
			type { href('http://example.com/some-location') }
		}

		checkDefaultSchemaLessInstance(instance)
	}

	@CompileStatic
	private void checkDefaultSchemaLessInstance(Instance instance) {
		assertNotNull instance
		assertNull instance.value

		// id
		QName idName = new QName('id')
		assertEquals 1, instance.getProperty(idName).size()
		assertEquals 12, instance.getProperty(idName)[0]

		// name
		QName nameName = new QName('name')
		assertEquals 2, instance.getProperty(nameName).size()
		assertEquals 'test', instance.getProperty(nameName)[0]
		assertEquals 'test2', instance.getProperty(nameName)[1]

		// type
		QName typeName = new QName('type')
		assertEquals 1, instance.getProperty(typeName).size()
		assertTrue instance.getProperty(typeName)[0] instanceof Group
		Group type = (Group) instance.getProperty(typeName)[0]

		// type.href
		QName hrefName = new QName('href')
		assertEquals 1, type.getProperty(hrefName).size()
		assertEquals 'http://example.com/some-location', type.getProperty(hrefName)[0]
	}

	/**
	 * Test creating a single instance w/o associated schema using the type safe builder API.
	 */
	@CompileStatic
	void testSchemaLessSafeAPI() {
		InstanceBuilder b = new InstanceBuilder()
		Instance instance = b.createInstance {
			b.createProperty('id', 12)
			b.createProperty('name', 'test')
			b.createProperty('name', 'test2')
			b.createProperty('type') {
				b.createProperty('href', 'http://example.com/some-location')
			}
		}

		checkDefaultSchemaLessInstance(instance)
	}

	/**
	 * Test creating a collection of instances bound to a schema.
	 */
	void testSchemaCollection() {
		// create the schema
		Schema schema = new SchemaBuilder().schema {
			def itemType = ItemType {
				id(Long)
				name(String)
				price(Double)
				description(String)
			}

			OrderType {
				item(itemType)
				quantity(Integer)
			}
		}

		QName orderTypeName = new QName('OrderType')

		// create the instance collection
		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			OrderType {
				item {
					id(12)
					name('item12')
					price(1.2)
					description('Item number 12')
				}
				quantity(1)
			}

			OrderType {
				item {
					id('42') // auto-convert to Long
					name('item42')
					price(4.2)
					description('Item number 42')
				}
				quantity(3)
			}
		}

		QName itemName = new QName('item')
		QName idName = new QName('id')
		QName quantityName = new QName('quantity')

		assertNotNull instances
		assertFalse instances.empty
		assertEquals 2, instances.size()

		ResourceIterator<Instance> it = instances.iterator()
		int index = 0;
		try {
			while (it.hasNext()) {
				Instance instance = it.next()

				assertNotNull instance.definition
				assertEquals schema.getType(orderTypeName), instance.definition

				if (index == 1) {
					// check second instance

					// item
					assertEquals 1, instance.getProperty(itemName).size()
					assertTrue instance.getProperty(itemName)[0] instanceof Instance
					Instance item = instance.getProperty(itemName)[0]

					// id
					assertEquals 1, item.getProperty(idName).size()
					assertTrue item.getProperty(idName)[0] instanceof Long
					assertEquals 42, item.getProperty(idName)[0]

					// quantity
					assertEquals 1, instance.getProperty(quantityName).size()
					assertEquals 3, instance.getProperty(quantityName)[0]
				}

				index++
			}
		} finally {
			it.close()
		}
	}
}
