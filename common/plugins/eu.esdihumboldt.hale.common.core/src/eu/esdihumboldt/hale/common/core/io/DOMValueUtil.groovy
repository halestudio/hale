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

package eu.esdihumboldt.hale.common.core.io

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.core.io.impl.ElementValue
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory


/**
 * Utilities for storing {@link Value}s in a DOM {@link Element} or extracting 
 * from a DOM element.
 * 
 * @author Simon Templer
 */
class DOMValueUtil {

	/**
	 * Create a value from an XML tag that is either represented as
	 * a simple attribute (named <i>value</i>) or a child element.
	 *
	 * @param element the tag
	 * @return
	 */
	static Value fromTag(Element element) {
		if (element.hasAttribute('value')) {
			// string representation
			// may be an empty string
			return Value.of(element.getAttribute('value'))
		}
		else {
			// DOM representation
			return new ElementValue(NSDOMCategory.firstChild(element), null)
		}
	}

	/**
	 * Create a value tag on the given DOM builder.
	 *
	 * @param builder the DOM builder
	 * @param tagName the name of the tag
	 * @param value the contained value
	 * @return the created element with the given tag name and value content
	 */
	static Element objectTag(NSDOMBuilder builder, String tagName, Object value) {
		valueTag(builder, tagName, value as Value)
	}

	/**
	 * Create a value tag on the given DOM builder.
	 *
	 * @param builder the DOM builder
	 * @param tagName the name of the tag
	 * @param value the contained value
	 * @return the created element with the given tag name and value content
	 */
	static Element valueTag(NSDOMBuilder builder, String tagName, Value value) {
		if (value.isRepresentedAsDOM()) {
			Element element = builder(tagName)
			def child = value.getDOMRepresentation();
			// add value representation as child
			Element adopted = (Element) element.ownerDocument.adoptNode(child)

			if (adopted == null) {
				// adoption failed (e.g. different DOM implementation), use importNode instead
				adopted = (Element) element.ownerDocument.importNode(child, true)
			}

			element.appendChild(adopted)

			element
		}
		else {
			builder(tagName, [value: value.stringRepresentation])
		}
	}

}
