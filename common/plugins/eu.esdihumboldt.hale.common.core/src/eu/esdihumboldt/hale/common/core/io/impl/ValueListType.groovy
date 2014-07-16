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

import eu.esdihumboldt.hale.common.core.io.ComplexValueJson
import eu.esdihumboldt.hale.common.core.io.ComplexValueType
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.JsonValueUtil
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.ValueList
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory
import groovy.json.JsonSlurper
import groovy.json.StreamingJsonBuilder
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode


/**
 * XML serialization for {@link ValueList}. Cannot represent <code>null</code> values.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ValueListType implements ComplexValueType<ValueList, Void>, ComplexValueJson<ValueList, Void> {

	@Override
	ValueList fromDOM(Element fragment, Void context) {
		ValueList list = new ValueList()

		def entries = NSDOMCategory.children(fragment, HaleIO.NS_HALE_CORE, 'entry')
		for (Element entry in entries) {
			list << DOMValueUtil.fromTag(entry)
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

	@CompileStatic(TypeCheckingMode.SKIP)
	@Override
	public ValueList fromJson(Reader json, Void context) {
		List<Value> values = []

		def js = new JsonSlurper().parse(json) // expecting an array
		js.each { entry ->
			values.add(JsonValueUtil.fromJson(entry))
		}

		return new ValueList(values)
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	@Override
	public void toJson(ValueList list, Writer writer) {
		def json = new StreamingJsonBuilder(writer)
		json(list.collect { value ->
			JsonValueUtil.valueJson(value)
		})
	}

	@Override
	Class<Void> getContextType() {
		return Void.class;
	}
}
