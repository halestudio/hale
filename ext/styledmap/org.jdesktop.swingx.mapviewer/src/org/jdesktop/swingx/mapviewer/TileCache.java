package org.jdesktop.swingx.mapviewer;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Tile cache interface.
 * 
 * @author Simon Templer
 */
public interface TileCache {

	/**
	 * Returns a buffered image for the requested URI from the cache. This
	 * method must return null if the image is not in the cache. If the image is
	 * unavailable but it's compressed version *is* available, then the
	 * compressed version will be expanded and returned.
	 * 
	 * @param tile the tile info
	 * @return the image matching the requested URI, or null if not available
	 * @throws java.io.IOException
	 */
	@SuppressWarnings("javadoc")
	public abstract BufferedImage get(TileInfo tile) throws IOException;

	/**
	 * Request that the cache free up some memory. How this happens or how much
	 * memory is freed is up to the TileCache implementation. Subclasses can
	 * implement their own strategy. The default strategy is to clear out all
	 * buffered images but retain the compressed versions.
	 */
	public abstract void clear();

}