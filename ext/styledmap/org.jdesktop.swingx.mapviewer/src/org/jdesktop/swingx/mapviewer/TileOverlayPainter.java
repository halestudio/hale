package org.jdesktop.swingx.mapviewer;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Tile overlay painter.
 * 
 * @author Simon Templer
 */
public interface TileOverlayPainter extends Comparable<TileOverlayPainter> {

	/**
	 * Set the current tile provider
	 * 
	 * @param tiles the tile provider
	 */
	public void setTileProvider(TileProvider tiles);

	/**
	 * Paint a tile overlay. The parameters x, y and zoom can be used for
	 * caching.
	 * 
	 * @param gfx the graphics device to paint on, its origin is at the upper
	 *            left corner of the tile
	 * @param x the tile x number
	 * @param y the tile y number
	 * @param zoom the zoom level
	 * @param tilePosX the tile x position in pixel
	 * @param tilePosY the tile y position in pixel
	 * @param tileWidth the tile width
	 * @param tileHeight the tile height
	 * @param converter the pixel converter
	 * @param viewportBounds the view-port bounds
	 */
	public void paintTile(Graphics2D gfx, int x, int y, int zoom, int tilePosX, int tilePosY,
			int tileWidth, int tileHeight, PixelConverter converter, Rectangle viewportBounds);

	/**
	 * Perform clean-up
	 */
	public void dispose();

}
