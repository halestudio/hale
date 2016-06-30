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

import java.text.MessageFormat;
import java.util.Map;

import org.osgi.framework.Version;

import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;

/**
 * Abstract project reader with information on project and projectfiles
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractProjectReader extends AbstractImportProvider
		implements ProjectReader {

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
	 * Set the loaded project and do a version check.
	 * 
	 * @param project the project to set
	 * @param reporter the reporter
	 */
	public void setProjectChecked(Project project, IOReporter reporter) {
		this.project = project;

		// check version
		Version projectVersion = stripQualifier(project.getHaleVersion());
		if (projectVersion != null) {
			Version haleVersion = stripQualifier(HalePlatform.getCoreVersion());
			if (haleVersion.compareTo(projectVersion) < 0) {
				// project is newer than HALE
				reporter.warn(new IOMessageImpl(MessageFormat.format(
						"The version of HALE the loaded project was created with ({1}) is newer than this version of HALE ({0}). Consider updating to avoid possible information loss or unexpected behavior.",
						haleVersion, projectVersion), null));
			}
		}
	}

	private Version stripQualifier(Version v) {
		if (v != null) {
			return new Version(v.getMajor(), v.getMinor(), v.getMicro());
		}

		return null;
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
