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
 * Validator that only checks whether all of its known validators validate the
 * given input.
 * 
 * @author Kai Schwierczek
 */
public class AndValidator implements CombinedValidator {

	private final LinkedList<Validator> validators = new LinkedList<Validator>();

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

	/**
	 * @return the internal validators
	 */
	@Override
	public List<Validator> getValidators() {
		return Collections.unmodifiableList(validators);
	}
}
