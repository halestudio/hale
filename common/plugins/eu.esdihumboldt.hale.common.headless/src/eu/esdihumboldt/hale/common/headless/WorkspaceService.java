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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless;

import java.io.File;
import java.io.FileNotFoundException;

import org.joda.time.ReadableDuration;

/**
 * Service for acquiring workspace folders that can be used to store files
 * related to a certain process, e.g. a transformation.
 * 
 * @author Simon Templer
 */
public interface WorkspaceService {

	/**
	 * Lease a workspace folder for the given duration. After the duration has
	 * passed, the folder is deleted by the service (the exact point in time
	 * depending on when the service is triggered).
	 * 
	 * @param duration the lease duration
	 * @return the workspace identifier
	 */
	public String leaseWorkspace(ReadableDuration duration);

	/**
	 * Get the workspace folder for the given workspace identifier.
	 * 
	 * @param id the workspace identifier
	 * @return the workspace folder
	 * @throws FileNotFoundException if the workspace does not exist
	 */
	public File getWorkspaceFolder(String id) throws FileNotFoundException;

	/**
	 * Delete the workspace with the given identifier.
	 * 
	 * @param id the workspace identifier
	 */
	public void deleteWorkspace(String id);

}
