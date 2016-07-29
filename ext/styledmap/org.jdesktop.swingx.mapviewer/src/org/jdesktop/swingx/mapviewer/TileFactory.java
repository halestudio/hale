/*
 * TileFactory.java
 *
 * Created on March 17, 2006, 8:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

/**
 * A class that can produce tiles and convert coordinates to pixels
 * 
 * @author joshy
 * @author Simon Templer
 * 
 * @version $Id$
 */
public interface TileFactory {

	/**
	 * Gets the TileProvider of the TileFactory
	 * 
	 * @return the TileProvider
	 */
	public abstract TileProvider getTileProvider();

	/**
	 * 
	 * Return the Tile at a given TilePoint and zoom level
	 * 
	 * @return the tile that is located at the given tilePoint for this zoom
	 *         level. For example, if getMapSize() returns 10x20 for this zoom,
	 *         and the tilePoint is (3,5), then the appropriate tile will be
	 *         located and returned. This method must not return null. However,
	 *         it can return dummy tiles that contain no data if it wants. This
	 *         is appropriate, for example, for tiles which are outside of the
	 *         bounds of the map and if the factory doesn't implement wrapping.
	 * 
	 * @param x the tile's x coordinate
	 * @param y the tile's y coordinate
	 * @param zoom the current zoom level
	 */
	public abstract Tile getTile(int x, int y, int zoom);

	/**
	 * Start loading the given tile
	 * 
	 * @param tile the tile to load
	 */
	public abstract void startLoading(Tile tile);

	/**
	 * Stops the worker threads and frees the tile cache
	 */
	public abstract void cleanup();

	/**
	 * Clears the tile cache
	 */
	public abstract void clearCache();

}
