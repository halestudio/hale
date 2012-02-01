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

/**
 * Validator for input lengths.
 * 
 * @author Kai Schwierczek
 */
public class LengthValidator extends AbstractValidator {
	private Type type;
	private int length;
	
	/**
	 * Construct a validator that checks the length of the input to match the given type and value.
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
