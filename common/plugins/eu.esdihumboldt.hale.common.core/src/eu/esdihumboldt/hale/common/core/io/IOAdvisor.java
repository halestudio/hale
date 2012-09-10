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

package eu.esdihumboldt.hale.common.core.io;

/**
 * Advises in the configuration of an {@link IOProvider} in a certain context
 * (e.g. the UI services) and integrates the execution results into this
 * context.
 * 
 * @param <T> the I/O provider type supported
 * 
 * @author Simon Templer
 */
public interface IOAdvisor<T extends IOProvider> {

	/**
	 * Prepare the I/O provider when it is created. This may be executed even if
	 * for the provider no execution takes place.<br>
	 * <br>
	 * This for instance allows configuration pages on IOWizards to base on this
	 * preparation.
	 * 
	 * @param provider the I/O provider
	 */
	public void prepareProvider(T provider);

	/**
	 * Update the provider configuration directly before the execution.
	 * 
	 * @param provider the I/O provider
	 */
	public void updateConfiguration(T provider);

	/**
	 * Process the results after the execution.
	 * 
	 * @param provider the I/O provider
	 */
	public void handleResults(T provider);

}
