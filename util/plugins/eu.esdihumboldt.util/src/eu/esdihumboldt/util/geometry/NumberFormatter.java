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

package eu.esdihumboldt.util.geometry;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Format the number in required representation
 * 
 * @author Arun
 */
public class NumberFormatter {

	private static final ALogger log = ALoggerFactory.getLogger(NumberFormatter.class);

	/**
	 * Format number to specified format
	 * 
	 * @param value double value
	 * @param format a pattern
	 * @return String presentation of a formatted number
	 */
	public static String formatTo(double value, String format) {
		if (format == null || format.equals("") || (!validateFormat(format)))
			return String.valueOf(value);
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		DecimalFormat formatter = new DecimalFormat(format, symbols);
		return formatter.format(value);
	}

	private static boolean validateFormat(String format) {
		String regEx = "0{1,13}(\\.0*)?";
		if (!Pattern.matches(regEx, format)) {
			log.warn("supplied format for formatted number output is not valid!");
			return false;
		}
		return true;
	}
}
