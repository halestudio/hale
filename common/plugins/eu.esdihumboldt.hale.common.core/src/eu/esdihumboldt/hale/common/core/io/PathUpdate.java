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

package eu.esdihumboldt.hale.common.core.io;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.util.io.IOUtils;
import eu.esdihumboldt.util.resource.Resources;

/**
 * Updater class for a path.
 * 
 * @author Patrick Lieb
 * @author Kai Schwierczek
 */
public class PathUpdate {

	private static final ALogger log = ALoggerFactory.getLogger(PathUpdate.class);

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
	 * <li>if the URI isn't absolute:
	 * <ul>
	 * <li>if a new location is available it is resolved against that</li>
	 * <li>if an old location is available it is resolved against that</li>
	 * </ul>
	 * </li>
	 * <li>if the URI is absolute:
	 * <ul>
	 * <li>if an old and a new location is available it is transformed in the
	 * same way</li>
	 * <li>the URI is used as is</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * If none of the applicable cases results in a valid, existing URI and
	 * tryFallback is true {@link #updatePathFallback(URI)} is returned,
	 * otherwise <code>null</code> is returned.
	 * 
	 * @param uri the URI in question
	 * @param tryFallback whether to use {@link #updatePathFallback(URI)} in the
	 *            end or not
	 * @param allowResource whether to allow resolving through {@link Resources}
	 * @return a valid, existing URI or <code>null</code>
	 */
	public URI findLocation(URI uri, boolean tryFallback, boolean allowResource) {
		return findLocation(uri, tryFallback, allowResource, false);
	}

	/**
	 * Tries to find an existing readable URI.<br>
	 * <ul>
	 * <li>if the URI isn't absolute:
	 * <ul>
	 * <li>if a new location is available it is resolved against that</li>
	 * <li>if an old location is available it is resolved against that</li>
	 * </ul>
	 * </li>
	 * <li>if the URI is absolute:
	 * <ul>
	 * <li>if an old and a new location is available it is transformed in the
	 * same way</li>
	 * <li>the URI is used as is</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * If none of the applicable cases results in a valid, existing URI and
	 * tryFallback is true {@link #updatePathFallback(URI)} is returned,
	 * otherwise <code>null</code> is returned.
	 * 
	 * @param uri the URI in question
	 * @param tryFallback whether to use {@link #updatePathFallback(URI)} in the
	 *            end or not
	 * @param allowResource whether to allow resolving through {@link Resources}
	 * @param keepRelative If the URI is relative to the new location and
	 *            keepRelative is set, the URI is returned as is.<br>
	 *            Also, if the URI is relative to the old location and it is
	 *            possible to construct a relative path to the new location,
	 *            that is returned
	 * @return a valid, existing URI or <code>null</code>
	 */
	public URI findLocation(URI uri, boolean tryFallback, boolean allowResource,
			boolean keepRelative) {
		if ("jdbc".equals(uri.getScheme())) {
			// not possible to update JDBC URLs or test the stream
			return uri;
		}

		if (!uri.isAbsolute()) {
			if (newLocation != null) {
				URI newAbsolute = newLocation.resolve(uri);
				if (HaleIO.testStream(newAbsolute, allowResource)) {
					if (keepRelative) {
						return uri;
					}
					else {
						return newAbsolute;
					}
				}
				else {
					// Check if the resource file name needs
					// to be URL-encoded first (for project archives
					// that were created w/ hale studio 3.2.0 and before)
					String resourcePath = FilenameUtils.getPath(uri.toString());
					String resourceFileName = FilenameUtils.getName(uri.toString());
					try {
						String encodedPath = resourcePath
								+ URLEncoder.encode(resourceFileName, "UTF-8");
						URI encodedUri = URI.create(encodedPath);
						newAbsolute = newLocation.resolve(encodedUri);
						if (HaleIO.testStream(newAbsolute, allowResource)) {
							if (keepRelative) {
								return encodedUri;
							}
							else {
								return newAbsolute;
							}
						}
					} catch (UnsupportedEncodingException e) {
						log.debug(MessageFormat.format("Could not URL-encode \"{0}\"",
								resourceFileName), e);
					}
				}
			}
			if (oldLocation != null) {
				URI oldAbsolute = oldLocation.resolve(uri);
				if (HaleIO.testStream(oldAbsolute, allowResource)) {
					if (keepRelative)
						return IOUtils.getRelativePath(oldAbsolute, newLocation);
					else
						return oldAbsolute;
				}
			}
		}
		else {
			if (oldLocation != null && newLocation != null) {
				URI changed = changePath(uri);
				if (HaleIO.testStream(changed, allowResource))
					return changed;
			}
			if (HaleIO.testStream(uri, allowResource))
				return uri;
		}
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
		if (oldRaw == null || oldRaw.isEmpty()) {
			return oldSource;
		}
		else {
			if (oldSource.toString().startsWith(oldRaw)) {
				return URI.create(oldSource.toString().replace(oldRaw, newRaw));
			}
			else {
				// try to fix cases where oldRaw matches '<scheme>:///<rest>'
				// but oldSource matches '<scheme>:/<rest>' or vice versa
				try {
					URI oldRawUri = new URI(oldRaw);
					// URI.normalize() will not remove the additional slashes
					URI normalizedOldRaw = new URI(oldRawUri.getScheme(), oldRawUri.getHost(),
							oldRawUri.getPath(), oldRawUri.getQuery(), oldRawUri.getFragment());
					URI normalizedOldSource = new URI(oldSource.getScheme(), oldSource.getHost(),
							oldSource.getPath(), oldSource.getQuery(), oldSource.getFragment());

					return URI.create(normalizedOldSource.toString()
							.replace(normalizedOldRaw.toString(), newRaw));
				} catch (URISyntaxException e) {
					// tough luck
					return oldSource;
				}
			}
		}
	}

	// Analyzes the old and the new project path and tries to return the new one
	private void analysePaths(URI oldLocation, URI newLocation) {
		String o = oldLocation.toString();
		String n = newLocation.toString();

		// cut off file name. only look at the path to the files.
		int oindex = o.lastIndexOf('/');
		o = (oindex >= 0) ? (o.substring(0, oindex)) : "";
		int nindex = n.lastIndexOf('/');
		n = (nindex >= 0) ? (n.substring(0, nindex)) : "";

		int commonEndLength = 0;
		while (commonEndLength < o.length() && commonEndLength < n.length()
				&& o.charAt(o.length() - commonEndLength - 1) == n
						.charAt(n.length() - commonEndLength - 1)) {
			commonEndLength++;
		}
		oldRaw = o.substring(0, o.length() - commonEndLength);
		newRaw = n.substring(0, n.length() - commonEndLength);
	}

	/**
	 * @return the oldLocation, may be null
	 */
	public URI getOldLocation() {
		return oldLocation;
	}

	/**
	 * @return the newLocation, may be null
	 */
	public URI getNewLocation() {
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
