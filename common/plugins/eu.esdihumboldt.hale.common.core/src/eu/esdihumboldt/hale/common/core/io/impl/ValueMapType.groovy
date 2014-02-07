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
import eu.esdihumboldt.hale.common.core.io.ValueMap
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory
import groovy.transform.CompileStatic


/**
 * XML serialization for {@link ValueMap}.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ValueMapType implements ComplexValueType<ValueMap, Void> {

	@Override
	ValueMap fromDOM(Element fragment, Void context) {
		ValueMap map = new ValueMap()

		def entries = NSDOMCategory.children(fragment, HaleIO.NS_HALE_CORE, 'entry')
		for (Element entry in entries) {
			Value key = DOMValueUtil.fromTag(NSDOMCategory.firstChild(entry, HaleIO.NS_HALE_CORE, 'key'))
			Value value = DOMValueUtil.fromTag(NSDOMCategory.firstChild(entry, HaleIO.NS_HALE_CORE, 'value'))
			map.put(key, value)
		}

		return map;
	}

	@Override
	Element toDOM(ValueMap map) {
		def b = NSDOMBuilder.newBuilder(core: HaleIO.NS_HALE_CORE)

		def fragment = b 'core:map', {
			map.each { Value key, Value value ->
				// ignore null values
				if (value != null) {
					b 'core:entry',  {
						DOMValueUtil.valueTag(b, 'core:key', key)
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
