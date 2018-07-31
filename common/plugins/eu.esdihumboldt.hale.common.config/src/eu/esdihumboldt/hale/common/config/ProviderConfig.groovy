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

import eu.esdihumboldt.hale.common.core.io.IOProvider
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration
import eu.esdihumboldt.util.config.Config

/**
 * Helper for provider configuration via a generic configuration object.
 * 
 * @author Simon Templer
 */
class ProviderConfig {

	static final PARAM_GENERIC_CONFIG = 'genericConfig'

	static Config get(IOProvider provider, String configName = PARAM_GENERIC_CONFIG) {
		Value val = provider.getParameter(configName)
		if (val != null && !val.isEmpty()) {
			ConfigValue.fromValue(val)
		}
		else {
			new Config()
		}
	}

	static void set(Config config, IOProvider provider, String configName = PARAM_GENERIC_CONFIG) {
		provider.setParameter(configName, ConfigValue.fromConfig(config))
	}

	public static Config get(IOConfiguration conf, String configName = PARAM_GENERIC_CONFIG) {
		Value val = conf.providerConfiguration?.get(configName)
		if (val != null && !val.isEmpty()) {
			ConfigValue.fromValue(val)
		}
		else {
			new Config()
		}
	}
}
