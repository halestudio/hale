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

package eu.esdihumboldt.hale.ui.codelist.service.internal.config

import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.codelist.config.CodeListAssociations;
import eu.esdihumboldt.hale.common.codelist.config.CodeListAssociationsType;
import eu.esdihumboldt.hale.common.codelist.config.CodeListReference;
import eu.esdihumboldt.hale.common.codelist.config.DummyEntityKey;
import groovy.transform.CompileStatic

/**
 * Tests for converting {@link CodeListAssociations} to DOM and back.
 * 
 * @author Simon Templer
 */
@CompileStatic
class CodeListAssociationsTypeTest extends GroovyTestCase {

	/**
	 * Test converting to DOM and back.
	 */
	void testToFrom() {
		CodeListAssociationsType conv = new CodeListAssociationsType()

		CodeListAssociations org = new CodeListAssociations()

		DummyEntityKey key1 = new DummyEntityKey([
			new QName('http://www.mynamespace.com/', 'MyType'),
			new QName('a')
		])
		CodeListReference ref1 = new CodeListReference(namespace: 'codelists', identifier: '12')
		org.associations.put(key1, ref1)

		DummyEntityKey key2 = new DummyEntityKey([
			new QName('http://www.mynamespace.com/', 'MyOtherType'),
			new QName('a'),
			new QName('x'),
			new QName('q')
		])
		CodeListReference ref2 = new CodeListReference(namespace: 'codelists', identifier: '4711')
		org.associations.put(key2, ref2)

		// to dom
		Element dom = conv.toDOM(org)

		// from dom
		CodeListAssociations duped = conv.fromDOM(dom, (Void)null)

		CodeListReference refDupe1 = duped.associations[key1]
		assertNotNull refDupe1
		assertEquals '12', refDupe1.identifier

		CodeListReference refDupe2 = duped.associations[key2]
		assertNotNull refDupe2
		assertEquals '4711', refDupe2.identifier
	}

	/**
	 * Test converting
	 */
	void testRegression1() {
		def markup = '''<associations>
		  <codeList identifier="4711" namespace="codelists">
		    <entity>
		      <type name="MyOtherType" namespace="http://www.mynamespace.com/"/>
		      <property name="a" namespace=""/>
		      <property name="x" namespace=""/>
		      <property name="q" namespace=""/>
		    </entity>
		  </codeList>
		  <codeList identifier="12" namespace="codelists">
		    <entity>
		      <type name="MyType" namespace="http://www.mynamespace.com/"/>
		      <property name="a" namespace=""/>
		    </entity>
		  </codeList>
		</associations>'''

		def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		def inputStream = new ByteArrayInputStream(markup.bytes)
		def associations     = builder.parse(inputStream).documentElement

		CodeListAssociationsType conv = new CodeListAssociationsType()
		CodeListAssociations cla = conv.fromDOM(associations, (Void)null)

		assertEquals 2, cla.associations.size()

		DummyEntityKey key2 = new DummyEntityKey([
			new QName('http://www.mynamespace.com/', 'MyOtherType'),
			new QName('a'),
			new QName('x'),
			new QName('q')
		])

		CodeListReference refDupe2 = cla.associations[key2]
		assertNotNull refDupe2
		assertEquals '4711', refDupe2.identifier
	}
}
