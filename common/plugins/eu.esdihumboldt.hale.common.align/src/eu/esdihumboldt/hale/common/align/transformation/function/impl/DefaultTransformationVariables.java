/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationVariableScope;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationVariables;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.ProjectVariables;

/**
 * Default implementation of transformation variables provided to transformation
 * functions.
 * 
 * @author Simon Templer
 */
public class DefaultTransformationVariables implements TransformationVariables {

	private static final ALogger log = ALoggerFactory
			.getLogger(DefaultTransformationVariables.class);

	private final ProjectVariables projectVariables;

	/**
	 * Constructor.
	 * 
	 * @param projectVariables the project variables
	 */
	public DefaultTransformationVariables(ProjectVariables projectVariables) {
		super();
		this.projectVariables = projectVariables;
	}

	@Override
	public Value getVariable(TransformationVariableScope scope, String name) {
		if (scope == null) {
			scope = TransformationVariableScope.transformation;
		}

		switch (scope) {
		case project:
			return projectVariables.getValue(name);
		default:
			// not supported
			return Value.NULL;
		}
	}

	@Override
	public String replaceVariables(String input) {
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
						TransformationVariableScope scope = TransformationVariableScope.transformation;

						int sepIndex = reference.indexOf(':');
						if (sepIndex > 0) {
							String scopeName = reference.substring(0, sepIndex);
							scope = TransformationVariableScope.valueOf(scopeName);
							varName = reference.substring(sepIndex + 1);
						}
						else {
							// default scope
							varName = reference;
						}

						Value value = getVariable(scope, varName);

						String strValue = value.as(String.class);

						if (strValue != null) {
							// only replace if there is a value
							String rx = "\\{\\{" + Pattern.quote(reference) + "\\}\\}";
							result = result.replaceAll(rx, strValue);
						}
					} catch (Exception e) {
						log.warn("Cannot resolve variable reference " + reference);
					}
				}

			}
		} catch (Exception e) {
			log.error("Error replacing transformation variables", e);
		}
		return result;
	}

}
