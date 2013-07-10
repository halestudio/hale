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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

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

	/**
	 * Extract a ZIP archive.
	 * 
	 * @param baseDir the base directory to extract to
	 * @param in the input stream of the ZIP archive, which is closed after
	 *            extraction
	 * @throws IOException if an error occurs
	 */
	public static void extract(File baseDir, InputStream in) throws IOException {
		final ZipInputStream zis = new ZipInputStream(in);
		try {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					final File file = new File(baseDir, entry.getName());
					Files.createParentDirs(file);
					Files.write(ByteStreams.toByteArray(zis), file);
				}
			}
		} finally {
			zis.close();
		}
	}

	/**
	 * Returns a relative path between basePath and targetPath if possible.
	 * 
	 * Source: http://stackoverflow.com/a/1288584
	 * 
	 * @param targetPath the target path
	 * @param basePath the base path
	 * @return a relative path from basePath to targetPath or the targetPath if
	 *         a relative path is not possible
	 */
	public static URI getRelativePath(URI targetPath, URI basePath) {
		// nothing to do if one path is opaque or not absolute
		if (!targetPath.isAbsolute() || !basePath.isAbsolute() || targetPath.isOpaque()
				|| basePath.isOpaque())
			return targetPath;
		// check scheme
		// XXX also check the other stuff (authority, host, port)?
		if (!targetPath.getScheme().equals(basePath.getScheme()))
			return targetPath;

		// We need the -1 argument to split to make sure we get a trailing
		// "" token if the base ends in the path separator and is therefore
		// a directory. We require directory paths to end in the path
		// separator -- otherwise they are indistinguishable from files.
		String[] base = basePath.getPath().split("/", -1);
		String[] target = targetPath.getPath().split("/", 0);

		// First get all the common elements. Store them as a string,
		// and also count how many of them there are.
		StringBuffer buf = new StringBuffer();
		int commonIndex = 0;
		for (int i = 0; i < target.length && i < base.length; i++) {

			if (target[i].equals(base[i])) {
				buf.append(target[i]).append("/");
				commonIndex++;
			}
			else
				break;
		}

		String common = buf.toString();

		if (commonIndex == 0) {
			// Whoops -- not even a single common path element. This most
			// likely indicates differing drive letters, like C: and D:.
			// These paths cannot be relativized. Return the target path.
			return targetPath;
			// This should never happen when all absolute paths
			// begin with / as in *nix.
		}

		String relative = "";
		if (base.length == commonIndex) {
			// Comment this out if you prefer that a relative path not start
			// with ./
//			relative = "./";
		}
		else {
			int numDirsUp = base.length - commonIndex - 1;
			// The number of directories we have to backtrack is the length of
			// the base path MINUS the number of common path elements, minus
			// one because the last element in the path isn't a directory.
			for (int i = 1; i <= (numDirsUp); i++) {
				relative += "../";
			}
		}
		relative += targetPath.getPath().substring(common.length());

		try {
			return new URI(null, null, relative, targetPath.getQuery(), targetPath.getFragment());
		} catch (URISyntaxException e) {
			return targetPath;
		}
	}
}
