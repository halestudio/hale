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

package eu.esdihumboldt.hale.common.core.io.impl;

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
		}
	}

}
