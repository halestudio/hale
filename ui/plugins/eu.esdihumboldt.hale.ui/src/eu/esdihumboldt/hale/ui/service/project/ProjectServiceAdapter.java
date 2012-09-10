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

}
