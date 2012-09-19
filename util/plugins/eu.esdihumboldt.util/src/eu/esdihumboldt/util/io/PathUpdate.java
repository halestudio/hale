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

import java.net.URI;

/**
 * Updater class for a path.
 * 
 * @author Patrick Lieb
 * @author Kai Schwierczek
 */
public class PathUpdate {

	private String oldRaw;
	private String newRaw;

	/**
	 * Create a path updater based on a pair of known old and new locations
	 * 
	 * @param oldLocation the old location of a file
	 * @param newLocation the new location of the same file (though the file
	 *            name may be different)
	 */
	public PathUpdate(URI oldLocation, URI newLocation) {
		super();
		/*
		 * analyze paths (w/o file name) of both URIs to find out which of the
		 * later parts are equal, to determine which part of the old location
		 * has to be replaced by which part of the new location for other files
		 * that have been moved in a similar way to the analyzed file.
		 */
		analysePaths(oldLocation, newLocation);
	}

	/**
	 * Create an alternative path for the given location if the corresponding
	 * file has been moved along to the project file.
	 * 
	 * @param oldSource path where the file was saved to
	 * @return the new URI
	 */
	public URI changePath(URI oldSource) {
		return URI.create(oldSource.toString().replace(oldRaw, newRaw));
	}

	// Analyses the old and the new project path and tries to return the new one
	private void analysePaths(URI oldLocation, URI newLocation) {
		String o = oldLocation.toString();
		String n = newLocation.toString();

		// cut off file name. only look at the path to the files.
		o = o.substring(0, o.lastIndexOf('/'));
		n = n.substring(0, n.lastIndexOf('/'));

		int commonEndLength = 0;
		while (commonEndLength < o.length()
				&& commonEndLength < n.length()
				&& o.charAt(o.length() - commonEndLength - 1) == n.charAt(n.length()
						- commonEndLength - 1)) {
			commonEndLength++;
		}
		oldRaw = o.substring(0, o.length() - commonEndLength);
		newRaw = n.substring(0, n.length() - commonEndLength);
	}
}
