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

package eu.esdihumboldt.hale.common.lookup.internal

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.core.io.ComplexValueType
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.impl.ElementValue
import eu.esdihumboldt.hale.common.lookup.LookupTable
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableImpl
import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory


/**
 * Lookup table complex value type representation.
 * 
 * @author Simon Templer
 */
class LookupTableType implements ComplexValueType<LookupTable, Void> {

	private static Value fromTag(def element) {
		if (element.'@value') {
			// string representation
			return Value.of(element.'@value')
		}
		else {
			// DOM representation
			return new ElementValue(element.'*'[0]);
		}
	}

	@Override
	public LookupTable fromDOM(Element fragment, Void context) {
		Map<Value, Value> values = new HashMap<Value, Value>();

		use (DOMCategory) {
			for (entry in fragment.entry) {
				Value key = fromTag(entry.key[0]);
				Value value = fromTag(entry.value[0])
				values.put(key, value)
			}
		}

		return new LookupTableImpl(values);
	}

	@Override
	public Element toDOM(LookupTable table) {
		def builder = DOMBuilder.newInstance(false, true)

		def valueTag = { String tagName, Value value ->
			if (value.isRepresentedAsDOM()) {
				def element = builder."$tagName"()
				def child = value.getDOMRepresentation();
				// add value representation as child
				element.appendChild(element.ownerDocument.adoptNode(child))
			}
			else {
				builder."$tagName"(value: value.stringRepresentation)
			}
		}

		def fragment = builder.'lookup-table' {
			for (Value key in table.keys) {
				entry {
					valueTag('key', key)
					valueTag('value', table.lookup(key))
				}
			}
		}

		return fragment;
	}

	@Override
	public Class<Void> getContextType() {
		Void.class
	}
}
