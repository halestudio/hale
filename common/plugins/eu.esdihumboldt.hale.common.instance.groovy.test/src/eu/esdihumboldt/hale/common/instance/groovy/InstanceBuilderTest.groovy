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


/**
 * Tests for {@link InstanceBuilder}.
 * 
 * @author Simon Templer
 */
class InstanceBuilderTest extends GroovyTestCase {

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
		Group type = instance.getProperty(typeName)[0]

		// type.href
		QName hrefName = new QName('href')
		assertEquals 1, type.getProperty(hrefName).size()
		assertEquals 'http://example.com/some-location', type.getProperty(hrefName)[0]
	}
}
