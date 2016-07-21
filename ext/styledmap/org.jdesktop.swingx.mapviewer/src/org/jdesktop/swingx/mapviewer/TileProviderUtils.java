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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * TileProviderUtils
 *
 * @author Simon Templer
 */
public abstract class TileProviderUtils {

	/**
	 * Returns the map center in pixels at the given zoom level
	 * 
	 * @param tileProvider the {@link TileProvider}
	 * @param zoom the zoom level
	 * @return the map center in pixels
	 */
	public static Point2D getMapCenterInPixels(TileProvider tileProvider, int zoom) {
		return new Point(
				tileProvider.getTileWidth(zoom) * tileProvider.getMapWidthInTiles(zoom) / 2,
				tileProvider.getTileHeight(zoom) * tileProvider.getMapHeightInTiles(zoom) / 2);
	}

	/**
	 * Returns the map center position
	 * 
	 * @param tileProvider the {@link TileProvider}
	 * @return the map center position
	 */
	public static GeoPosition getMapCenter(TileProvider tileProvider) {
		int zoom = tileProvider.getMinimumZoom();

		Point2D center = getMapCenterInPixels(tileProvider, zoom);

		return tileProvider.getConverter().pixelToGeo(center, zoom);
	}

	/**
	 * Returns a Dimension containing the width and height of the map in tiles
	 * at the given zoom level. So a Dimension that returns 10x20 would be 10
	 * tiles wide and 20 tiles tall. These values can be multipled by
	 * getTileSize() to determine the pixel width/height for the map at the
	 * given zoom level.
	 * 
	 * @return the size of the world bitmap in tiles
	 * @param tileProvider the {@link TileProvider}
	 * @param zoom the current zoom level
	 */
	public static Dimension getMapSize(TileProvider tileProvider, int zoom) {
		return new Dimension(tileProvider.getMapWidthInTiles(zoom),
				tileProvider.getMapHeightInTiles(zoom));
	}

	/**
	 * Returns if the given tile coordinates and zoom level specify a valid tile
	 * 
	 * @param tileProvider the {@link TileProvider}
	 * @param x the x tile coordinate
	 * @param y the y tile coordinate
	 * @param zoom the zoom level
	 * @return true if the given tile coordinates and zoom level specify a valid
	 *         tile, otherwise false
	 */
	public static boolean isValidTile(TileProvider tileProvider, int x, int y, int zoom) {
		if (x < 0 || y < 0)
			return false;
		if (zoom < tileProvider.getMinimumZoom() || zoom > tileProvider.getMaximumZoom())
			return false;
		if (x >= tileProvider.getMapWidthInTiles(zoom)
				|| y >= tileProvider.getMapHeightInTiles(zoom))
			return false;

		return true;
	}

}
