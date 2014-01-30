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

import groovy.transform.CompileStatic
import groovy.xml.DOMBuilder
import groovy.xml.FactorySupport
import groovy.xml.QName

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

import org.w3c.dom.Document

import com.google.common.collect.ImmutableMap


/**
 * DOMBuilder that accepts a map of prefixes and namespaces and creates
 * elements with a namespace according to the prefix specified in the
 * prefix map. The given prefixes don't have any influence on the prefixes
 * used in a corresponding document. 
 *  
 * @author Simon Templer
 */
@CompileStatic
class NSDOMBuilder extends DOMBuilder {

	/**
	 * Create a new DOM builder where element namespaces can be specified via
	 * certain prefixes. The given map is used to look up namespace for the
	 * prefixes.
	 * 
	 * @param prefixes prefixes mapped to namespace
	 * @return a new DOM builder
	 */
	public static NSDOMBuilder newInstance(Map<String, String> prefixes,
			boolean validating = false) throws ParserConfigurationException {
		DocumentBuilderFactory factory = FactorySupport.createDocumentBuilderFactory();
		factory.setNamespaceAware(true);
		factory.setValidating(validating);
		return new NSDOMBuilder(factory.newDocumentBuilder(), prefixes);
	}

	/**
	 * Prefixes mapped to namespaces.
	 */
	private final Map<String, String> prefixes

	/**
	 * Create a new DOM builder where element namespaces can be specified via
	 * certain prefixes. The given map is used to look up namespace for the
	 * prefixes.
	 *
	 * @param builder the document builder to use
	 * @param prefixes prefixes mapped to namespace
	 * @return a new DOM builder
	 */
	NSDOMBuilder(DocumentBuilder builder, Map<String, String> prefixes) {
		super(builder)
		this.prefixes = ImmutableMap.copyOf(prefixes)
	}

	/**
	 * Create a new DOM builder where element namespaces can be specified via
	 * certain prefixes. The given map is used to look up namespace for the
	 * prefixes.
	 *
	 * @param document the initial document
	 * @param prefixes prefixes mapped to namespace
	 * @return a new DOM builder
	 */
	NSDOMBuilder(Document document, Map<String, String> prefixes) {
		super(document)
		this.prefixes = ImmutableMap.copyOf(prefixes)
	}

	@Override
	protected Object getName(String methodName) {
		String[] parts = methodName.split(/:/, 2)

		if (parts.length == 2) {
			// lookup the prefix
			String prefix = parts[0]
			String ns = prefixes[prefix]

			if (ns) {
				new QName(ns, parts[1])
			}
			else {
				new QName(parts[1])
			}
		}
		else {
			super.getName(methodName)
		}
	}
}
