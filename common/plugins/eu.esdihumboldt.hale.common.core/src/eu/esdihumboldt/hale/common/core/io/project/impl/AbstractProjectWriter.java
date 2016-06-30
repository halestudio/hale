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

import java.net.URI;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

/**
 * Abstract project writer with information on project and projectfiles
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractProjectWriter extends AbstractExportProvider
		implements ProjectWriter {

	/**
	 * The additional project files, file names are mapped to project file
	 * objects
	 */
	private Map<String, ProjectFile> projectFiles;

	/**
	 * The main project file
	 */
	private Project project;

	private URI previousTarget;

	/**
	 * @see ProjectWriter#setProjectFiles(Map)
	 */
	@Override
	public void setProjectFiles(Map<String, ProjectFile> projectFiles) {
		this.projectFiles = projectFiles;
	}

	/**
	 * @return the additional project files of the project
	 */
	public Map<String, ProjectFile> getProjectFiles() {
		return projectFiles;
	}

	/**
	 * @see ProjectWriter#setProject(Project)
	 */
	@Override
	public void setProject(Project project) {
		this.project = project;

		// ensure a HALE version is set
		if (this.project.getHaleVersion() == null) {
			this.project.setHaleVersion(HalePlatform.getCoreVersion());
		}
	}

	/**
	 * @see ProjectWriter#getProject()
	 */
	@Override
	public Project getProject() {
		return project;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return ProjectIO.PROJECT_TYPE_NAME;
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see ProjectWriter#setPreviousTarget(URI)
	 */
	@Override
	public void setPreviousTarget(URI previousTarget) {
		this.previousTarget = previousTarget;
	}

	/**
	 * Returns the previous target of the project. May be <code>null</code>.
	 * 
	 * @return the previous target of the project. May be <code>null</code>
	 */
	protected URI getPreviousTarget() {
		return previousTarget;
	}

}
