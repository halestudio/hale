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

import java.util.Collection;

/**
 * Validator for a specific set of allowed inputs.
 * 
 * @author Kai Schwierczek
 */
public class EnumerationValidator extends AbstractValidator {

	// XXX rewrite to parameterized version?

	private final Collection<String> values;

	/**
	 * Constructor.
	 * 
	 * @param values the allowed values
	 */
	public EnumerationValidator(Collection<String> values) {
		this.values = values;
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#validate(java.lang.Object)
	 */
	@Override
	public String validate(Object value) {
		String stringValue = getObjectAs(value, String.class);
		if (values.contains(stringValue))
			return null;
		else
			return "Input " + stringValue + " is not one of the allowed values (" + allowedValues()
					+ ").";
	}

	/**
	 * Returns a string containing all allowed values.
	 * 
	 * @return a string containing all allowed values
	 */
	private String allowedValues() {
		StringBuilder buf = new StringBuilder();
		for (String s : values) {
			if (buf.length() > 0)
				buf.append(", ");
			buf.append(s);
		}
		return buf.toString();
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Input must be one of: " + allowedValues();
	}
}
