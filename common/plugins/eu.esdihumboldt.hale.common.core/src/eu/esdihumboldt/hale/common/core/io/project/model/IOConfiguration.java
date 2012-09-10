/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
