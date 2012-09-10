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

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;

/**
 * Abstract {@link IOAdvisor} base implementation
 * 
 * @param <T> the I/O provider type
 * 
 * @author Simon Templer
 */
public abstract class AbstractIOAdvisor<T extends IOProvider> implements IOAdvisor<T> {

	/**
	 * @see IOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(T provider) {
		// override me
	}

	/**
	 * @see IOAdvisor#updateConfiguration(IOProvider)
	 */
	@Override
	public void updateConfiguration(T provider) {
		// override me
	}

	/**
	 * @see IOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(T provider) {
		// override me
	}

}
