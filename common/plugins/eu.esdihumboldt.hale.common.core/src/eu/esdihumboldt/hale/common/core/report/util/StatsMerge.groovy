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

package eu.esdihumboldt.hale.common.core.report.util


/**
 * Helper for merging statistics (in map/list representation).
 * 
 * @author Simon Templer
 */
class StatsMerge {

	/**
	 * Merge statistic maps together.
	 *
	 * @param configs the configurations
	 * @return the merged configuration
	 */
	static Map mergeConfigs(@SuppressWarnings("rawtypes") Map... configs) {
		(configs as List).inject([:], StatsMerge.&combineMap)
	}

	private static Map combineMap(Map a, Map b) {
		Map target = [:]
		if (a.is(b)) {
			target = a
		}
		else {
			target.putAll(a)
			b.each { key, value ->
				if (value != null) {
					target.merge(key, value, StatsMerge.&combineValue.curry(key))
				}
			}
		}
		Map result = new HashMap(target)
		target.each { key, value ->
			// make compatible to MongoDB
			String newKey = key
			if (newKey.contains('.')) {
				newKey = newKey.replaceAll(/\./, '_')
			}
			// map keys may not have been processed
			if (value instanceof Map) {
				value = combineMap([:], value)
			}
			result.remove(key)
			result.merge(newKey, value, StatsMerge.&combineValue.curry(newKey))
		}
		result
	}

	private static Object combineValue(String key, Object a, Object b) {
		if (a == null && b == null) {
			null
		}
		else if (a instanceof Map) {
			if (b instanceof Map) {
				combineMap(a, b)
			}
			else {
				//XXX error?
				combineMap([], a)
			}
		}
		else if (b instanceof Map) {
			//XXX error?
			combineMap([], b)
		}
		else if (a instanceof List && b instanceof List) {
			if (a.size() == 1 && b.size() == 1) {
				combineSingleValue(key, a[0], b[0])
			}
			else {
				def combined = []
				combined.addAll(a)
				combined.addAll(b)
				combined
			}
		}
		else if (a instanceof List && !(b instanceof List)) {
			if (a.size() == 1) {
				combineSingleValue(key, a[0], b)
			}
			else {
				def combined = []
				combined.addAll(a)
				combined.add(b)
				combined
			}
		}
		else if (!(a instanceof List) && b instanceof List) {
			if (b.size() == 1) {
				combineSingleValue(key, a, b[0])
			}
			else {
				def combined = []
				combined.add(a)
				combined.addAll(b)
				combined

			}
		}
		else {
			combineSingleValue(key, a, b)
		}
	}

	private static Object combineSingleValue(String key, Object a, Object b) {
		if (a instanceof Number && b instanceof Number) {
			// add numbers
			((Number) a).longValue() + ((Number) b).longValue()
		}
		else if (a instanceof Boolean && b instanceof Boolean) {
			if (key == 'completed') {
				a && b
			}
			else {
				// by default and booleans
				a && b
			}
		}
		else {
			if (a == null) {
				b
			}
			else if (b == null) {
				a
			}
			else {
				// by default combine list
				def combined = [a, b]
				combined
			}
		}
	}

}

