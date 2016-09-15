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

package eu.esdihumboldt.hale.ui.transformation;

import eu.esdihumboldt.hale.common.align.transformation.function.impl.DefaultTransformationVariables;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;
import eu.esdihumboldt.hale.common.core.io.project.ProjectVariables;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.VariableReplacer;

/**
 * Variable replace for transformation variables.
 * 
 * @author Simon Templer
 */
public class TransformationVariableReplacer implements VariableReplacer {

	@Override
	public String replaceVariables(String input) {
		ProjectInfoService projectInfo = HaleUI.getServiceProvider()
				.getService(ProjectInfoService.class);
		if (projectInfo != null) {
			ProjectVariables projectVariables = new ProjectVariables(projectInfo);
			DefaultTransformationVariables vars = new DefaultTransformationVariables(
					projectVariables);
			return vars.replaceVariables(input, true);
		}
		else {
			return input;
		}
	}

}
