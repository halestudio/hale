/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.service.cleanup;

import java.io.File;

/**
 * Exposes associated temporary files for external clean up.
 * 
 * @author Simon Templer
 */
public interface TemporaryFiles {

	/**
	 * Get the temporary files that should be removed when no longer needed
	 * 
	 * @return the temporary files
	 */
	public Iterable<File> getTemporaryFiles();

}
