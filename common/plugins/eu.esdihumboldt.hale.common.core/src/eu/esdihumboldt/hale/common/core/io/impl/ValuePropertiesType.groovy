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
import eu.esdihumboldt.hale.common.core.io.ValueProperties
import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory


/**
 * XML serialization for {@link ValueProperties}.
 * 
 * @author Simon Templer
 */
class ValuePropertiesType implements ComplexValueType<ValueProperties, Void> {

	@Override
	ValueProperties fromDOM(Element fragment, Void context) {
		ValueProperties properties = new ValueProperties()

		use (DOMCategory) {
			for (entry in fragment.property) {
				String key = entry.'@name'
				Value value = ValuePropertiesType.fromTag(entry.value[0])
				properties[key] = value
			}
		}

		return properties;
	}

	@Override
	Element toDOM(ValueProperties properties) {
		def builder = DOMBuilder.newInstance(false, true)

		def fragment = builder.properties {
			properties.each { String key, Value value ->
				// ignore null values
				if (value != null) {
					property(name: key) {
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
