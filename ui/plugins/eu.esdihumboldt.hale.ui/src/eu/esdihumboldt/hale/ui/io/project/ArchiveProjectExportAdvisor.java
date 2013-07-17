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

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectExport;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.DefaultIOAdvisor;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Advisor for the export of project archives
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectExportAdvisor extends DefaultIOAdvisor<ArchiveProjectExport> {

	/**
	 * @see eu.esdihumboldt.hale.ui.io.DefaultIOAdvisor#prepareProvider(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public void prepareProvider(ArchiveProjectExport provider) {
		super.prepareProvider(provider);

		ProjectService projectService = getService(ProjectService.class);
		Project project = (Project) projectService.getProjectInfo();
		provider.setProject(project);
		Map<String, ProjectFile> files = ProjectIO.createDefaultProjectFiles(HaleUI
				.getServiceProvider());
		provider.setProjectFiles(files);
	}
}
