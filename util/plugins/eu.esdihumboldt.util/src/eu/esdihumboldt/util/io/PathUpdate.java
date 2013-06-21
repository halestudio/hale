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

import eu.esdihumboldt.util.resource.Resources;

/**
 * Updater class for a path.
 * 
 * @author Patrick Lieb
 * @author Kai Schwierczek
 */
public class PathUpdate {

	private final URI oldLocation;
	private final URI newLocation;
	private String oldRaw;
	private String newRaw;

	/**
	 * Create a path updater based on a pair of known old and new locations.
	 * 
	 * @param oldLocation the old location of a file, may be null
	 * @param newLocation the new location of the same file (though the file
	 *            name may be different), may be null
	 */
	public PathUpdate(URI oldLocation, URI newLocation) {
		super();
		this.oldLocation = oldLocation;
		this.newLocation = newLocation;
		/*
		 * analyze paths (w/o file name) of both URIs to find out which of the
		 * later parts are equal, to determine which part of the old location
		 * has to be replaced by which part of the new location for other files
		 * that have been moved in a similar way to the analyzed file.
		 */
		if (oldLocation != null && newLocation != null && !oldLocation.equals(newLocation))
			analysePaths(oldLocation, newLocation);
	}

	/**
	 * Tries to find an existing readable URI.<br>
	 * <ul>
	 * <li>Tries the given URI directly</li>
	 * <li>If the URI isn't absolute and an old location is available it is
	 * resolved against that</li>
	 * <li>If a old and new location is given the URI is transformed in the same
	 * way</li>
	 * </ul>
	 * If none of that results in a valid, existing URI and tryFallback is true
	 * {@link #updatePathFallback(URI)} is returned, otherwise <code>null</code>
	 * is returned.
	 * 
	 * @param uri the URI in question
	 * @param tryFallback whether to use {@link #updatePathFallback(URI)} in the
	 *            end or not
	 * @param allowResource whether to allow resolving through {@link Resources}
	 * @return a valid, existing URI or <code>null</code>
	 */
	public URI findLocation(URI uri, boolean tryFallback, boolean allowResource) {
		if (IOUtils.testStream(uri, allowResource))
			return uri;
		URI absolute = resolveRelative(uri);
		if (!absolute.equals(uri) && IOUtils.testStream(absolute, allowResource))
			return absolute;
		URI changed = changePath(absolute);
		if (!changed.equals(absolute) && IOUtils.testStream(changed, allowResource))
			return changed;
		if (tryFallback)
			return updatePathFallback(uri);
		else
			return null;
	}

	/**
	 * Create an alternative path for the given location if it matches changes
	 * from old to new location. If either old or new location is null, or the
	 * given URI wasn't changed in the same way, this method has no effect.
	 * 
	 * @param oldSource path where the file was saved to
	 * @return the new URI
	 */
	public URI changePath(URI oldSource) {
		if (oldRaw != null)
			return URI.create(oldSource.toString().replace(oldRaw, newRaw));
		else
			return oldSource;
	}

	// Analyzes the old and the new project path and tries to return the new one
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

	/**
	 * If the given URI isn't absolute and an old location is available, it is
	 * resolved against the old location. If the result cannot be found it
	 * afterwards can be used with {@link #changePath(URI)}.
	 * 
	 * @param uri the URI to resolve
	 * @return the resolved URI or the original URI, if it was absolute already
	 *         or no location is available to resolve the URI against
	 */
	public URI resolveRelative(URI uri) {
		if (!uri.isAbsolute() && oldLocation != null)
			return oldLocation.resolve(uri);
		else
			return uri;
	}

	/**
	 * @return the oldLocation, may be null
	 */
	protected URI getOldLocation() {
		return oldLocation;
	}

	/**
	 * @return the newLocation, may be null
	 */
	protected URI getNewLocation() {
		return newLocation;
	}

	/**
	 * Update the path to a resource if automatic update fails. The default
	 * implementation returns <code>null</code>, which means the location is not
	 * updated.
	 * 
	 * @param oldLocation the old resource location
	 * @return the replacement resource location or <code>null</code>
	 */
	protected URI updatePathFallback(URI oldLocation) {
		return null;
	}
}
