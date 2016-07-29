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
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jdesktop.swingx.mapviewer.AbstractBufferedImageTileCache;
import org.jdesktop.swingx.mapviewer.TileInfo;

/**
 * Cache using soft references to images
 * 
 * @author Simon Templer
 */
public class SoftTileCache extends AbstractBufferedImageTileCache {

	private final Map<URI, SoftReference<BufferedImage>> images = new HashMap<URI, SoftReference<BufferedImage>>();

	/**
	 * @see AbstractBufferedImageTileCache#doPut(TileInfo, BufferedImage)
	 */
	@Override
	protected void doPut(TileInfo tile, BufferedImage image) {
		synchronized (images) {
			images.put(tile.getIdentifier(), new SoftReference<BufferedImage>(image));
		}
	}

	/**
	 * @see AbstractBufferedImageTileCache#doGet(TileInfo)
	 */
	@Override
	protected BufferedImage doGet(TileInfo tile) {
		SoftReference<BufferedImage> ref;
		synchronized (images) {
			ref = images.get(tile.getIdentifier());
		}

		if (ref != null) {
			return ref.get();
		}

		return null;
	}

}
