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

package eu.esdihumboldt.hale.common.cli.project

import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import eu.esdihumboldt.hale.common.core.report.ReportHandler
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment
import groovy.transform.CompileStatic

@CompileStatic
abstract class AbstractProjectEnvironmentCommand extends AbstractProjectCommand<ProjectTransformationEnvironment> {

	@Override
	ProjectTransformationEnvironment loadProject(URI location, ReportHandler reports) {
		new ProjectTransformationEnvironment(null, new DefaultInputSupplier(
				location), reports)
	}

	@Override
	String getProjectName(ProjectTransformationEnvironment project) {
		project?.project?.name
	}
}
