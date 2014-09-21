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
import eu.esdihumboldt.hale.common.core.io.JsonValueUtil
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.ValueList
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory
import groovy.transform.CompileStatic


/**
 * XML serialization for {@link ValueList}. Cannot represent <code>null</code> values.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ValueListType extends AbstractGroovyValueJson<ValueList, Object> implements ComplexValueType<ValueList, Object> {

	@Override
	ValueList fromDOM(Element fragment, Object context) {
		ValueList list = new ValueList()

		def entries = NSDOMCategory.children(fragment, HaleIO.NS_HALE_CORE, 'entry')
		for (Element entry in entries) {
			list << DOMValueUtil.fromTag(entry, context)
		}

		return list;
	}

	@Override
	Element toDOM(ValueList list) {
		def builder = NSDOMBuilder.newBuilder(core: HaleIO.NS_HALE_CORE)

		def fragment = builder('core:list') {
			for (Value value in list) {
				// ignore null values
				if (value != null) {
					DOMValueUtil.valueTag(builder, 'core:entry', value)
				}
			}
		}

		return fragment;
	}

	@Override
	public ValueList fromJson(Object json, Object context) {
		List<Value> values = []

		// expecting an array
		json.each { entry ->
			values.add(JsonValueUtil.fromJson(entry, context))
		}

		return new ValueList(values)
	}

	@Override
	public Object toJson(ValueList list) {
		list.collect { Value value ->
			JsonValueUtil.valueJson(value)
		}
	}

	@Override
	Class<Object> getContextType() {
		return Object.class;
	}
}
