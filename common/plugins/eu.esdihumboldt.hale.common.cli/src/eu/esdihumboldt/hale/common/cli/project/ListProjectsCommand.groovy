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

import java.nio.file.Path
import java.nio.file.Paths

import eu.esdihumboldt.hale.common.core.report.ReportHandler
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment
import eu.esdihumboldt.util.cli.CommandContext
import groovy.transform.CompileStatic

@CompileStatic
class ListProjectsCommand extends AbstractProjectEnvironmentCommand {

	final String shortDescription = 'List hale projects found at the specified location'

	@Override
	int runForProjects(List<URI> projects, OptionAccessor options, CommandContext context) {
		println()
		if (projects) {
			println 'Found the following hale projects:'
			projects.each { URI location ->
				try {
					Path path = Paths.get(location)
					Path relative = Paths.get('.').toAbsolutePath().parent.relativize(path)
					println "  $relative"
				} catch (e) {
					e.printStackTrace()
					// ignore - just print URI
					println "  $location"
				}
			}
			0
		}
		else {
			println 'No hale projects found.'
			0
		}
	}

	boolean runForProject(ProjectTransformationEnvironment projectEnv, URI projectLocation,
			OptionAccessor options, CommandContext context, ReportHandler reports) {
		// is not being called
		true
	}
}
