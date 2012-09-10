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

package eu.esdihumboldt.hale.common.align.extension.function;

import com.google.common.collect.ListMultimap;

/**
 * Validator for function parameters.
 * 
 * @author Kai Schwierczek
 */
public interface Validator {

	/**
	 * Checks whether the given value is valid.
	 * 
	 * @param value the value to check
	 * @return null, if the value is valid, otherwise the reason why it's
	 *         invalid
	 */
	public String validate(String value);

	/**
	 * Sets this validators parameters.
	 * 
	 * @param parameters the parameters
	 */
	public void setParameters(ListMultimap<String, String> parameters);
}
