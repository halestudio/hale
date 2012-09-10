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

import java.math.BigDecimal;

import org.springframework.core.convert.ConversionException;

/**
 * Validator for number ranges.
 * 
 * @author Kai Schwierczek
 */
public class NumberValidator extends AbstractValidator {

	private Type type;
	private BigDecimal value;

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
		BigDecimal bdValue;
		try {
			bdValue = getObjectAs(value, BigDecimal.class);
			if (bdValue == null)
				return "Input must be a number.";
		} catch (ConversionException ce) {
			return "Input must be a number.";
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
