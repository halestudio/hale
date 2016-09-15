/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.project;

import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Service providing project information.
 * 
 * @author Simon Templer
 */
public interface ProjectInfoService {

	/**
	 * Get general information about the current project
	 * 
	 * @return the project info
	 */
	public ProjectInfo getProjectInfo();

	/**
	 * Get the property value for the project property with the given name.
	 * 
	 * @param name the property name
	 * @return the property value, may be a null value but not <code>null</code>
	 */
	public Value getProperty(String name);

}
