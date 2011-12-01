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

package eu.esdihumboldt.hale.server.war;

/**
 * Fatal exception during the execution of a WPS request 
 * @author Simon Templer
 */
public class WpsException extends Exception {
	
	private static final long serialVersionUID = -7488130903557942216L;

	/**
	 * WPS error codes
	 */
	public enum WpsErrorCode {
		/**
		 * Exception code for GetCapabilities that states that the version negotiation failed
		 */
		VersionNegotiationFailed,
		/**
		 * Exception code for GetCapabilities/DescribeProcess that states that a parameter is missing
		 */
		MissingParameterValue,
		/**
		 * Exception code for GetCapabilities/DescribeProcess that states that a parameter value is invalid
		 */
		InvalidParameterValue,
		/**
		 * Exception code for cases where no other code applies
		 */
		NoApplicableCode
	}

	private final String locator;
	
	private final WpsErrorCode code;

	/**
	 * Create a new WPS execution exception
	 * @param message the error message
	 * @param code the error code
	 * @param cause the exception cause, may be <code>null</code>
	 * @param locator the error locator, may be <code>null</code>
	 */
	public WpsException(String message, WpsErrorCode code,
			Throwable cause, String locator) {
		super(message, cause);
		this.locator = locator;
		this.code = code;
	}

	/**
	 * @return the locator
	 */
	public String getLocator() {
		return locator;
	}

	/**
	 * @return the code
	 */
	public WpsErrorCode getCode() {
		return code;
	}

}
