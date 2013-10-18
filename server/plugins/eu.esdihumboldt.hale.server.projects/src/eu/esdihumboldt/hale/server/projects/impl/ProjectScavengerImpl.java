/*
 * Copyright (c) 2012 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.server.projects.impl;

import java.io.File;
import java.io.IOException;

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.headless.EnvironmentManager;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.common.headless.scavenger.AbstractProjectScavenger;
import eu.esdihumboldt.hale.server.projects.ProjectScavenger;

/**
 * Scans for projects in a directory. Manages if projects are active and in that
 * case publishes them as {@link TransformationEnvironment}s to an
 * {@link EnvironmentManager}.
 * 
 * @author Simon Templer
 */
public class ProjectScavengerImpl extends
		AbstractProjectScavenger<EnvironmentManager, ProjectHandler> implements ProjectScavenger {

	/**
	 * Create a scavenger instance.
	 * 
	 * @param environments the environments manager to populate
	 * @param scavengeLocation the location to scan, if the location does not
	 *            exist or is not accessible, a default location inside the
	 *            platform instance location is used
	 */
	public ProjectScavengerImpl(EnvironmentManager environments, File scavengeLocation) {
		super(scavengeLocation, "projects", environments);
	}

	@Override
	protected void onRemove(ProjectHandler reference, String resourceId) {
		// remove from the env manager as well
		getContext().removeEnvironment(resourceId);
	}

	@Override
	protected void onAdd(ProjectHandler reference, String resourceId) {
		// everything that is needed is already handled in loadReference
	}

	@Override
	protected ProjectHandler loadReference(File resourceFolder, String resourceFileName,
			String resourceId) throws IOException {
		// create a new project handler
		ProjectHandler config = new ProjectHandler(resourceFolder, resourceFileName, resourceId);
		config.update(getContext());
		return config;
	}

	@Override
	public Status getStatus(String projectId) {
		ProjectHandler project = getReference(projectId);
		if (project == null) {
			return Status.NOT_AVAILABLE;
		}
		else {
			return project.getStatus();
		}
	}

	@Override
	public File getLoadReports(String projectId) {
		ProjectHandler project = getReference(projectId);
		if (project != null) {
			return project.getLoadReportFile();
		}
		return null;
	}

	@Override
	public ProjectInfo getInfo(String projectId) {
		ProjectHandler project = getReference(projectId);
		if (project != null) {
			return project.getProjectInfo();
		}
		return null;
	}

	@Override
	public void activate(String projectId) {
		ProjectHandler project = getReference(projectId);
		if (project != null) {
			project.activate(getContext());
		}
	}

	@Override
	public void deactivate(String projectId) {
		ProjectHandler project = getReference(projectId);
		if (project != null) {
			project.deactivate(getContext());
		}
	}

}
