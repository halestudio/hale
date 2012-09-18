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

/**
 * Validator for input lengths.
 * 
 * @author Kai Schwierczek
 */
public class LengthValidator extends AbstractValidator {

	private Type type;
	private int length;

	/**
	 * Construct a validator that checks the length of the input to match the
	 * given type and value.
	 * 
	 * @param type the length attribute to check for
	 * @param length the length to check for
	 */
	public LengthValidator(Type type, int length) {
		this.type = type;
		this.length = length;
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#validate(Object)
	 */
	@Override
	public String validate(Object value) {
		String s = getObjectAs(value, String.class);
		switch (type) {
		case MINIMUM:
			if (s.length() >= length)
				return null;
			else
				return "Input length must at least be " + length + " but is " + s.length();
		case MAXIMUM:
			if (s.length() <= length)
				return null;
			else
				return "Input length must at most be " + length + " but is " + s.length();
		case EXACT:
			if (s.length() == length)
				return null;
			else
				return "Input length must exactly be " + length + " but is " + s.length();
		default:
			return null; // all types checked, doesn't happen
		}
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#getDescription()
	 */
	@Override
	public String getDescription() {
		switch (type) {
		case MINIMUM:
			return "Input length must at least be " + length + ".";
		case MAXIMUM:
			return "Input length must at most be " + length + ".";
		case EXACT:
			return "Input length must exactly be " + length + ".";
		default:
			return ""; // all types checked, doesn't happen
		}
	}

	/**
	 * Type specifies what the LengthValidator should check.
	 */
	public enum Type {
		/**
		 * Check for minimum length.
		 */
		MINIMUM,
		/**
		 * Check for maximum length.
		 */
		MAXIMUM,
		/**
		 * Check for exact length.
		 */
		EXACT;
	}
}
