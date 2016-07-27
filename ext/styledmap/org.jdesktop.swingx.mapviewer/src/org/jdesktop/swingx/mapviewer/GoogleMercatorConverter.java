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
 * Google Mercator projection converter providing WGS84 GeoPositions. Usable
 * with world maps using Google like Mercator projection.
 *
 * @author Simon Templer
 */
public class GoogleMercatorConverter extends AbstractPixelConverter {

	private final TileProvider tileProvider;

	/**
	 * Constructor
	 * 
	 * @param tileProvider the tile provider
	 * @param geoConverter the geo converter
	 */
	public GoogleMercatorConverter(TileProvider tileProvider, GeoConverter geoConverter) {
		super(geoConverter);

		this.tileProvider = tileProvider;
	}

	private double getLongitudeDegreeWidthInPixels(int zoom) {
		return tileProvider.getMapWidthInTiles(zoom) * tileProvider.getTileWidth(zoom) / 360.0;
	}

	private double getLongitudeRadianWidthInPixels(int zoom) {
		return tileProvider.getMapWidthInTiles(zoom) * tileProvider.getTileWidth(zoom)
				/ (2.0 * Math.PI);
	}

	/**
	 * @see PixelConverter#geoToPixel(GeoPosition, int)
	 */
	@Override
	public Point2D geoToPixel(GeoPosition pos, int zoom) throws IllegalGeoPositionException {
		// convert to WGS84 long/lat
		pos = geoConverter.convert(pos, GeoPosition.WGS_84_EPSG);

		double x = TileProviderUtils.getMapCenterInPixels(tileProvider, zoom).getX()
				+ pos.getX() /* long */ * getLongitudeDegreeWidthInPixels(zoom);
		double e = Math.sin(pos.getY() /* lat */ * (Math.PI / 180.0));
		if (e > 0.9999) {
			e = 0.9999;
		}
		if (e < -0.9999) {
			e = -0.9999;
		}
		double y = TileProviderUtils.getMapCenterInPixels(tileProvider, zoom).getY()
				+ 0.5 * Math.log((1 + e) / (1 - e)) * -1 * (getLongitudeRadianWidthInPixels(zoom));

		return new Point2D.Double(x, y);
	}

	/**
	 * @see PixelConverter#pixelToGeo(Point2D, int)
	 */
	@Override
	public GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom) {
		double wx = pixelCoordinate.getX();
		double wy = pixelCoordinate.getY();
		// this reverses getBitmapCoordinates
		double flon = (wx - TileProviderUtils.getMapCenterInPixels(tileProvider, zoom).getX())
				/ getLongitudeDegreeWidthInPixels(zoom);
		double e1 = (wy - TileProviderUtils.getMapCenterInPixels(tileProvider, zoom).getY())
				/ (-1 * getLongitudeRadianWidthInPixels(zoom));
		double e2 = (2 * Math.atan(Math.exp(e1)) - Math.PI / 2) / (Math.PI / 180.0);
		double flat = e2;
		GeoPosition wc = new GeoPosition(flon, flat, GeoPosition.WGS_84_EPSG);
		return wc;
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
