

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

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

import org.w3c.dom.Document
import org.w3c.dom.Element

import com.google.common.collect.ImmutableMap

import groovy.transform.CompileStatic
import groovy.xml.DOMBuilder
import groovy.xml.FactorySupport
import groovy.xml.QName


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
	public static NSDOMBuilder newBuilder(Map<String, String> prefixes,
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

	/**
	 * Convenience method for building DOM in a type safe way.
	 * @param name the name of the element to create
	 * @param attributes the attributes to attach to the element
	 * @param the element text content
	 * @return the created element
	 */
	Element call(String name, Map attributes = null, String text) {
		call(getName(name), attributes, text)
	}

	/**
	 * Convenience method for building DOM in a type safe way.
	 *
	 * @param name the name of the element to create
	 * @param closure the closure where potential child elements may be created
	 * @return the created element
	 */
	Element call(String name, Closure closure) {
		call(getName(name), null, closure)
	}

	/**
	 * Convenience method for building DOM in a type safe way.
	 *
	 * @param name the name of the element to create
	 * @param attributes the attributes to attach to the element
	 * @param closure the closure where potential child elements may be created
	 * @return the created element
	 */
	Element call(String name, Map attributes = null, Closure closure = null) {
		call(getName(name), attributes, closure)
	}

	/**
	 * Convenience method for building DOM in a type safe way.
	 *
	 * @param name the name of the element to create
	 * @param closure the closure where potential child elements may be created
	 * @return the created element
	 */
	Element call(QName name, Map attributes) {
		call(name, attributes, (Closure) null)
	}

	/**
	 * Convenience method for building DOM in a type safe way.
	 * @param name the name of the element to create
	 * @param attributes the attributes to attach to the element
	 * @param the element text content
	 * @return the created element
	 */
	Element call(QName name, Map attributes = null, String text) {
		Element node;
		if (attributes == null) {
			node = (Element) createNode(name, text)
		}
		else {
			node = (Element) createNode(name, attributes, text)
		}

		if (getCurrent() != null) {
			setParent(getCurrent(), node);
		}

		nodeCompleted(getCurrent(), node);
		return (Element) postNodeCompletion(getCurrent(), node);
	}

	/**
	 * Convenience method for building DOM in a type safe way.
	 * 
	 * @param name the name of the element to create
	 * @param attributes the attributes to attach to the element
	 * @param closure the closure where potential child elements may be created
	 * @return the created element
	 */
	Element call(QName name, Map attributes = null, Closure closure) {
		Element node;
		if (attributes == null || attributes.empty) {
			node = (Element) createNode(name)
		}
		else {
			node = (Element) createNode(name, attributes)
		}

		if (getCurrent() != null) {
			setParent(getCurrent(), node);
		}

		if (closure != null) {
			// push new node on stack
			Object oldCurrent = getCurrent();
			setCurrent(node);
			// let's register the builder as the delegate
			setClosureDelegate(closure, node);
			closure.call();
			setCurrent(oldCurrent);
		}

		nodeCompleted(getCurrent(), node);
		return (Element) postNodeCompletion(getCurrent(), node);
	}

	@Override
	protected QName getName(String methodName) {
		String[] parts = methodName.split(/:/, 2)

		if (parts.length == 2) {
			// lookup the prefix
			String prefix = parts[0]
			String ns = prefixes[prefix]

			if (ns) {
				new QName(ns, parts[1], prefix)
			}
			else {
				new QName(parts[1])
			}
		}
		else {
			return new QName(methodName)
		}
	}

	@Override
	protected Object createNode(Object name, Map attributes) {
		super.createNode(name, filterAttributes(attributes))
	}

	protected Map filterAttributes(Map attributes) {
		Map result = [:]

		attributes.each { key, value ->
			if (key != null && value != null) {
				/*
				 * A null value should not result in an empty attribute (which is an empty string).
				 * Instead, don't write the attribute.
				 */
				result[key] = value
			}
		}

		result
	}
}
