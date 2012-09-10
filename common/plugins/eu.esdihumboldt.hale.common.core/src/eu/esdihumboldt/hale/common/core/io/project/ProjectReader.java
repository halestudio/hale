/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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