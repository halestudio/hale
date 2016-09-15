/*+-------------+----------------------------------------------------------*
 *|  |  |_|_|_|_|   Fraunhofer-Institut fuer Graphische Datenverarbeitung  *
 *|__|__|_|_|_|_|     (Fraunhofer Institute for Computer Graphics)         *
 *|  |  |_|_|_|_|                                                          *
 *|__|__|_|_|_|_|                                                          *
 *|  __ |    ___|                                                          *
 *| /_  /_  / _ |     Fraunhoferstrasse 5                                  *
 *|/   / / /__/ |     D-64283 Darmstadt, Germany                           *
 *+-------------+----------------------------------------------------------*/
package org.jdesktop.swingx.mapviewer;

import java.net.URI;

import org.jdesktop.swingx.painter.Painter;

/**
 * Provides map information and can generate tile urls. Combines information
 * contained in former TileFactory and TileFactoryInfo classes.
 * 
 * @author Simon Templer
 */
public interface TileProvider {

	/**
	 * Gets the size of an edge of a tile in pixels at the given zoom level.
	 * Tiles must be square.
	 * 
	 * @param zoom the zoom level
	 * @return the size of an edge of a tile in pixels
	 */
	// public int getTileSize(int zoom);

	/**
	 * Gets the width of a tile in pixels at the given zoom level.
	 * 
	 * @param zoom the zoom level
	 * @return the width of a tile in pixels
	 */
	public int getTileWidth(int zoom);

	/**
	 * Gets the height of a tile in pixels at the given zoom level.
	 * 
	 * @param zoom the zoom level
	 * @return the height of a tile in pixels
	 */
	public int getTileHeight(int zoom);

	/**
	 * Get the PixelConverter that converts between map pixels and
	 * {@link GeoPosition}s
	 * 
	 * @return the PixelConverter
	 */
	public PixelConverter getConverter();

	/**
	 * Get the minimum zoom level, usually zero. The minimum zoom level is the
	 * zoom level where the map is displayed at maximum scale.
	 * 
	 * @return the minimum zoom level
	 */
	public int getMinimumZoom();

	/**
	 * Get the maximum zoom level, has to be greater than the minimum zoom level
	 * and less or equal the total map zoom level.
	 * 
	 * @return the maximum zoom level
	 */
	public int getMaximumZoom();

	/**
	 * Get the total map zoom level. The total map zoom level is the zoom level
	 * where the map is displayed at minimum scale.
	 * 
	 * @return the total map zoom level
	 */
	public int getTotalMapZoom();

	/**
	 * Returns the width of the map in tiles at the given zoom level
	 * 
	 * @param zoom the zoom level
	 * @return the width of the map in tiles
	 */
	public int getMapWidthInTiles(int zoom);

	/**
	 * Returns the height of the map in tiles at the given zoom level
	 * 
	 * @param zoom the zoom level
	 * @return the height of the map in tiles
	 */
	public int getMapHeightInTiles(int zoom);

	/**
	 * Returns an {@link URI} (and its alternatives) to the tile at the given
	 * tile coordinates and zoom level or <code>null</code> if there is no such
	 * tile.
	 * 
	 * @param x the x tile coordinate (valid values lie between 0 and
	 *            {@link #getMapWidthInTiles(int)})
	 * @param y the y tile coordinate (valid values lie between 0 and
	 *            {@link #getMapHeightInTiles(int)})
	 * @param zoom the zoom level
	 * @return an {@link URI} (and its alternatives) to a tile or
	 *         <code>null</code>
	 */
	public URI[] getTileUris(int x, int y, int zoom);

	/**
	 * Returns the default zoom level
	 * 
	 * @return the default zoom level
	 */
	public int getDefaultZoom();

	/**
	 * Specifies if horizontal wrapping shall be allowed for this map (makes
	 * sense for maps that show the whole world)
	 * 
	 * @return if horizontal wrapping is allowed for this tile map
	 */
	public boolean getAllowHorizontalWrapping();

	/**
	 * Specifies if tile borders shall be drawn for this tile map
	 * 
	 * @return if tile borders shall be drawn for this tile map
	 */
	public boolean getDrawTileBorders();

	/**
	 * Gets the custom overlay painter (may be null)
	 * 
	 * @return the custom overlay painter
	 */
	public Painter<JXMapViewer> getMapOverlayPainter();

}
