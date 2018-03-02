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

package eu.esdihumboldt.hale.common.core.io.impl;

import eu.esdihumboldt.hale.common.core.io.CachingImportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;

/**
 * I/O advisor based on configuration in an existing {@link IOConfiguration}.
 * Subclasses should at least override {@link #handleResults(IOProvider)}.
 * 
 * @param <T> the I/O provider type
 * 
 * @author Simon Templer
 */
public class ConfigurationIOAdvisor<T extends IOProvider> extends AbstractIOAdvisor<T> {

	private IOConfiguration conf;

	/**
	 * Create an I/O advisor based on I/O configurations that have to be set
	 * using {@link #setConfiguration(IOConfiguration)} before using the
	 * advisor.
	 */
	public ConfigurationIOAdvisor() {
		super();
	}

	/**
	 * Create an I/O advisor based on the given I/O configuration.
	 * 
	 * @param conf the I/O configuration
	 */
	public ConfigurationIOAdvisor(IOConfiguration conf) {
		super();
		this.conf = conf;
	}

	/**
	 * Set the I/O configuration to use for provider configuration.
	 * 
	 * @param conf the I/O configuration
	 */
	public void setConfiguration(IOConfiguration conf) {
		this.conf = conf;
	}

	/**
	 * @see AbstractIOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(T provider) {
		super.prepareProvider(provider);

		if (conf != null) {
			// load the configuration from the IOConfiguration object
			provider.loadConfiguration(conf.getProviderConfiguration());
			if (provider instanceof CachingImportProvider) {
				((CachingImportProvider) provider).setCache(conf.getCache());
			}
			setActionId(conf.getActionId());
		}
	}

}
