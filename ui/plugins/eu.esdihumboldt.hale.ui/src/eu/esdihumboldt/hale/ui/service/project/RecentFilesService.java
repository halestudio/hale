/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.service.project;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IMemento;

/**
 * Service that manages recent projects
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface RecentFilesService {

	/**
	 * Add a file.
	 * 
	 * @param file the file name
	 * @param projectName the project name
	 */
	public abstract void add(String file, String projectName);

	/**
	 * A recent files entry.
	 * 
	 * @author Kai Schwierczek
	 */
	interface Entry {

		/**
		 * Get the file name.
		 * 
		 * @return the file name
		 */
		String getFile();

		/**
		 * Get the project name.
		 * 
		 * @return the project name
		 */
		String getProjectName();
	}

	/**
	 * Get the recent files
	 * 
	 * @return the recent files
	 */
	public abstract Entry[] getRecentFiles();

	/**
	 * Restore the state
	 * 
	 * @param memento the memento
	 * @return the status
	 */
	public abstract IStatus restoreState(IMemento memento);

	/**
	 * Save the state
	 * 
	 * @param memento the memento
	 * @return the status
	 */
	public abstract IStatus saveState(IMemento memento);

}