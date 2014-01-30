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
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.ValueMap
import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory


/**
 * XML serialization for {@link ValueMap}.
 * 
 * @author Simon Templer
 */
class ValueMapType implements ComplexValueType<ValueMap, Void> {

	@Override
	ValueMap fromDOM(Element fragment, Void context) {
		ValueMap map = new ValueMap()

		use (DOMCategory) {
			for (entry in fragment.entry) {
				Value key = ValueMapType.fromTag(entry.key[0])
				Value value = ValueMapType.fromTag(entry.value[0])
				map.put(key, value)
			}
		}

		return map;
	}

	@Override
	Element toDOM(ValueMap map) {
		def builder = DOMBuilder.newInstance(false, true)

		def fragment = builder.map {
			map.each { String key, Value value ->
				// ignore null values
				if (value != null) {
					entry {
						ValueListType.valueTag(builder, 'key', value)
						ValueListType.valueTag(builder, 'value', value)
					}
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
