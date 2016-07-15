/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.jdbc;

/**
 * JDBC utility methods.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class JDBCUtil {

	/**
	 * Removes one pair of leading/trailing quotes ("x" or 'x' or `x` becomes
	 * x).
	 * 
	 * @param s the string to remove quotes from
	 * @return the string with one pair of quotes less if possible
	 */
	public static String unquote(String s) {
		if (s == null) {
			return null;
		}

		char startChar = s.charAt(0);
		char endChar = s.charAt(s.length() - 1);
		if ((startChar == '\'' || startChar == '"' || startChar == '`') && startChar == endChar)
			return s.substring(1, s.length() - 1);
		else
			return s;
	}

	/**
	 * Adds a pair of quotes ("x") if no quotes (" or ') are present.
	 * 
	 * @param s the string to quote
	 * @return the quoted string
	 */
	public static String quote(String s) {
		if (s == null) {
			return null;
		}

		char startChar = s.charAt(0);
		char endChar = s.charAt(s.length() - 1);
		if ((startChar == '\'' || startChar == '"') && startChar == endChar)
			return s; // already quoted
		else
			return '"' + s + '"';
	}

}
