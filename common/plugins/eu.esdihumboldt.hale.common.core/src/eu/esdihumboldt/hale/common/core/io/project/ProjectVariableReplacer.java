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

package eu.esdihumboldt.hale.common.core.io.project;

import eu.esdihumboldt.hale.common.core.io.util.AbstractScopedVariableReplacer;

/**
 * Project variable replacer.
 * 
 * @author Simon Templer
 */
public class ProjectVariableReplacer extends AbstractScopedVariableReplacer {

	/**
	 * Name of the scope for project variables.
	 */
	public static final String SCOPE_NAME = "project";

	private final ProjectVariables variables;

	/**
	 * Create a new project variable replacer.
	 * 
	 * @param projectInfo the project info service
	 */
	public ProjectVariableReplacer(ProjectInfoService projectInfo) {
		super();

		this.variables = new ProjectVariables(projectInfo);
	}

	@Override
	protected String getDefaultScope() {
		return SCOPE_NAME;
	}

	@Override
	protected String getVariable(String scope, String varName) {
		if (scope.equals(SCOPE_NAME)) {
			return variables.getStringOpt(varName);
		}
		return null;
	}

}
