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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.CachingImportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueExtension;

/**
 * Object holding all information necessary to reproduce an {@link IOProvider}
 * execution, e.g. when loading a project.
 * 
 * @author Simon Templer
 */
public class IOConfiguration implements Serializable, Cloneable {

	private static final long serialVersionUID = 8248743659873733496L;

	private String actionId;

	private String providerId;

	private String name;

	/**
	 * Value that optionally stores cached information or the cached resource
	 * and may be updated when the {@link IOConfiguration} is executed.
	 * 
	 * @see CachingImportProvider
	 */
	private Value cache;

	private final Map<String, Value> providerConfiguration = new HashMap<String, Value>();

	/**
	 * Default constructor
	 */
	public IOConfiguration() {
		super();
	}

	/**
	 * Creates an independent copy of the {@link IOConfiguration}.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IOConfiguration clone() {
		IOConfiguration copy = new IOConfiguration();

		copy.setActionId(getActionId());
		copy.setProviderId(getProviderId());
		copy.setName(getName());

		copy.getProviderConfiguration().putAll(getProviderConfiguration());

		return copy;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

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
	 * Get the I/O provider configuration.
	 * 
	 * @return the provider configuration, values are either strings, DOM
	 *         elements or complex value types defined in the
	 *         {@link ComplexValueExtension}
	 */
	public Map<String, Value> getProviderConfiguration() {
		return providerConfiguration;
	}

	/**
	 * @return the cached value
	 */
	public Value getCache() {
		if (cache == null)
			return Value.NULL;
		return cache;
	}

	/**
	 * Sets the cached value.
	 * 
	 * @param cache the cached value
	 */
	public void setCache(Value cache) {
		this.cache = cache;
	}

}
