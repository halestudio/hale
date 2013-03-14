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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.project;

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

/**
 * Listens for {@link ProjectService} events, e.g. the loading and saving of a
 * project.
 * 
 * @author Simon Templer
 */
public interface ProjectServiceListener {

	/**
	 * Called before a project is saved.
	 * 
	 * @param projectService the calling project service
	 * @param projectFiles the map of additional project files, listeners may
	 *            add additional files to the map
	 */
	public void beforeSave(ProjectService projectService, Map<String, ProjectFile> projectFiles);

	/**
	 * Called after a project was loaded.
	 * 
	 * @param projectService the calling project service
	 * @param projectFiles the additional project files that were loaded,
	 *            listeners may use them to update their state
	 */
	public void afterLoad(ProjectService projectService, Map<String, ProjectFile> projectFiles);

	/**
	 * Called when the project is cleaned.
	 */
	public void onClean();

	/**
	 * Called after project has changed
	 */
	public void afterChange();

}
