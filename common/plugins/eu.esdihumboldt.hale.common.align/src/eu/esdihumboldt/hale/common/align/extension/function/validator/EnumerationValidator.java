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

package eu.esdihumboldt.hale.common.align.extension.function.validator;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.Validator;

/**
 * Validator that checks whether the value is one of the given values.<br>
 * 
 * Expects parameters "value".
 * 
 * @author Kai Schwierczek
 */
public class EnumerationValidator implements Validator {

	private List<String> values;

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.Validator#validate(java.lang.String)
	 */
	@Override
	public String validate(String value) {
		if (values.contains(value))
			return null;
		else
			return "Input must be one of " + Joiner.on(", ").join(values);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.Validator#setParameters(com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameters(ListMultimap<String, String> parameters) {
		values = parameters.get("value");
	}

}
