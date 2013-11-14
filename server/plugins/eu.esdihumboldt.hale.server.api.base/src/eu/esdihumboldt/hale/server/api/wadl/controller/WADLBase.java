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

package eu.esdihumboldt.hale.server.api.wadl.controller;

import groovy.lang.Closure;

/**
 * WADL controller base class.
 * 
 * @author Simon Templer
 */
public class WADLBase {

	/**
	 * Normalize a request mapping pattern.
	 * 
	 * @param pattern the pattern
	 * @param closure the closure to apply to the content of any set of curly
	 *            braces
	 * @return the normalized pattern
	 */
	protected String normalizePattern(String pattern, Closure<String> closure) {
		StringBuilder result = new StringBuilder();
		StringBuilder inBrackets = new StringBuilder();

		int openBrackets = 0;
		for (int i = 0; i < pattern.length(); i++) {
			boolean record = true;
			char c = pattern.charAt(i);
			if (c == '{' && (i == 0 || pattern.charAt(i - 1) != '\\')) {
				openBrackets++;
				record = false;
			}
			else if (openBrackets > 0 && c == '}' && (i == 0 || pattern.charAt(i - 1) != '\\')) {
				openBrackets--;
				record = false;

				if (openBrackets == 0) {
					// bracket closed, handle bracket content
					boolean isEnd = i == pattern.length() - 1;
					String bracket = closure.call(inBrackets.toString(), isEnd);
					if (bracket != null && !bracket.isEmpty()) {
						result.append('{');
						result.append(bracket);
						result.append('}');
					}
					// reset brackets builder
					inBrackets = new StringBuilder();
				}
			}

			if (record) {
				if (openBrackets == 0) {
					// normal content -> add to result
					result.append(c);
				}
				else if (openBrackets > 0) {
					// belongs to outer bracket
					inBrackets.append(c);
				}
			}
		}

		return result.toString();
	}

}
