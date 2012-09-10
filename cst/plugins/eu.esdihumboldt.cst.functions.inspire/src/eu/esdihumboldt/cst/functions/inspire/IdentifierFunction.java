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

package eu.esdihumboldt.cst.functions.inspire;

/**
 * Constants for the identifier function
 * 
 * @author Kevin Mais
 * 
 */
@SuppressWarnings("javadoc")
public interface IdentifierFunction {

	public static final String ID = "eu.esdihumboldt.cst.functions.inspire.identifier";

	public static final String COUNTRY_PARAMETER_NAME = "countryName"; //$NON-NLS-1$
	public static final String DATA_PROVIDER_PARAMETER_NAME = "providerName"; //$NON-NLS-1$
	public static final String PRODUCT_PARAMETER_NAME = "productName"; //$NON-NLS-1$
	public static final String VERSION = "version"; //$NON-NLS-1$
	public static final String VERSION_NIL_REASON = "versionNilReason"; //$NON-NLS-1$

	public static final String INSPIRE_IDENTIFIER_PREFIX = "urn:x-inspire:object:id"; //$NON-NLS-1$

}
