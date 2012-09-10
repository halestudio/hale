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
 * Exception that is thrown when an I/O provider has not been configured
 * properly
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class IOProviderConfigurationException extends Exception {

	private static final long serialVersionUID = -2422941090415844659L;

	/**
	 * @see Exception#Exception()
	 */
	public IOProviderConfigurationException() {
		super();
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public IOProviderConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public IOProviderConfigurationException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public IOProviderConfigurationException(Throwable cause) {
		super(cause);
	}

}
