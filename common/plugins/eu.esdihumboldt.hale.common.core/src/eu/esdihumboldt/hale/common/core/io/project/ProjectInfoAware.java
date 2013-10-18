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

package eu.esdihumboldt.hale.common.core.io.project;

import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.IOProvider;

/**
 * Interface for {@link ProjectInfo} aware objects, e.g. {@link IOProvider}s
 * making use of project information.
 * 
 * @author Simon Templer
 */
public interface ProjectInfoAware {

	/**
	 * Set information about the current project.
	 * 
	 * @param projectInfo the project information, may be <code>null</code> if
	 *            no project is available
	 */
	public void setProjectInfo(ProjectInfo projectInfo);

	/**
	 * Set the location the project was loaded from.
	 * 
	 * @param location the project location or <code>null</code> if it was not
	 *            saved yet
	 */
	public void setProjectLocation(URI location);

}
