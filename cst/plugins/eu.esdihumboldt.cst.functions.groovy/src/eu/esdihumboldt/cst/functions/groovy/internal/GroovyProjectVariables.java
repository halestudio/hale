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

package eu.esdihumboldt.cst.functions.groovy.internal;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;
import eu.esdihumboldt.hale.common.core.io.project.ProjectVariables;
import groovy.lang.GroovyObjectSupport;

/**
 * Accessor for project variables in Groovy scripts.
 * 
 * @author Simon Templer
 */
public class GroovyProjectVariables extends GroovyObjectSupport {

	private final ProjectVariables variables;
	private final TransformationLogWrapper cellLog;
	private final ExecutionContext executionContext;

	/**
	 * Constructor.
	 * 
	 * @param projectInfo the project information service
	 * @param executionContext the transformation execution context
	 * @param cellLog the cell-associated transformation log
	 */
	public GroovyProjectVariables(ProjectInfoService projectInfo, TransformationLogWrapper cellLog,
			ExecutionContext executionContext) {
		this.variables = new ProjectVariables(projectInfo);
		this.cellLog = cellLog;
		this.executionContext = executionContext;
	}

	/**
	 * Support subscript operator.
	 * 
	 * @param name the variable name
	 * @return the variable value or <code>null</code>
	 */
	@Nullable
	public String getAt(String name) {
		String value = variables.getStringOpt(name);
		if (value == null) {
			reportMissing(name);
		}
		return value;
	}

	private void reportMissing(String name) {
		// already reported?
		Object previous = executionContext.getTransformationContext()
				.put("REPORTED_MISSING_VARIABLE_" + name, true);
		if (previous != null) {
			cellLog.warn("Project variable '" + name + "' accessed in script is not defined.");
		}
	}

	/**
	 * Get a variable value with a default value as fall-back.
	 * 
	 * @param name the variable name
	 * @param def the variable default value
	 * @return the variable value or the default
	 */
	public String get(String name, String def) {
		return variables.getString(name, def);
	}

	/**
	 * Get a variable value. Fail with an exception if the variable does not
	 * exist.
	 * 
	 * @param name the variable name
	 * @return the variable value
	 * @throws IllegalArgumentException if a variable value with the given name
	 *             cannot be retrieved
	 */
	public String getOrFail(String name) {
		return variables.getString(name);
	}

	@Override
	public Object getProperty(String property) {
		return getAt(property);
	}

}
