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
 * WPS related constants
 * @author Simon Templer
 */
public interface WpsConstants {
	
	/**
	 * Exception code for GetCapabilities that states that the version negotiation failed
	 */
	public static final String EXCEPTION_CODE_INVALID_VERSION = "VersionNegotiationFailed";
	
	/**
	 * Exception code for GetCapabilities/DescribeProcess that states that a parameter is missing
	 */
	public static final String EXCEPTION_CODE_MISSING_PARAM = "MissingParameterValue";
	
	/**
	 * Exception code for GetCapabilities/DescribeProcess that states that a parameter value is invalid
	 */
	public static final String EXCEPTION_CODE_INVALID_PARAM = "InvalidParameterValue";
	
	/**
	 * Exception code for cases where no other code applies
	 */
	public static final String EXCEPTION_CODE_OTHER = "NoApplicableCode";

}
