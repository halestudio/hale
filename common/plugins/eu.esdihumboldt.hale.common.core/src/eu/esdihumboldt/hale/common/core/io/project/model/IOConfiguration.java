/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.project.model;

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.IOProvider;

/**
 * Object holding all information necessary to reproduce an {@link IOProvider}
 * execution, e.g. when loading a project.
 * 
 * @author Simon Templer
 */
public class IOConfiguration {

	private String actionId;

	private String providerId;

	private final Map<String, String> providerConfiguration = new HashMap<String, String>();

	/**
	 * @return the action ID
	 */
	public String getActionId() {
		return actionId;
	}

	/**
	 * @param actionId the action ID to set
	 */
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	/**
	 * @return the providerConfiguration
	 */
	public Map<String, String> getProviderConfiguration() {
		return providerConfiguration;
	}

}
