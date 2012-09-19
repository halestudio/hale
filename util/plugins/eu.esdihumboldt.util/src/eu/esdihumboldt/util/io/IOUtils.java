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

package eu.esdihumboldt.util.io;

import java.io.File;
import java.net.URI;

import eu.esdihumboldt.util.resource.Resources;

/**
 * Helper class for IO
 * 
 * @author Kai Schwierczek
 */
public final class IOUtils {

	/**
	 * Static class, constructor private.
	 */
	private IOUtils() {
	}

	/**
	 * Tests whether a InputStream to the given URI can be opened. <br>
	 * In case of a file it instead tests File.isFile and File.canRead() because
	 * it is a lot faster.
	 * 
	 * @param uri the URI to test
	 * @param allowResource allow resolving through {@link Resources}
	 * @return true, if a InputStream to the URI could be opened.
	 */
	public static boolean testStream(URI uri, boolean allowResource) {
		if ("file".equalsIgnoreCase(uri.getScheme())) {
			File file = new File(uri);
			if (file.isFile() && file.canRead())
				return true;
			return false;
		}

		// try resolving through local resources
		if (allowResource && Resources.tryResolve(uri, null) != null) {
			return true;
		}

		// could be further enhanced to check for example for http response
		// codes like 404.
		try {
			uri.toURL().openConnection().getInputStream().close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
