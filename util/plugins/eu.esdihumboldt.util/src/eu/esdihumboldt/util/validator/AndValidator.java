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
import java.util.LinkedList;

/**
 * Validator that only checks whether all of its known validators validate the
 * given input.
 * 
 * @author Kai Schwierczek
 */
public class AndValidator implements Validator {

	private LinkedList<Validator> validators = new LinkedList<Validator>();

	/**
	 * Constructs an AndValidator with the given validators.
	 * 
	 * @param validators the validators.
	 */
	public AndValidator(Collection<Validator> validators) {
		for (Validator validator : validators)
			addValidator(validator);
	}

	/**
	 * Adds the given Validator to this Validator.<br>
	 * Validators which are always true are ignored and AndValidators get
	 * flattened.
	 * 
	 * @param validator the validator to add
	 */
	private void addValidator(Validator validator) {
		// only add validators which aren't always true
		if (!validator.isAlwaysTrue()) {
			// flatten AndVvalidators
			if (validator instanceof AndValidator)
				for (Validator childValidator : ((AndValidator) validator).validators)
					addValidator(childValidator);
			else
				validators.add(validator);
		}
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#validate(Object)
	 */
	@Override
	public String validate(Object value) {
		StringBuilder result = new StringBuilder();
		int numFailed = 0;

		// output result of all validators
		for (Validator validator : validators) {
			String validation = validator.validate(value);
			if (validation != null) {
				numFailed++;
				if (result.length() > 0)
					result.append('\n');
				result.append(validation);
			}
		}
		if (numFailed > 1)
			return "All of the following are false:\n   "
					+ result.toString().replace("\n", "\n   ");

		if (numFailed > 0)
			return result.toString();
		else
			return null;
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#getDescription()
	 */
	@Override
	public String getDescription() {
		// special cases
		if (validators.size() == 0) // also special case isAlwaysTrue()
			return "Every input is valid";
		if (validators.size() == 1)
			return validators.getFirst().getDescription();

		// default: put together descriptions of known validators
		StringBuilder description = new StringBuilder();
		description.append("All of the following must be true to be valid:");
		for (Validator validator : validators)
			description.append("\n   ").append(validator.getDescription().replace("\n", "\n   "));

		return description.toString();
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#isAlwaysTrue()
	 */
	@Override
	public boolean isAlwaysTrue() {
		for (Validator validator : validators)
			if (!validator.isAlwaysTrue())
				return false;
		return true;
	}
}
