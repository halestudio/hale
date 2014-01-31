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

package eu.esdihumboldt.hale.common.core.io.impl

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.core.io.ComplexValueType
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.ValueList
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory


/**
 * XML serialization for {@link ValueList}. Cannot represent <code>null</code> values.
 * 
 * @author Simon Templer
 */
class ValueListType implements ComplexValueType<ValueList, Void> {

	/**
	 * Create a value from an XML tag that is either represented as
	 * a simple attribute (named <i>value</i>) or a child element.
	 * 
	 * @param element the tag
	 * @return
	 */
	static Value fromTag(def element) {
		if (element.'@value') {
			// string representation
			return Value.of(element.'@value')
		}
		else {
			// DOM representation
			return new ElementValue(element.'*'[0], null)
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
	static Element objectTag(DOMBuilder builder, String tagName, Object value) {
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
	static Element valueTag(DOMBuilder builder, String tagName, Value value) {
		if (value.isRepresentedAsDOM()) {
			def element = builder."$tagName"()
			def child = value.getDOMRepresentation();
			// add value representation as child
			element.appendChild(element.ownerDocument.adoptNode(child))

			element
		}
		else {
			builder."$tagName"(value: value.stringRepresentation)
		}
	}

	@Override
	ValueList fromDOM(Element fragment, Void context) {
		ValueList list = new ValueList()

		use (DOMCategory) {
			for (entry in fragment.entry) {
				list << ValueListType.fromTag(entry)
			}
		}

		return list;
	}

	@Override
	Element toDOM(ValueList list) {
		def builder = NSDOMBuilder.newInstance(core: HaleIO.NS_HALE_CORE)

		def fragment = builder.'core:list' {
			for (Value value in list) {
				// ignore null values
				if (value != null) {
					valueTag(builder, 'entry', value)
				}
			}
		}

		return fragment;
	}

	@Override
	Class<Void> getContextType() {
		return Void.class;
	}
}
