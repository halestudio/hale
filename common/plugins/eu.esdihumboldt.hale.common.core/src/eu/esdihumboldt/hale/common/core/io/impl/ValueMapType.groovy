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
import eu.esdihumboldt.hale.common.core.io.ValueMap
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode


/**
 * XML serialization for {@link ValueMap}.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ValueMapType extends AbstractGroovyValueJson<ValueMap, Object> implements ComplexValueType<ValueMap, Object> {

	@Override
	ValueMap fromDOM(Element fragment, Object context) {
		ValueMap map = new ValueMap()

		def entries = NSDOMCategory.children(fragment, HaleIO.NS_HALE_CORE, 'entry')
		for (Element entry in entries) {
			Value key = DOMValueUtil.fromTag(NSDOMCategory.firstChild(entry, HaleIO.NS_HALE_CORE, 'key'), context)
			Value value = DOMValueUtil.fromTag(NSDOMCategory.firstChild(entry, HaleIO.NS_HALE_CORE, 'value'), context)
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

	@CompileStatic(TypeCheckingMode.SKIP)
	@Override
	public void toJson(ValueMap map, Writer writer) {
		def json = new JsonStreamBuilder(writer)
		json {
			map.each { Value key, Value value ->
				// ignore null values
				if (value != null) {
					'entries[]' {
						json.key JsonValueUtil.valueJson(key)
						json.value JsonValueUtil.valueJson(value)
					}
				}
			}
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	@Override
	public ValueMap fromJson(Object json, Object context) {
		ValueMap values = new ValueMap()

		json.entries.each { entry ->
			Value key = JsonValueUtil.fromJson(entry.key, context)
			Value value = JsonValueUtil.fromJson(entry.value, context)
			values.put(key, value)
		}

		return values
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	@Override
	public Object toJson(ValueMap map) {
		def entries = []

		map.each { Value key, Value value ->
			// ignore null values
			if (value != null) {
				entries << [
					key: JsonValueUtil.valueJson(key),
					value: JsonValueUtil.valueJson(value)
				]
			}
		}

		[entries: entries]
	}

	@Override
	Class<Object> getContextType() {
		return Object.class;
	}
}
