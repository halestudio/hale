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

package eu.esdihumboldt.hale.ui.io.project;

import java.net.URI;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Advisor for the export of project archives
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectExportAdvisor extends DefaultIOAdvisor<ArchiveProjectWriter> {

	@Override
	public void prepareProvider(ArchiveProjectWriter provider) {
		super.prepareProvider(provider);

		ProjectService projectService = getService(ProjectService.class);
		Project project = (Project) projectService.getProjectInfo();
		// set a copy of the project
		provider.setProject(project.clone());
	}

	@Override
	public void updateConfiguration(ArchiveProjectWriter provider) {
		super.updateConfiguration(provider);

		Map<String, ProjectFile> files = ProjectIO.createDefaultProjectFiles(HaleUI
				.getServiceProvider());
		provider.setProjectFiles(files);

		URI projectLocation = getService(ProjectService.class).getLoadLocation();
		if (projectLocation != null) {
			provider.setPreviousTarget(projectLocation);
		}
	}
}
