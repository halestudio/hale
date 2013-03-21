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

import org.apache.xmlbeans.impl.regex.RegularExpression;

/**
 * Validator using pattern matching. Should be conform to
 * http://www.w3.org/TR/xmlschema-2/#dt-regex.
 * 
 * @author Kai Schwierczek
 */
public class PatternValidator extends AbstractValidator {

	private final String pattern;
	private RegularExpression regEx;

	/**
	 * Construct a PatternValidator with the given pattern.
	 * 
	 * @param pattern the pattern to use
	 */
	public PatternValidator(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#validate(Object)
	 */
	@Override
	public String validate(Object value) {
		if (value == null)
			return null;

		String s = getObjectAs(value, String.class);
		if (regEx == null)
			regEx = new RegularExpression(pattern, "X");
		if (regEx.matches(s))
			return null;
		else
			return "Input doesn't match " + pattern + ".";
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Input must match this pattern: " + pattern + ".";
	}
}
