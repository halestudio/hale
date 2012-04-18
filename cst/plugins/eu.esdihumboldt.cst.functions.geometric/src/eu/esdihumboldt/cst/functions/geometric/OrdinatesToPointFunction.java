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

package eu.esdihumboldt.cst.functions.geometric;

/**
 * Ordinates to point constants.
 * 
 * @author Kevin Mais
 */
public interface OrdinatesToPointFunction {

	/**
	 * Name of the parameter specifying the reference system to use.
	 */
	public static final String PARAMETER_REFERENCE_SYSTEM = "referenceSystem";
	
	/**
	 * the ordinates to point function id
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.geometric.ordinates_to_point";
	
}
