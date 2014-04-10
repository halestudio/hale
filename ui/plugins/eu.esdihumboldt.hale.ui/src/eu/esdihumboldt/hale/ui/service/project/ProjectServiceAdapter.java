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

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;

/**
 * Adapter for {@link ProjectServiceListener}s
 * 
 * @author Simon Templer
 */
public class ProjectServiceAdapter implements ProjectServiceListener {

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectServiceListener#afterSave(eu.esdihumboldt.hale.ui.service.project.ProjectService)
	 */
	@Override
	public void afterSave(ProjectService projectService) {
		// override me
	}

	/**
	 * @see ProjectServiceListener#afterLoad(ProjectService)
	 */
	@Override
	public void afterLoad(ProjectService projectService) {
		// override me
	}

	/**
	 * @see ProjectServiceListener#onClean()
	 */
	@Override
	public void onClean() {
		// override me
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectServiceListener#onExportConfigurationChange()
	 */
	@Override
	public void onExportConfigurationChange() {
		// override me
	}

	@Override
	public void projectSettingChanged(String name, Value value) {
		// override me
	}

	@Override
	public void resourceAdded(String actionId, Resource resource) {
		// override me
	}

	@Override
	public void resourcesRemoved(String actionId, List<Resource> resources) {
		// override me
	}

	@Override
	public void projectInfoChanged(ProjectInfo info) {
		// override me
	}

}
