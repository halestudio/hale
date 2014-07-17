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
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil
import eu.esdihumboldt.hale.common.core.io.JsonValueUtil
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.impl.AbstractGroovyValueJson
import eu.esdihumboldt.hale.common.lookup.LookupTable
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableImpl
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import groovy.xml.dom.DOMCategory


/**
 * Lookup table complex value type representation.
 * 
 * @author Simon Templer
 */
class LookupTableType extends AbstractGroovyValueJson<LookupTable, Object> implements ComplexValueType<LookupTable, Object> {

	@Override
	public LookupTable fromDOM(Element fragment, Object context) {
		Map<Value, Value> values = new HashMap<Value, Value>();

		use (DOMCategory) {
			for (entry in fragment.entry) {
				Value key = DOMValueUtil.fromTag(entry.key[0], context)
				Value value = DOMValueUtil.fromTag(entry.value[0], context)
				values.put(key, value)
			}
		}

		return new LookupTableImpl(values);
	}

	@Override
	public Element toDOM(LookupTable table) {
		def builder = NSDOMBuilder.newBuilder([:])

		def fragment = builder.'lookup-table' {
			for (Value key in table.keys) {
				// ignore null values
				if (table.lookup(key) != null) {
					entry {
						DOMValueUtil.valueTag(builder, 'key', key)
						DOMValueUtil.valueTag(builder, 'value', table.lookup(key))
					}
				}
			}
		}

		return fragment;
	}

	@Override
	public void toJson(LookupTable table, Writer writer) {
		def json = new JsonStreamBuilder(writer)
		json {
			for (Value key in table.keys) {
				// ignore null values
				if (table.lookup(key) != null) {
					'entries[]' {
						json.key JsonValueUtil.valueJson(key)
						json.value JsonValueUtil.valueJson(table.lookup(key))
					}
				}
			}
		}
	}

	@Override
	public LookupTable fromJson(Object json, Object context) {
		Map<Value, Value> values = new HashMap<Value, Value>()

		json.entries.each { entry ->
			Value key = JsonValueUtil.fromJson(entry.key, context)
			Value value = JsonValueUtil.fromJson(entry.value, context)
			values.put(key, value)
		}

		return new LookupTableImpl(values)
	}

	@Override
	public Object toJson(LookupTable table) {
		def entries = []

		for (Value key in table.keys) {
			// ignore null values
			if (table.lookup(key) != null) {
				entries << [
					key: JsonValueUtil.valueJson(key),
					value: JsonValueUtil.valueJson(table.lookup(key))
				]
			}
		}

		[entries: entries]
	}

	@Override
	public Class<Object> getContextType() {
		Object.class
	}
}
