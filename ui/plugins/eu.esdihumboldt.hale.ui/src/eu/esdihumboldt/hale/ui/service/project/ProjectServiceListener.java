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

package eu.esdihumboldt.hale.ui.service.project;

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

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
	 * Called after a project was loaded.
	 * 
	 * @param projectService the calling project service
	 * @param projectFiles the additional project files that were loaded,
	 *            listeners may use them to update their state
	 */
	public void afterLoad(ProjectService projectService, Map<String, ProjectFile> projectFiles);

	/**
	 * Called when the project is cleaned.
	 */
	public void onClean();

}
