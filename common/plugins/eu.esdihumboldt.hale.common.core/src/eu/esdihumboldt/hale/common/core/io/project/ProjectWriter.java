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

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

/**
 * Provides support for saving projects
 * 
 * @author Simon Templer
 */
public interface ProjectWriter extends ExportProvider {

	/**
	 * Set the additional project files to write.
	 * 
	 * @param projectFiles the project files to write (file name mapped to
	 *            project file)
	 */
	public void setProjectFiles(Map<String, ProjectFile> projectFiles);

	/**
	 * Set the main project file.
	 * 
	 * @param project the main project file to write
	 */
	public void setProject(Project project);

	/**
	 * Get the main project file to be adapted before saving it.
	 * 
	 * @return the main project file that is to be written
	 */
	public Project getProject();

}