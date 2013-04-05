/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.defaults.config

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition


/**
 * Default value configuration for a specific attribute.
 * 
 * @author Simon Templer
 */
class AttributeConfig {

	private String attribute

	private ConfigEntry defConfig

	private def featureConfigs = [:]

	String getDefaultValue(PropertyEntityDefinition ped) {
		String typeName = ped.type.displayName

		ConfigEntry entry = featureConfigs[typeName]
		if (!entry) {
			entry = defConfig
		}

		if (entry) {
			entry.defaultValue
		}
		else {
			null
		}
	}

	void addEntry(ConfigEntry entry) {
		assert entry.attribute

		if (!attribute) {
			attribute = entry.attribute
		}
		else {
			assert attribute == entry.attribute
		}

		if (entry.featureType) {
			featureConfigs[entry.featureType] = entry
		}
		else {
			defConfig = entry
		}
	}
}
