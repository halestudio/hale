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
import eu.esdihumboldt.hale.common.core.io.impl.ValueListType
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

	@Override
	public LookupTable fromDOM(Element fragment, Void context) {
		Map<Value, Value> values = new HashMap<Value, Value>();

		use (DOMCategory) {
			for (entry in fragment.entry) {
				Value key = ValueListType.fromTag(entry.key[0]);
				Value value = ValueListType.fromTag(entry.value[0])
				values.put(key, value)
			}
		}

		return new LookupTableImpl(values);
	}

	@Override
	public Element toDOM(LookupTable table) {
		def builder = DOMBuilder.newInstance(false, true)

		def fragment = builder.'lookup-table' {
			for (Value key in table.keys) {
				// ignore null values
				if (table.lookup(key) != null) {
					entry {
						ValueListType.valueTag(builder, 'key', key)
						ValueListType.valueTag(builder, 'value', table.lookup(key))
					}
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
