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
 * Service that provides the possibility to clean up application or project
 * related resources.
 * 
 * @author Simon Templer
 */
public interface CleanupService {

	/**
	 * Add a cleaner that shall perform a clean up of resources associated with
	 * the given context.
	 * 
	 * @param context the clean up context
	 * @param cleaner the cleaner performing the clean up
	 */
	public void addCleaner(CleanupContext context, Cleanup cleaner);

	/**
	 * Add temporary files associated with the given context, that should be
	 * deleted if the context is no longer valid.
	 * 
	 * @param context the clean up context
	 * @param files the temporary files
	 */
	public void addTemporaryFiles(CleanupContext context, File... files);

	/**
	 * Trigger clean up for the project context.
	 */
	public void triggerProjectCleanup();

}
