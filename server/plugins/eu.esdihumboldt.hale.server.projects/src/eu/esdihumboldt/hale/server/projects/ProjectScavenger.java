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

package eu.esdihumboldt.hale.server.projects;

import java.util.Set;

import eu.esdihumboldt.hale.common.headless.EnvironmentManager;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;

/**
 * Service that scans for projects in a directory. Manages if projects are
 * active and in that case publishes them as {@link TransformationEnvironment}s
 * to an {@link EnvironmentManager}.
 * 
 * @author Simon Templer
 */
public interface ProjectScavenger {

	/**
	 * Project states
	 */
	public enum Status {
		/** This project does not exist */
		NOT_AVAILABLE,
		/** The project exists but cannot be loaded */
		BROKEN,
		/** The project is deactivated */
		INACTIVE,
		/** The project is loaded and enabled */
		ACTIVE
	}

	/**
	 * Check if there are any new projects available.
	 */
	public void triggerScan();

	/**
	 * Get the identifiers of the available projects.
	 * 
	 * @return the set of identifiers of all available projects
	 */
	public Set<String> getProjects();

	/**
	 * Get the status of the project with the given identifier.
	 * 
	 * @param projectId the project identifier
	 * @return the project status
	 */
	public Status getStatus(String projectId);

	/**
	 * Activate the project with the given identifier.
	 * 
	 * @param projectId the project identifier
	 */
	public void activate(String projectId);

	/**
	 * Activate the project with the given identifier.
	 * 
	 * @param projectId the project identifier
	 */
	public void deactivate(String projectId);

}
