/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.config

import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.ValueList
import eu.esdihumboldt.hale.common.core.io.ValueProperties
import eu.esdihumboldt.util.config.Config

/**
 * Helper for converting a Config to and from a Value.
 * 
 * @author Simon Templer
 */
class ConfigValue {

	static Value fromConfig(Config config) {
		toValue(config.asMap())
	}

	static Config fromValue(Value value) {
		new Config(toMapList(value))
	}

	// private helpers

	private static Value toValue(Map map) {
		def converted = map.collectEntries { key, value ->
			[
				(key as String):
				toValue(value)
			]
		}

		new ValueProperties(converted).toValue()
	}

	private static Value toValue(List list) {
		def converted = list.collect { toValue(it) }

		new ValueList(converted).toValue()
	}

	private static Value toValue(Object value) {
		Value.of(value)
	}

	private static def toMapList(Value value) {
		if (value.complex) {
			ValueProperties props = value.as(ValueProperties)
			if (props != null) {
				return props.collectEntries { key, val ->
					[(key): toMapList(val)]
				}
			}

			ValueList list = value.as(ValueList)
			if (list != null) {
				return list.collect { toMapList(it) }
			}
		}

		// XXX does not support booleans, numbers or any other data types
		// XXX use different kind of representation?
		value.as(String)
	}

}
