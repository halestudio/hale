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

import org.springframework.core.convert.ConversionException;

/**
 * Validator for digit counts. As in
 * (http://www.w3.org/TR/xmlschema-2/#rf-totalDigits and
 * http://www.w3.org/TR/xmlschema-2/#rf-fractionDigits)
 * 
 * @author Kai Schwierczek
 */
public class DigitCountValidator extends AbstractValidator {

	private final Type type;
	private final int length;

	/**
	 * Construct a validator that checks the digit count of the input to match
	 * the given type and value.
	 * 
	 * @param type the digits to check for
	 * @param length the length to check for
	 */
	public DigitCountValidator(Type type, int length) {
		this.type = type;
		this.length = length;
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#validate(Object)
	 */
	@Override
	public String validate(Object value) {
		// ignore null values, rely on the NillableFlagValidator for that.
		if (value == null) {
			return null;
		}

		BigDecimal decimal;
		try {
			decimal = getObjectAs(value, BigDecimal.class);
			if (decimal == null)
				return "Input must be a number.";
		} catch (ConversionException ce) {
			return "Input must be a number.";
		}

		switch (type) {
		case FRACTIONDIGITS:
			boolean ok = true;
			try {
				// try lowering the scale if it is too large without rounding
				// -> cut off ending zeros if possible
				if (decimal.scale() > length) {
					// throws exception if scaling is not possible
					/*
					 * FIXME this does not change the original value, so what
					 * does this achieve?
					 */
					decimal = decimal.setScale(length);
				}
			} catch (ArithmeticException ae) {
				ok = false; // scaling failed
			}
			if (ok)
				return null;
			else
				return "Input must have at most " + length + " fraction digits but has "
						+ decimal.scale() + ".";
		case TOTALDIGITS:
			// single zero in front of decimal point and ending zeros don't
			// count
			// here BigDecimal doesn't help, do string work.
			String numberString = decimal.abs().toPlainString();
			int indexOfDot = numberString.indexOf('.');
			if (indexOfDot != -1) {
				StringBuilder buf = new StringBuilder(numberString);
				// remove ending zeros
				while (buf.charAt(buf.length() - 1) == '0')
					buf.deleteCharAt(buf.length() - 1);

				// remove dot and maybe single zero in front of dot
				if (indexOfDot == 1 && buf.charAt(0) == '0')
					buf.delete(0, 2); // delete leading zero and .
				else
					buf.deleteCharAt(indexOfDot); // only delete point

				numberString = buf.toString();
			}
			else if (numberString.equals("0"))
				numberString = "";

			if (numberString.length() <= length)
				return null;
			else
				return "Input must have at most " + length + " total digits but has "
						+ numberString.length() + ".";
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
		case FRACTIONDIGITS:
			return "Input must be a number and must have at most " + length + " fraction digits.";
		case TOTALDIGITS:
			return "Input must be a number and must have at most " + length + " total digits.";
		default:
			return ""; // all types checked, doesn't happen
		}
	}

	/**
	 * @return the type of the compare operation
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the length to compare against
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Type specifies what DigitCountValidator should check.
	 */
	public enum Type {
		/**
		 * Check for fraction digit count.
		 */
		FRACTIONDIGITS,
		/**
		 * Check for total digit count.
		 */
		TOTALDIGITS;
	}
}
