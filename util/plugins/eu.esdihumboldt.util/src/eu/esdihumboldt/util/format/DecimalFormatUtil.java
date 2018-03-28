/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.util.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Utility methods for number formatting
 * 
 * @author Arun Verma
 * @author Florian Esser
 */
public class DecimalFormatUtil {

	private DecimalFormatUtil() {
		// Utility class
	}

	/**
	 * Create a {@link DecimalFormat} instance for the given pattern that uses
	 * '.' as the decimal separator.
	 * 
	 * @param pattern the pattern string
	 * @return A {@link DecimalFormat} for the given pattern
	 * @throws IllegalArgumentException if the given pattern is invalid
	 * @throws NullPointerException if the given pattern is <code>null</code>
	 * @see DecimalFormat
	 */
	public static DecimalFormat getFormatter(String pattern) throws IllegalArgumentException {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		return new DecimalFormat(pattern, symbols);
	}

	/**
	 * Call {@link DecimalFormat#format(Object)} for the given value if the
	 * formatter is not <code>null</code>, otherwise call
	 * {@link String#valueOf(Object)}.
	 * 
	 * @param value double value to format
	 * @param formatter a {@link DecimalFormat} instance or <code>null</code>
	 * @return the formatted value
	 */
	public static String applyFormatter(Number value, DecimalFormat formatter) {
		if (formatter == null) {
			return String.valueOf(value);
		}

		return formatter.format(value);
	}
}
