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

package eu.esdihumboldt.hale.common.core.io.project.impl;

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

/**
 * Abstract project reader with information on project and projectfiles
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractProjectReader extends AbstractImportProvider implements ProjectReader {

	/**
	 * The additional project files, file names are mapped to project file
	 * objects
	 */
	private Map<String, ProjectFile> projectFiles;

	/**
	 * The main project file, <code>null</code> if not yet loaded
	 */
	private Project project = null;

	/**
	 * @see ProjectReader#setProjectFiles(Map)
	 */
	@Override
	public void setProjectFiles(Map<String, ProjectFile> projectFiles) {
		this.projectFiles = projectFiles;
	}

	/**
	 * @see ProjectReader#getProjectFiles()
	 */
	@Override
	public Map<String, ProjectFile> getProjectFiles() {
		return projectFiles;
	}

	/**
	 * @see ProjectReader#getProject()
	 */
	@Override
	public Project getProject() {
		return project;
	}

	/**
	 * @param project set the current project
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		// TODO change?
		return false;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return ProjectIO.PROJECT_TYPE_NAME;
	}

}
