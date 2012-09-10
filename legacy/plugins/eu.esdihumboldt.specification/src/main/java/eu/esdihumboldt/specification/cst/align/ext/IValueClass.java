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

import java.util.List;

/**
 * The superinterface for all ValueClasses.
 * 
 * @author A. Pitaev, Logica
 */

public interface IValueClass {

	/**
	 * returns a list of the ValueExpressions
	 */
	public List<IValueExpression> getValue();

	/**
	 * return a class/attribute in case of reference
	 * 
	 * @return
	 */
	public String getResource();

	/**
	 * Gets the value of the about property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAbout();

}
