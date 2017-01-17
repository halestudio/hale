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

package eu.esdihumboldt.hale.io.validation.service;

import java.util.List;

import eu.esdihumboldt.hale.io.validation.ValidatorConfiguration;

/**
 * Service for managing validator configuration
 * 
 * @author Florian Esser
 */
public interface ValidatorConfigurationService {

	/**
	 * @return the validation configurations
	 */
	List<ValidatorConfiguration> getConfigurations();

	/**
	 * Adds a validator configuration
	 * 
	 * @param resourceId the resource identifier of the schema
	 * @param configuration configuration to add
	 */
	void addConfiguration(String resourceId, ValidatorConfiguration configuration);

	/**
	 * @param resourceId resource ID of the configuration
	 * @return true if the specified configuration was found and removed
	 */
	boolean removeConfiguration(String resourceId);

	/**
	 * @param resourceId resource ID of the configuration
	 * @return the configuration saved under the given resource ID or null
	 */
	ValidatorConfiguration getConfiguration(String resourceId);
}
