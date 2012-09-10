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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.project.extension.ProjectFileExtension;
import eu.esdihumboldt.hale.common.core.io.project.extension.ProjectFileFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

/**
 * Common utilities and constants regarding project I/O
 * 
 * @author Simon Templer
 */
public abstract class ProjectIO {

	/**
	 * Project file default type name
	 */
	public static final String PROJECT_TYPE_NAME = "HALE project";

	/**
	 * Name of the internal project file
	 */
	public static final String PROJECT_FILE = "project.xml";

	/**
	 * Create a set of default project files for use with {@link ProjectReader}
	 * and {@link ProjectWriter}
	 * 
	 * @return the default project files
	 */
	public static Map<String, ProjectFile> createDefaultProjectFiles() {
		Map<String, ProjectFile> result = new HashMap<String, ProjectFile>();

		Collection<ProjectFileFactory> elements = ProjectFileExtension.getInstance().getElements();
		for (ProjectFileFactory element : elements) {
			result.put(element.getId(), element.createProjectFile());
		}

		return result;
	}

}
