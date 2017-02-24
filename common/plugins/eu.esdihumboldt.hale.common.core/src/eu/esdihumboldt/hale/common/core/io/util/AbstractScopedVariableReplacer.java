/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.core.io.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Default base class for variable replaces where variables are placed in double
 * curly braces and may have a scope.
 * 
 * @author Simon Templer
 */
public abstract class AbstractScopedVariableReplacer implements VariableReplacer {

	private static final ALogger log = ALoggerFactory
			.getLogger(AbstractScopedVariableReplacer.class);

	@Override
	public String replaceVariables(String input, boolean failUnresolved) {
		if (input == null) {
			return null;
		}

		String result = input;
		try {
			String re = "\\{\\{([^}]+)\\}\\}";
			Pattern p = Pattern.compile(re);
			Matcher m = p.matcher(input);

			// Iterate over identifiers and determine values.
			while (m.find()) {
				String reference = m.group(1);

				if (reference != null) {
					try {
						String varName;
						String scope = getDefaultScope();

						int sepIndex = reference.indexOf(':');
						if (sepIndex > 0) {
							String scopeName = reference.substring(0, sepIndex);
							scope = scopeName;
							varName = reference.substring(sepIndex + 1);
						}
						else {
							// default scope
							varName = reference;
						}

						String strValue = getVariable(scope, varName);

						if (strValue != null) {
							// only replace if there is a value
							String rx = "\\{\\{" + Pattern.quote(reference) + "\\}\\}";
							result = result.replaceAll(rx, strValue);
						}
						else if (failUnresolved) {
							throw new IllegalStateException(
									"Cannot resolve variable reference " + reference);
						}
					} catch (IllegalStateException e) {
						throw e;
					} catch (Exception e) {
						if (failUnresolved) {
							throw new IllegalStateException(
									"Cannot resolve variable reference " + reference);
						}
						log.warn("Cannot resolve variable reference " + reference);
					}
				}

			}
		} catch (IllegalStateException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error replacing transformation variables", e);
		}
		return result;
	}

	/**
	 * @return the default scope name
	 */
	protected String getDefaultScope() {
		return null;
	}

	/**
	 * Get the string value of a variable or <code>null</code>.
	 * 
	 * @param scope the scope name or <code>null</code>
	 * @param varName the name of the variable
	 * @return the variable value or <code>null</code>
	 */
	protected abstract String getVariable(@Nullable String scope, String varName);

}
