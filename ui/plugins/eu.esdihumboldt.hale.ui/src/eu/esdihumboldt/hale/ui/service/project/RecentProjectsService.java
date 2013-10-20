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

package eu.esdihumboldt.hale.ui.service.project;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IMemento;

/**
 * Service that manages recent projects
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface RecentProjectsService {

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
