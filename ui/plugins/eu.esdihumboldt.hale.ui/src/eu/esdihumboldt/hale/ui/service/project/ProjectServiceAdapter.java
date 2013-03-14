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
 * Adapter for {@link ProjectServiceListener}s
 * 
 * @author Simon Templer
 */
public class ProjectServiceAdapter implements ProjectServiceListener {

	/**
	 * @see ProjectServiceListener#beforeSave(ProjectService, Map)
	 */
	@Override
	public void beforeSave(ProjectService projectService, Map<String, ProjectFile> projectFiles) {
		// override me
	}

	/**
	 * @see ProjectServiceListener#afterLoad(ProjectService, Map)
	 */
	@Override
	public void afterLoad(ProjectService projectService, Map<String, ProjectFile> projectFiles) {
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

}
