/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.util.groovy.xml

import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.w3c.dom.Element
import org.w3c.dom.Node

import groovy.transform.CompileStatic
import groovy.xml.DOMBuilder
import groovy.xml.QName
import groovy.xml.dom.DOMCategory


/**
 * Tests for {@link NSDOMBuilder}.
 * 
 * @author Simon Templer
 */
class NSDOMBuilderTest extends GroovyTestCase {

	private static String NS1 = "http://www.example.com/ns1"

	private static String NS2 = "http://www.example.com/ns2"

	void testWriteRead() {
		NSDOMBuilder builder = NSDOMBuilder.newBuilder(ns1: NS1, ns2: NS2)
		Node nsroot = builder.'ns1:root' {
			'ns2:item' (att1: 'test', att2: 'text') {
				'ns1:test' ( 'hello' )
				'nons-ense' { text 'lalal' }
			}
			fix 'me'
		}

		writeReadTest(nsroot)
	}

	@CompileStatic
	void testWriteReadTypeSafeQName() {
		NSDOMBuilder b = NSDOMBuilder.newBuilder(ns1: NS1, ns2: NS2)
		// XXX for some reason call(QName, Closure) is not found
		Element nsroot = b(new QName(NS1, 'root'), [:]) {
			b(new QName(NS2, 'item'), [att1: 'test', att2: 'text']) {
				b(new QName(NS1, 'test'), 'hello')
				b('nons-ense') { b 'text', 'lalal' }
			}
			b 'fix', 'me'
		}

		writeReadTest(nsroot)
	}

	@CompileStatic
	void testWriteReadTypeSafe() {
		NSDOMBuilder b = NSDOMBuilder.newBuilder(ns1: NS1, ns2: NS2)
		Element nsroot = b('ns1:root') {
			b('ns2:item', [att1: 'test', att2: 'text']) {
				b('ns1:test', 'hello')
				b('nons-ense') { b 'text', 'lalal' }
			}
			b 'fix', 'me'
		}

		writeReadTest(nsroot)
	}

	private void writeReadTest(Node nsroot) {
		// configure transformer for serialization
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

		// serialize DOM
		StringWriter w = new StringWriter();
		w.withWriter {
			DOMSource source = new DOMSource(nsroot);
			StreamResult result = new StreamResult(it);
			transformer.transform(source, result);
		}

		println w.toString()

		// read DOM again
		StringReader reader = new StringReader(w.toString())
		reader.withReader {
			Element root = DOMBuilder.parse(it, false, true).documentElement;
			use (DOMCategory) {
				// ns1:root node
				assertEquals 'root', root.localName
				assertEquals NS1, root.namespaceURI

				// ns2:item
				assertEquals 1, root.getElementsByTagNameNS(NS2, 'item').size()
				Element item = root.getElementsByTagNameNS(NS2, 'item').item(0)
				assertEquals 'item', item.localName
				assertEquals NS2, item.namespaceURI

				// ns1:test
				Element test = item.getElementsByTagNameNS(NS1, 'test').item(0)
				assertEquals 'test', test.localName
				assertEquals NS1, test.namespaceURI

				// att1
				Node att1 = item.getAttributeNode('att1')
				assertEquals 'att1', att1.localName
				assertTrue att1.namespaceURI == null || att1.namespaceURI.empty

				// nons-ense
				Element nons = item.'nons-ense'[0]
				assertEquals 'nons-ense', nons.localName
				assertTrue nons.namespaceURI == null || nons.namespaceURI.empty

				// fix
				assertEquals 1, root.fix.size()
				Node fix = root.fix[0]
				assertEquals 'fix', fix.localName
				assertTrue fix.namespaceURI == null || fix.namespaceURI.empty
			}
		}
	}
}
