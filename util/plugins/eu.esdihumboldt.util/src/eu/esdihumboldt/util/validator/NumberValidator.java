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

import java.math.BigDecimal;
import java.util.Collection;

import org.springframework.core.convert.ConversionException;

/**
 * Validator for number ranges.
 * 
 * @author Kai Schwierczek
 */
public class NumberValidator extends AbstractValidator {

	private final Type type;
	private final BigDecimal value;

	/**
	 * Construct a validator that checks the value of the input to match the
	 * given type and value.
	 * 
	 * @param type the condition to check for
	 * @param value the value to check for
	 */
	public NumberValidator(Type type, BigDecimal value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#validate(java.lang.Object)
	 */
	@Override
	public String validate(Object value) {
		// ignore null values, rely on the NillableFlagValidator for that.
		if (value == null) {
			return null;
		}

		BigDecimal bdValue;
		if (value instanceof Collection) {
			bdValue = BigDecimal.valueOf(((Collection<?>) value).size());
		}
		else {
			try {
				bdValue = getObjectAs(value, BigDecimal.class);
				if (bdValue == null)
					return "Input must be a number.";
			} catch (ConversionException ce) {
				return "Input must be a number.";
			}
		}

		switch (type) {
		case MAXEXCLUSIVE:
			if (bdValue.compareTo(this.value) < 0)
				return null;
			else
				return "Input must at most be " + this.value + " (exclusive), but is " + bdValue
						+ ".";
		case MAXINCLUSIVE:
			if (bdValue.compareTo(this.value) <= 0)
				return null;
			else
				return "Input must at most be " + this.value + " (inclusive), but is " + bdValue
						+ ".";
		case MINEXCLUSIVE:
			if (bdValue.compareTo(this.value) > 0)
				return null;
			else
				return "Input must at least be " + this.value + " (exclusive), but is " + bdValue
						+ ".";
		case MININCLUSIVE:
			if (bdValue.compareTo(this.value) >= 0)
				return null;
			else
				return "Input must at least be " + this.value + " (inclusive), but is " + bdValue
						+ ".";
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
		case MAXEXCLUSIVE:
			return "Input must be a number and must at most be " + value + " (exclusive).";
		case MAXINCLUSIVE:
			return "Input must be a number and must at most be " + value + " (inclusive).";
		case MINEXCLUSIVE:
			return "Input must be a number and must at least be " + value + " (exclusive).";
		case MININCLUSIVE:
			return "Input must be a number and must at least be " + value + " (inclusive).";
		default:
			return ""; // all types checked, doesn't happen
		}
	}

	/**
	 * @return the type of the comparison
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the value to compare against
	 */
	public BigDecimal getValue() {
		return value;
	}

	/**
	 * Type specifies what NumberValidator should check.
	 */
	public enum Type {
		/**
		 * Check for maximum value (exclusive).
		 */
		MAXEXCLUSIVE,
		/**
		 * Check for maximum value (inclusive).
		 */
		MAXINCLUSIVE,
		/**
		 * Check for minimum value (exclusive).
		 */
		MINEXCLUSIVE,
		/**
		 * Check for minimum value (inclusive).
		 */
		MININCLUSIVE;
	}
}
