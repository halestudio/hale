/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.util.validator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Validator that only checks whether one of its known validators validate the
 * given input.
 * 
 * @author Kai Schwierczek
 */
public class OrValidator implements CombinedValidator {

	private final LinkedList<Validator> validators = new LinkedList<Validator>();

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

	/**
	 * @return the internal validators
	 */
	@Override
	public List<Validator> getValidators() {
		return Collections.unmodifiableList(validators);
	}
}
