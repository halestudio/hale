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
 * Helper for merging configurations.
 * 
 * @author Simon Templer
 */
class ConfigMerger {

	/**
	 * Merge configurations together.
	 * Configurations in subsequent maps may override configuration from the previous maps.
	 *
	 * @param configs the configurations
	 * @return the merged configuration
	 */
	static Config mergeConfigs(Config... configs) {
		new ConfigMerger().doMergeConfigs(configs.toList())
	}

	/**
	 * Merge configurations together.
	 * Configurations in subsequent maps may override configuration from the previous maps.
	 *
	 * @param configs the configurations
	 * @return the merged configuration
	 */
	static Config mergeConfigs(Iterable<Config> configs) {
		new ConfigMerger().doMergeConfigs(configs)
	}

	/**
	 * Merge configurations together.
	 * Configurations in subsequent maps may override configuration from the previous maps.
	 *
	 * @param configs the configurations
	 * @return the merged configuration
	 */
	Config doMergeConfigs(Iterable<Config> configs) {
		new Config(configs.asCollection().collect{ it.asMap() }.inject([:], this.&combineMap))
	}

	protected Map combineMap(Map a, Map b) {
		if (a.is(b)) {
			return a
		}

		Map result = [:]
		result.putAll(a)
		b.each { key, value ->
			if (value != null) {
				result.merge(key, value, this.&combineValue)
			}
		}
		result
	}

	protected Object combineValue(Object a, Object b) {
		if (a instanceof Map) {
			if (b instanceof Map) {
				combineMap(a, b)
			}
			else {
				//XXX error?
				a
			}
		}
		else if (b instanceof Map) {
			//XXX error?
			b
		}
		else if (a instanceof List && b instanceof List) {
			def combined = []
			combined.addAll(a)
			combined.addAll(b)
			combined
		}
		else if (a instanceof List && !(b instanceof List)) {
			def combined = []
			combined.addAll(a)
			combined.add(b)
			combined
		}
		else if (!(a instanceof List) && b instanceof List) {
			def combined = []
			combined.add(a)
			combined.addAll(b)
			combined
		}
		else {
			// b overrides a
			//XXX message?
			b
		}
	}
}
