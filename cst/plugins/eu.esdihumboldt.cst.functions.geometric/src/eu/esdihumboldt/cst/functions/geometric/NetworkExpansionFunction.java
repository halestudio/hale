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
 * Network expansion constants.
 * 
 * @author Kevin Mais
 */
public interface NetworkExpansionFunction {
	
	/**
	 * Name of the parameter specifying the buffer width.
	 */
	public static final String PARAMETER_BUFFER_WIDTH = "bufferWidth";

	/**
	 * the network expansion function id
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.geometric.networkexpansion";
}
