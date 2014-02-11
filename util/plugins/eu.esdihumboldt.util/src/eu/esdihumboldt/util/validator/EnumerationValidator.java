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
	 * @return the allowed values
	 */
	public Collection<String> getValues() {
		return Collections.unmodifiableCollection(values);
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Input must be one of: " + allowedValues();
	}
}
