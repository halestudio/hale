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

package eu.esdihumboldt.hale.io.haleconnect.ui.projects;

import java.net.URI;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.io.haleconnect.project.HaleConnectProjectWriter;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * I/O advisor for exporting projects to hale connect
 * 
 * @author Florian Esser
 */
public class HaleConnectProjectExportAdvisor extends DefaultIOAdvisor<HaleConnectProjectWriter> {

	@Override
	public void prepareProvider(HaleConnectProjectWriter provider) {
		super.prepareProvider(provider);

		ProjectService projectService = getService(ProjectService.class);
		Project project = (Project) projectService.getProjectInfo();
		provider.setProject(project);
	}

	@Override
	public void updateConfiguration(HaleConnectProjectWriter provider) {
		super.updateConfiguration(provider);

		Map<String, ProjectFile> files = ProjectIO
				.createDefaultProjectFiles(HaleUI.getServiceProvider());
		provider.setProjectFiles(files);

		URI projectLocation = getService(ProjectService.class).getLoadLocation();
		if (projectLocation != null) {
			provider.setPreviousTarget(projectLocation);
		}
	}

}
