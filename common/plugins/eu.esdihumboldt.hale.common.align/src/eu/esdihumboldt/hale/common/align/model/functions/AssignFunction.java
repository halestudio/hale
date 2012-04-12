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

package eu.esdihumboldt.hale.common.align.model.functions;

/**
 * Assign function constants.
 * @author Simon Templer
 */
public interface AssignFunction {
	
	/**
	 * the assign function Id
	 */
	public static final String ID = "eu.esdihumboldt.hale.align.assign";
	
	/**
	 * Name of the parameter specifying the value to assign. See the function
	 * definition on <code>eu.esdihumboldt.hale.common.align</code>.
	 */
	public static final String PARAMETER_VALUE = "value";

}
