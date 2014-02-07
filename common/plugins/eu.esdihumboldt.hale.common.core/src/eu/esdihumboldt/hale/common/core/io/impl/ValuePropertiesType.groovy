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
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.ValueProperties
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory
import groovy.transform.CompileStatic


/**
 * XML serialization for {@link ValueProperties}.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ValuePropertiesType implements ComplexValueType<ValueProperties, Void> {

	@Override
	ValueProperties fromDOM(Element fragment, Void context) {
		ValueProperties properties = new ValueProperties()

		def entries = NSDOMCategory.children(fragment, HaleIO.NS_HALE_CORE, 'property')
		for (Element entry in entries) {
			String key = entry.getAttribute('name')
			Value value = DOMValueUtil.fromTag(NSDOMCategory.firstChild(entry, HaleIO.NS_HALE_CORE, 'value'))
			properties[key] = value
		}

		return properties;
	}

	@Override
	Element toDOM(ValueProperties properties) {
		def b = NSDOMBuilder.newBuilder(core: HaleIO.NS_HALE_CORE)

		def fragment = b 'core:properties', {
			properties.each { String key, Value value ->
				// ignore null values
				if (value != null) {
					b 'core:property', [name: key], {
						DOMValueUtil.valueTag(b, 'core:value', value)
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
