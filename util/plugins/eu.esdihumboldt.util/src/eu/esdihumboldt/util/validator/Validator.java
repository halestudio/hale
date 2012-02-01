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

package eu.esdihumboldt.util.validator;

/**
 * Validator for strings.
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
	public String validate(Object value);

	/**
	 * Returns a human readable representation of this Validator.
	 * 
	 * @return a human readable representation of this Validator
	 */
	public String getDescription();

	/**
	 * Returns true, if this Validator validates all values to true.<br>
	 * The result must not necessarily be correct. If the validator does not
	 * know its result for sure it must return false.
	 * 
	 * @return true, iff the validator knows for sure, that it validates all
	 *         values to true
	 */
	public boolean isAlwaysTrue();
}
