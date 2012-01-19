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

import eu.esdihumboldt.hale.common.core.io.IOProvider;

/**
 * Interface for {@link ProjectInfo} aware objects, e.g. {@link IOProvider}s
 * making use of project information.
 * @author Simon Templer
 */
public interface ProjectInfoAware {
	
	/**
	 * Set information about the current project.
	 * @param projectInfo the project information, may be <code>null</code> if
	 *   no project is available
	 */
	public void setProjectInfo(ProjectInfo projectInfo);

}
