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

package eu.esdihumboldt.hale.common.core.io.project;

import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;

/**
 * {@link ProjectInfoService} implementation with a fixed associated project.
 * 
 * @author Simon Templer
 */
public class FixedProjectInfoService implements ProjectInfoService {

	private final Project project;
	private final URI loadLocation;
	private final ComplexConfigurationService config;

	/**
	 * Create a new project info service with the given project.
	 * 
	 * @param project the project
	 * @param loadLocation location the project was loaded from
	 */
	public FixedProjectInfoService(Project project, URI loadLocation) {
		super();
		this.project = project;
		this.loadLocation = loadLocation;

		this.config = ProjectIO.createProjectConfigService(project);
	}

	@Override
	public ProjectInfo getProjectInfo() {
		return project;
	}

	@Override
	public Value getProperty(String name) {
		return config.getProperty(name);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService#getLoadLocation()
	 */
	@Override
	public URI getLoadLocation() {
		return loadLocation;
	}

}
