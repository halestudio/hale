/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.util;

import java.io.File;
import java.net.URI;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

/**
 * {@link Platform} utility methods.
 * 
 * @author Simon Templer
 */
public class PlatformUtil {

	/**
	 * Get the platform instance location folder.
	 * 
	 * @return the instance location folder or <code>null</code> if it cannot be
	 *         determined
	 */
	public static File getInstanceLocation() {
		Location location = Platform.getInstanceLocation();
		if (location != null) {
			try {
				return new File(URI.create(location.getURL().toString().replaceAll(" ", "%20")));
			} catch (Exception e) {
				// ignore
			}
		}

		return null;
	}

}
