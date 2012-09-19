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

package eu.esdihumboldt.hale.common.core.io.project;

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

/**
 * Provides support for loading projects
 * 
 * @author Simon Templer
 */
public interface ProjectReader extends ImportProvider {

	/**
	 * Set the project files to read if applicable.
	 * 
	 * @param projectFiles the project files to read (file name mapped to
	 *            project file)
	 */
	public void setProjectFiles(Map<String, ProjectFile> projectFiles);

	/**
	 * Get the additional project files
	 * 
	 * @return the project files (file name mapped to project file)
	 */
	public Map<String, ProjectFile> getProjectFiles();

	/**
	 * Get the loaded main project
	 * 
	 * @return the project or <code>null</code> if it has not been loaded
	 */
	public Project getProject();

}
