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
 * Validator that only checks whether one of its known validators validate the
 * given input.
 * 
 * @author Kai Schwierczek
 */
public class OrValidator implements Validator {

	private LinkedList<Validator> validators = new LinkedList<Validator>();

	/**
	 * Constructs an OrValidator with the given validators.
	 * 
	 * @param validators the validators.
	 */
	public OrValidator(Collection<Validator> validators) {
		// if one validator is always true, only keep it and forget the rest.
		boolean alwaysTrue = false;
		for (Validator validator : validators)
			if (validator.isAlwaysTrue()) {
				alwaysTrue = true;
				this.validators.add(validator);
				break;
			}
		if (!alwaysTrue)
			this.validators.addAll(validators);
	}

	/**
	 * Adds the given Validator to this Validator.<br>
	 * OrValidators get flattened.
	 * 
	 * @param validator the validator to add
	 */
	public void addValidator(Validator validator) {
		// flatten OrVvalidators
		if (validator instanceof OrValidator)
			for (Validator childValidator : ((OrValidator) validator).validators)
				addValidator(childValidator);
		else
			validators.add(validator);
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#validate(Object)
	 */
	@Override
	public String validate(Object value) {
		if (validators.size() == 1) {
			return validators.getFirst().validate(value);
		}

		StringBuilder result = new StringBuilder();

		// add results of all validators
		for (Validator validator : validators) {
			String validation = validator.validate(value);
			if (validation != null) {
				result.append("\n   ").append(validation.replace("\n", "\n   "));
			}
			else
				// one validator validates!
				return null;
		}

		if (result.length() > 0)
			return "All possibilities are false:" + result.toString();
		else
			return "Every input is invalid.";
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#getDescription()
	 */
	@Override
	public String getDescription() {
		// special cases
		if (validators.size() == 0)
			return "No input is valid.";
		if (validators.size() == 1) // also special case isAlwaysTrue()
			return validators.getFirst().getDescription();

		// default: put together descriptions of known validators
		StringBuilder description = new StringBuilder();
		description.append("One of the following must be true to be valid:");
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
			if (validator.isAlwaysTrue())
				return true;
		return false;
	}
}
