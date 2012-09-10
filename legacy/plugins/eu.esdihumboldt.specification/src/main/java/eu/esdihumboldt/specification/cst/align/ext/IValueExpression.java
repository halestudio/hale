/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2008 to 2010.
 */
package eu.esdihumboldt.specification.cst.align.ext;

/**
 * The superinterface for all ValueExpression.
 * 
 * @author A. Pitaev, Logica
 */

public interface IValueExpression {

	/**
	 * returns String values
	 * 
	 * @return
	 */
	public String getLiteral();

	/**
	 * @return the min
	 */
	public String getMin();

	/**
	 * @return the max
	 */
	public String getMax();

}
