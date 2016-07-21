/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.view.cache;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import de.fhg.igd.mapviewer.cache.FileTileCache;

/**
 * {@link FileTileCache} with a predefined cache dir
 * 
 * @author Simon Templer
 */
public class WorkspaceFileTileCache extends FileTileCache {

	/**
	 * Default constructor
	 */
	public WorkspaceFileTileCache() {
		super(getCacheDir());
	}

	private static File getCacheDir() {
		File dir = null;

		// determine main dir

		// instance location
		Location instanceLoc = Platform.getInstanceLocation();
		if (instanceLoc != null) {
			try {
				dir = new File(instanceLoc.getURL().toURI());
			} catch (Throwable e) {
				// ignore
			}
		}

		// temp dir
		if (dir == null) {
			String name = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
			dir = new File(name);
		}

		dir = new File(dir, "tilecache"); //$NON-NLS-1$

		return dir;
	}

}
