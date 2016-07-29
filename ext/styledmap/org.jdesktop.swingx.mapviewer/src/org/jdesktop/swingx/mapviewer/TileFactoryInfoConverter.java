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

import org.jdesktop.swingx.mapviewer.util.GeoUtil;

/**
 * Google Mercator projection converter for TileFactories using TileFactoryInfo
 *
 * @author Simon Templer
 * 
 * @deprecated use GoogleMercatorConverter instead
 */
@Deprecated
public class TileFactoryInfoConverter extends AbstractPixelConverter {

	private final TileFactoryInfo info;

	/**
	 * Constructor
	 * 
	 * @param info the tile factory info
	 * @param geoConverter the geo converter
	 */
	public TileFactoryInfoConverter(TileFactoryInfo info, GeoConverter geoConverter) {
		super(geoConverter);

		this.info = info;
	}

	/**
	 * @see PixelConverter#geoToPixel(GeoPosition, int)
	 */
	@Override
	public Point2D geoToPixel(GeoPosition pos, int zoom) throws IllegalGeoPositionException {
		pos = geoConverter.convert(pos, GeoPosition.WGS_84_EPSG);

		return GeoUtil.getBitmapCoordinate(pos, zoom, info);
	}

	/**
	 * @see PixelConverter#pixelToGeo(Point2D, int)
	 */
	@Override
	public GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom) {
		return GeoUtil.getPosition(pixelCoordinate, zoom, info);
	}

	/**
	 * @see PixelConverter#getMapEpsg()
	 */
	@Override
	public int getMapEpsg() {
		return GeoPosition.WGS_84_EPSG;
	}

	/**
	 * @see PixelConverter#supportsBoundingBoxes()
	 */
	@Override
	public boolean supportsBoundingBoxes() {
		return false;
	}

}
