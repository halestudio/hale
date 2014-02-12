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

import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;

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
	 * Called after a project is saved.
	 * 
	 * @param projectService the calling project service
	 */
	public void afterSave(ProjectService projectService);

	/**
	 * Called after a project was loaded.
	 * 
	 * @param projectService the calling project service
	 * @param projectFiles the additional project files that were loaded,
	 *            listeners may use them to update their state
	 */
	public void afterLoad(ProjectService projectService, Map<String, ProjectFile> projectFiles);

	/**
	 * Called after a new resource was added.
	 * 
	 * @param actionId the action the resource is associated to
	 * @param resource the added resource
	 */
	public void resourceAdded(String actionId, Resource resource);

	/**
	 * Called when resources for an action have been removed.
	 * 
	 * @param actionId the action identifier
	 * @param resources the removed resources
	 */
	public void resourcesRemoved(String actionId, List<Resource> resources);

	/**
	 * Called when the project information has been changed.
	 * 
	 * @param info the updated project information
	 */
	public void projectInfoChanged(ProjectInfo info);

	/**
	 * Called when a project setting has been changed. Will not be called when
	 * the project has been loaded and the settings changed in consequence.
	 * 
	 * @param name the configuration key
	 * @param value the new value
	 */
	public void projectSettingChanged(String name, Value value);

	/**
	 * Called when the project is cleaned.
	 */
	public void onClean();

	/**
	 * Called after project has changed
	 */
	public void onExportConfigurationChange();

}
