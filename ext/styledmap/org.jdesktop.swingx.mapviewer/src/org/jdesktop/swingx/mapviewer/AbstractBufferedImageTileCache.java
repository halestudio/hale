package org.jdesktop.swingx.mapviewer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

/**
 * AbstractBufferedImageTileCache
 * 
 * @author Simon Templer
 */
public abstract class AbstractBufferedImageTileCache extends AbstractTileCache {

	private static final Log log = LogFactory.getLog(AbstractBufferedImageTileCache.class);

	/**
	 * @see AbstractTileCache#load(TileInfo)
	 */
	@Override
	protected BufferedImage load(TileInfo tile) {
		try {
			return load(tile, openInputStream(tile.getURI()));
		} catch (Exception e) {
			log.error("Error loading tile", e);
			return null;
		}
	}

	/**
	 * Load a tile from the given input stream
	 * 
	 * @param tile the tile
	 * @param in the input stream
	 * 
	 * @return the image that was loaded a put into the cache, may be
	 *         <code>null</code>
	 */
	protected BufferedImage load(TileInfo tile, InputStream in) {
		try {
			try {
				BufferedImage img = GraphicsUtilities.loadCompatibleImage(in);
				if (img != null) {
					doPut(tile, img);
				}
				return img;
			} finally {
				in.close();
			}
		} catch (IOException e) {
			log.error("Error loading tile", e);
			return null;
		}
	}

	/**
	 * @see TileCache#get(TileInfo)
	 */
	@Override
	public BufferedImage get(TileInfo tile) throws IOException {
		BufferedImage img = doGet(tile);
		if (img == null) {
			img = load(tile);
		}
		return img;
	}

	/**
	 * Get the image from the cache
	 * 
	 * @param tile the tile info
	 * 
	 * @return the cached image or <code>null</code>
	 */
	protected abstract BufferedImage doGet(TileInfo tile);

	/**
	 * Put an tile image into the cache
	 * 
	 * @param tile the tile info
	 * @param image the tile image
	 */
	protected abstract void doPut(TileInfo tile, BufferedImage image);

}
