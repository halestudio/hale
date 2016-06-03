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

package eu.esdihumboldt.util.groovy.xml;

import groovy.transform.CompileStatic
import groovy.xml.dom.DOMCategory

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

/**
 * Helper methods for accessing the DOM.
 * 
 * @author Simon Templer
 */
@CompileStatic
public class NSDOMCategory extends DOMCategory {

	/**
	 * Get the attribute with the given name. If the attribute is not present, yield <code>null</code>.
	 * @param element the element which is inspected for the attribute
	 * @param name the attribute name
	 * @return the attribute value or <code>null</code>
	 */
	public static String getAttributeOrNull(Element element, String name) {
		Node attribute = element.getAttributes().getNamedItem(name)
		if (attribute == null) {
			return null
		}
		else {
			return attribute.nodeValue
		}
	}

	/**
	 * Get the first child element.
	 * @param parent the parent element
	 * @return the first child element or <code>null</code> if none exists
	 */
	public static Element firstChild(Element parent) {
		firstChild(parent, null, null)
	}

	/**
	 * Get the first child element with the given namespace and local name.
	 * 
	 * @param parent the parent element
	 * @param ns the namespace, may be <code>null</code> to ignore namespace
	 * @param localName the local name, may be <code>null</code> to find a child with any local name
	 * @return the child element or <code>null</code> if none exists
	 */
	public static Element firstChild(Element parent, String ns = null, String localName) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element && (localName == null || node.getLocalName().equals(localName))
			&& (ns == null || ns.equals(node.getNamespaceURI()))) {
				return (Element) node;
			}
		}

		return null;
	}

	/**
	 * Get all child elements with the given namespace and local name.
	 * 
	 * @param parent the parent element
	 * @param ns the namespace, may be <code>null</code> to ignore the namespace
	 * @param localName the local name, may not be <code>null</code>
	 * @return the child element or <code>null</code> if none exists
	 */
	public static List<Element> children(Element parent, String ns = null, String localName) {
		List<Element> result = new ArrayList<>();
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element && node.getLocalName().equals(localName)
			&& (ns == null || ns.equals(node.getNamespaceURI()))) {
				result.add((Element) node);
			}
		}
		return result;
	}
}
