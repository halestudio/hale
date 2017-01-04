/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.io.validation.service.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import eu.esdihumboldt.hale.io.validation.ValidatorConfiguration;
import eu.esdihumboldt.hale.io.validation.service.ValidatorConfigurationService;

/**
 * Validator configuration service
 * 
 * @author Florian Esser
 */
public class ValidatorConfigurationServiceImpl implements ValidatorConfigurationService {

	/**
	 * Maps resource identifiers to validation rules
	 */
	private final Map<String, ValidatorConfiguration> configurations = new HashMap<>();

	/**
	 * @see eu.esdihumboldt.hale.io.validation.service.ValidatorConfigurationService#getConfigurations()
	 */
	@Override
	public List<ValidatorConfiguration> getConfigurations() {
		return Lists.newArrayList(configurations.values());
	}

	/**
	 * @see eu.esdihumboldt.hale.io.validation.service.ValidatorConfigurationService#addConfiguration(java.lang.String,
	 *      eu.esdihumboldt.hale.io.validation.ValidatorConfiguration)
	 */
	@Override
	public void addConfiguration(String resourceId, ValidatorConfiguration schema) {
		configurations.put(resourceId, schema);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.validation.service.ValidatorConfigurationService#removeConfiguration(java.lang.String)
	 */
	@Override
	public boolean removeConfiguration(String resourceId) {
		if (configurations.containsKey(resourceId)) {
			configurations.remove(resourceId);
			return true;
		}

		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.validation.service.ValidatorConfigurationService#getConfiguration(java.lang.String)
	 */
	@Override
	public ValidatorConfiguration getConfiguration(String resourceId) {
		return configurations.get(resourceId);
	}

}
