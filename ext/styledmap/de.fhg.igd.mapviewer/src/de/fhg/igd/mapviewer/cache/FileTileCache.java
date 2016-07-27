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

package de.fhg.igd.mapviewer.cache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.jdesktop.swingx.mapviewer.AbstractBufferedImageTileCache;
import org.jdesktop.swingx.mapviewer.TileInfo;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * {@link SoftTileCache} that additionally stores remote images on the local
 * file system
 * 
 * @author Simon Templer
 */
public class FileTileCache extends SoftTileCache {

	private static final ALogger log = ALoggerFactory.getLogger(FileTileCache.class);

	private final File cacheDir;

	/**
	 * Constructor
	 * 
	 * @param cacheDir the cache directory
	 */
	public FileTileCache(File cacheDir) {
		super();
		this.cacheDir = cacheDir;

		log.info("Create file tile cache with cache directory " + cacheDir.getAbsolutePath()); //$NON-NLS-1$
	}

	/**
	 * @see SoftTileCache#doGet(TileInfo)
	 */
	@Override
	protected BufferedImage doGet(TileInfo tile) {
		BufferedImage img = super.doGet(tile);

		if (img == null && useFileCacheFor(tile.getIdentifier())) {
			// try loading the image from local file cache
			File file = getLocalFile(tile);
			if (file.exists()) {
				try {
					img = load(tile, new FileInputStream(file));
				} catch (FileNotFoundException e) {
					// ignore
				} /*
					 * catch (IOException e) { log.error(
					 * "Error loading local cache file: " +
					 * file.getAbsolutePath(), e); //$NON-NLS-1$ }
					 */
			}
		}

		return img;
	}

	/**
	 * @see AbstractBufferedImageTileCache#load(TileInfo)
	 */
	@Override
	protected BufferedImage load(TileInfo tile) {
		try {
			return load(tile, true);
		} catch (Exception e) {
			log.error("Error loading tile", e);
			return null;
		}
	}

	private BufferedImage load(TileInfo tile, boolean cache) throws IOException {
		InputStream in = null;
		if (cache && useFileCacheFor(tile.getIdentifier())) {
			// copy to local file, load from there
			File local = getLocalFile(tile);
			if (local != null) {
				// copy stream
				local.getParentFile().mkdirs();
				local.createNewFile();

				InputStream remote = openInputStream(tile.getURI());
				FileOutputStream out = new FileOutputStream(local);
				try {
					IOUtils.copy(remote, out);
				} finally {
					out.close();
					remote.close();
				}

				// use local file as new source
				in = new FileInputStream(local);
			}
		}

		if (in == null) {
			in = openInputStream(tile.getURI());
		}

		// super.load closes in
		return super.load(tile, in);
	}

	/**
	 * Get the local cache file name for the given URI
	 * 
	 * @param tile the tile info
	 * 
	 * @return the local file or <code>null</code>
	 */
	protected File getLocalFile(TileInfo tile) {
		URI uri = tile.getIdentifier();
		File dir = new File(cacheDir, uri.getHost());
		dir = new File(dir, "level-" + tile.getZoom()); //$NON-NLS-1$
		try {
			String mapKey = computeHash(uri.toString());
			return new File(dir, tile.getX() + "_" + tile.getY() + "_" + mapKey); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Throwable e) {
			log.error("Error determinating local file name for caching", e); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * Compute a secure hash for the given string
	 * 
	 * @param password the string
	 * 
	 * @return the hash of the string as HEX string
	 * @throws NoSuchAlgorithmException if the SHA-1 algorithm could not be
	 *             found
	 */
	protected String computeHash(String password) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(password.getBytes());
		return byteArrayToHexString(digest.digest());
	}

	/**
	 * Convert a byte array to a HEX string
	 * 
	 * @param bytes the byte array
	 * 
	 * @return the bytes as HEX string
	 */
	private String byteArrayToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * Determines if for a given URI the local file cache shall be used
	 * 
	 * @param uri the URI
	 * 
	 * @return if the file cache shall be used
	 */
	protected boolean useFileCacheFor(URI uri) {
		String scheme = uri.getScheme();
		return scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
