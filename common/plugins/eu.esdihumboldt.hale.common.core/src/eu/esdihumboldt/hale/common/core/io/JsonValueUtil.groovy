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

package eu.esdihumboldt.hale.common.core.io

import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueDefinition
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueExtension
import eu.esdihumboldt.hale.common.core.io.impl.ComplexValue
import eu.esdihumboldt.hale.common.core.io.impl.StringValue
import eu.esdihumboldt.hale.common.core.io.impl.ValuePropertiesType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper


/**
 * Utilities for storing {@link Value}s as JSON or extracting 
 * from JSON for nested value structures.
 * 
 * @author Simon Templer
 */
class JsonValueUtil {

	/**
	 * Create a value from a JSON object that is either represented as
	 * a JSON primitive or a child property (@value).
	 *
	 * @param json the JSON object / primitive / array (e.g. as retrieved through JsonSlurper)
	 * @return the extracted value
	 */
	static Value fromJson(def json, def context = null) {
		if (json == null) {
			return Value.NULL
		}
		if (json instanceof Map) {
			// object

			// determine object type from property
			def type = json.'@type'
			if (type) {
				ComplexValueDefinition cdv = ComplexValueExtension.instance.get(type)
				if (cdv && cdv.jsonConverter) {
					def value = cdv.jsonConverter.fromJson(json.'@value',
							cdv.getContextType().isInstance(context) ? context : null)
					new ComplexValue(value)
				}
				else {
					throw new IllegalStateException('Unable to extract value from Json object: no converter found for type ' + type)
				}
			}
			else {
				// object w/o @type information
				// create ValueProperties
				new ValuePropertiesType().fromJson(new JsonBuilder(json).toString(), null).toValue()
			}
		}
		else if (json instanceof List) {
			// array
			// represent as ValueList
			new ValueList(json.collect { fromJson(it) }).toValue()
		}
		else {
			// primitive
			new StringValue(json as String)
		}
	}

	/**
	 * Create a value JSON representation.
	 *
	 * @param value the contained value
	 * @return the created JSON object / primitive / array (e.g. for use with JsonBuilder or JsonOutput)
	 */
	static def objectJson(Object value) {
		valueJson(value as Value)
	}

	/**
	 * Create a value JSON representation.
	 *
	 * @param value the contained value
	 * @return the created JSON object / primitive / array (e.g. for use with JsonBuilder or JsonOutput)
	 * @throws IllegalStateException if no JSON representation could be created
	 */
	static def valueJson(Value value) {
		if (value == null) {
			return null
		}

		if (value.isRepresentedAsDOM()) {
			def intern = value.value
			if (intern == null) {
				return null
			}

			// retrieve complex value definition based on object type
			ComplexValueDefinition cdv = ComplexValueExtension.instance.getDefinition(intern.getClass())
			if (cdv && cdv.jsonConverter) {
				def valueJs = cdv.jsonConverter.toJson(intern)

				if (intern instanceof ValueList) {
					// no wrapper needed - represented as Json array
					valueJs
				}
				else {
					// create wrapper
					def builder = new JsonBuilder()
					builder {
						'@type' cdv.id
						'@value' valueJs
					}
				}
			}
			else {
				throw new IllegalStateException('Unable to create Json representation for value: ' + value)
			}
		}
		else {
			value.stringRepresentation
		}
	}

	private static def parseValue(String json) {
		if (json.trim().startsWith('{') || json.trim().startsWith('[')) {
			// object or array
			new JsonSlurper().parseText(json)
		}
		else {
			// primitive

			//XXX the following line only works with Groovy 2.3!
			//		new JsonSlurper().parse(json)

			new JsonSlurper().parseText('{"v":' + json + '}').v
		}
	}

}
