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

package eu.esdihumboldt.util.config


/**
 * Generic configuration object backed by a structure of Maps and Lists.
 * 
 * @author Simon Templer
 */
class Config {

	private final Map<String, Object> config

	public Config(Map<String, Object> config = [:]) {
		this.config = config
	}

	Map<String, Object> asMap() {
		config
	}

	Object getAt(String key) {
		if (!key) {
			throw new IllegalArgumentException('Key may not be empty')
		}

		def parts = new LinkedList(key.split(/\./).toList())
		def value = config
		while (value != null && !parts.empty) {
			if (value instanceof Map) {
				value = value[parts.poll()]
			}
			else {
				// key not available
				value = null
			}
		}

		value
	}

	void putAt(String key, Object value) {
		if (!key) {
			throw new IllegalArgumentException('Key may not be empty')
		}
		def parts = new LinkedList(key.split(/\./).toList())

		def map = config
		while (parts.size() > 1) {
			String name = parts.poll()
			if (!name) {
				throw new IllegalArgumentException('Key parts may not be empty')
			}

			def current = map[name]
			if (current == null) {
				// create and store a new map
				def child = [:]
				map[name] = child
				map = child
			}
			else {
				// already something present
				if (current instanceof Map) {
					// we can descend further
					map = current
				}
				else {
					throw new IllegalStateException("An existing value conflicts with setting a value for key $key")
				}
			}
		}

		if (map != null) {
			String name = parts.poll()
			if (!name) {
				throw new IllegalArgumentException('Key parts may not be empty')
			}
			map[name] = value
		}
	}
}
