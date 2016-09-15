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

/**
 * GeoPositionConverter
 *
 * @author Simon Templer
 */
public interface GeoConverter {

	/**
	 * Tries to convert the given {@link GeoPosition} to the coordinate
	 * reference system specified by the given target epsg code.
	 * 
	 * @param pos the {@link GeoPosition} to convert
	 * @param targetEpsg the epsg code of the target coordinate reference system
	 * 
	 * @return the converted {@link GeoPosition}
	 * @throws IllegalGeoPositionException if the given {@link GeoPosition} is
	 *             invalid or conversion failed
	 */
	public abstract GeoPosition convert(GeoPosition pos, int targetEpsg)
			throws IllegalGeoPositionException;
}
