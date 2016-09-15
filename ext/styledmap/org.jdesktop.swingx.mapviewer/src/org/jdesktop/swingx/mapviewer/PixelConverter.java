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

import java.awt.geom.Point2D;

/**
 * GeoConverter
 *
 * @author Simon Templer
 */
public interface PixelConverter {

	/**
	 * Convert a pixel in the world bitmap at the specified zoom level into a
	 * GeoPosition
	 * 
	 * @param pixelCoordinate a Point2D representing a pixel in the world bitmap
	 * @param zoom the zoom level of the world bitmap
	 * @return the converted GeoPosition
	 */
	public GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom);

	/**
	 * Convert a GeoPosition to a pixel position in the world bitmap a the
	 * specified zoom level.
	 * 
	 * @param pos a GeoPosition
	 * @param zoom the zoom level to extract the pixel coordinate for
	 * @return the pixel point
	 * 
	 * @throws IllegalGeoPositionException if pos is not inside the map's bounds
	 *             or if it cannot be converted to the map's coordinate
	 *             reference system
	 */
	public Point2D geoToPixel(GeoPosition pos, int zoom) throws IllegalGeoPositionException;

	/**
	 * Get the EPSG code of the map SRS
	 * 
	 * @return the EPSG code
	 */
	public int getMapEpsg();

	/**
	 * Get if non-overlapping bounding boxes are supported
	 * 
	 * @return if non-overlapping bounding boxes are supported
	 */
	public boolean supportsBoundingBoxes();

}
