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

package eu.esdihumboldt.hale.ui.service.project.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceListener;

/**
 * Abstract base implementation for project services
 * 
 * @author Simon Templer
 */
public abstract class AbstractProjectService implements ProjectService {

	private final CopyOnWriteArraySet<ProjectServiceListener> listeners = new CopyOnWriteArraySet<ProjectServiceListener>();

	/**
	 * @see ProjectService#addListener(ProjectServiceListener)
	 */
	@Override
	public void addListener(ProjectServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see ProjectService#removeListener(ProjectServiceListener)
	 */
	@Override
	public void removeListener(ProjectServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Call before a project is saved.
	 * 
	 * @param projectFiles the map of additional project files, listeners may
	 *            add additional files to the map
	 */
	protected void notifyBeforeSave(Map<String, ProjectFile> projectFiles) {
		for (ProjectServiceListener listener : listeners) {
			listener.beforeSave(this, projectFiles);
		}
	}

	/**
	 * Call after a project was loaded.
	 * 
	 * @param projectFiles the additional project files that were loaded,
	 *            listeners may use them to update their state
	 */
	protected void notifyAfterLoad(Map<String, ProjectFile> projectFiles) {
		for (ProjectServiceListener listener : listeners) {
			listener.afterLoad(this, projectFiles);
		}
	}

	/**
	 * Call after a new resource was added.
	 * 
	 * @param actionId the action the resource is associated to
	 * @param resource the added resource
	 */
	protected void notifyResourceAdded(String actionId, Resource resource) {
		for (ProjectServiceListener listener : listeners) {
			listener.resourceAdded(actionId, resource);
		}
	}

	/**
	 * Call when resources for an action have been removed.
	 * 
	 * @param actionId the action identifier
	 * @param resources the removed resources
	 */
	protected void notifyResourcesRemoved(String actionId, List<Resource> resources) {
		for (ProjectServiceListener listener : listeners) {
			listener.resourcesRemoved(actionId, resources);
		}
	}

	/**
	 * Call when the project is cleaned.
	 */
	protected void notifyClean() {
		for (ProjectServiceListener listener : listeners) {
			listener.onClean();
		}
	}

	/**
	 * Called when the export configurations have changed
	 */
	protected void notifyExportConfigurationChanged() {
		for (ProjectServiceListener listener : listeners) {
			listener.onExportConfigurationChange();
		}
	}

	/**
	 * Called when the project information has been changed.
	 * 
	 * @param info the updated project information
	 */
	public void notifyProjectInfoChanged(ProjectInfo info) {
		for (ProjectServiceListener listener : listeners) {
			listener.projectInfoChanged(info);
		}
	}

}
