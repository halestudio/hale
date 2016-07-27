/*
 * Copyright (c) 2016 Fraunhofer IGD
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.server;

import java.awt.geom.Point2D;
import java.util.Properties;

import org.jdesktop.swingx.mapviewer.AbstractPixelConverter;
import org.jdesktop.swingx.mapviewer.GeoConverter;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.jdesktop.swingx.mapviewer.TileProvider;

/**
 * PixelConverter that supports a bounded area in a certain crs where linear
 * interpolation of the coordinates is allowed.
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class LinearBoundsConverter extends AbstractPixelConverter {

	// mandatory properties
	/** the name of the minimum x ordinate property */
	public static final String PROP_MIN_X = "minX"; //$NON-NLS-1$
	/** the name of the minimum y ordinate property */
	public static final String PROP_MIN_Y = "minY"; //$NON-NLS-1$
	/** the name of the x range property */
	public static final String PROP_X_RANGE = "xRange"; //$NON-NLS-1$
	/** the name of the y range property */
	public static final String PROP_Y_RANGE = "yRange"; //$NON-NLS-1$
	/** the name of the EPSG code property */
	public static final String PROP_EPSG = "epsg"; //$NON-NLS-1$
	// optional properties
	/** the name of the swap axes property */
	public static final String PROP_SWAP_AXES = "swapAxes"; //$NON-NLS-1$
	/** the name of the reverse x property */
	public static final String PROP_REVERSE_X = "reverseX"; //$NON-NLS-1$
	/** the name of the reverse y property */
	public static final String PROP_REVERSE_Y = "reverseY"; //$NON-NLS-1$

	private final double minX;
	private final double minY;
	private final double xRange;
	private final double yRange;

	private final int epsg;

	private final TileProvider tileProvider;

	private final boolean swapAxes;
	private final boolean reverseX;
	private final boolean reverseY;

	/**
	 * Creates a {@link LinearBoundsConverter} and configures it using the given
	 * {@link Properties}
	 * 
	 * @param properties the properties defining the parameters
	 * @param tileProvider the tile provider
	 */
	public LinearBoundsConverter(Properties properties, TileProvider tileProvider) {
		this(GeotoolsConverter.getInstance(), tileProvider,
				Double.parseDouble(properties.getProperty(PROP_MIN_X)),
				Double.parseDouble(properties.getProperty(PROP_MIN_Y)),
				Double.parseDouble(properties.getProperty(PROP_X_RANGE)),
				Double.parseDouble(properties.getProperty(PROP_Y_RANGE)),
				Integer.parseInt(properties.getProperty(PROP_EPSG)),
				Boolean.parseBoolean(properties.getProperty(PROP_SWAP_AXES, "false")), //$NON-NLS-1$
				Boolean.parseBoolean(properties.getProperty(PROP_REVERSE_X, "false")), //$NON-NLS-1$
				Boolean.parseBoolean(properties.getProperty(PROP_REVERSE_Y, "false")) //$NON-NLS-1$
		);
	}

	/**
	 * Creates a {@link LinearBoundsConverter}
	 * 
	 * @param geoConverter the GEO converter
	 * @param tileProvider the tile provider
	 * @param minX the minimum x ordinate
	 * @param minY the minimum y ordinate
	 * @param xRange the range in x-direction
	 * @param yRange the range in y-direction
	 * @param epsg the EPSG code of the SRS
	 * @param swapAxes if the axes shall be swapped
	 * @param reverseX if the x-axis shall be reversed
	 * @param reverseY if the y-axis shall be reversed
	 */
	public LinearBoundsConverter(final GeoConverter geoConverter, final TileProvider tileProvider,
			final double minX, final double minY, final double xRange, final double yRange,
			final int epsg, final boolean swapAxes, final boolean reverseX,
			final boolean reverseY) {
		super(geoConverter);

		this.tileProvider = tileProvider;

		this.minX = minX;
		this.minY = minY;
		this.xRange = xRange;
		this.yRange = yRange;

		this.epsg = epsg;

		this.swapAxes = swapAxes;
		this.reverseX = reverseX;
		this.reverseY = reverseY;
	}

	/**
	 * @see PixelConverter#geoToPixel(GeoPosition, int)
	 */
	@Override
	public Point2D geoToPixel(GeoPosition pos, final int zoom) throws IllegalGeoPositionException {
		// convert to desired epsg code
		pos = geoConverter.convert(pos, epsg);

		// check position - in bounds?
		if (pos.getX() < minX || pos.getX() > minX + xRange || pos.getY() < minY
				|| pos.getY() > minY + yRange)
			throw new IllegalGeoPositionException("GeoPosition out of bounds: " + pos); //$NON-NLS-1$

		// calculate range fractions
		double xFrac = ((pos.getX() - minX) / xRange);
		double yFrac = ((pos.getY() - minY) / yRange);

		// reverse fractions if needed
		if (reverseX)
			xFrac = 1.0 - xFrac;
		if (reverseY)
			yFrac = 1.0 - yFrac;

		// swap fractions if needed
		if (swapAxes) {
			double tmpFrac = xFrac;
			xFrac = yFrac;
			yFrac = tmpFrac;
		}

		// calculate pixels
		try {
			double x = xFrac * tileProvider.getMapWidthInTiles(zoom)
					* tileProvider.getTileWidth(zoom);
			double y = yFrac * tileProvider.getMapHeightInTiles(zoom)
					* tileProvider.getTileHeight(zoom);

			return new Point2D.Double(x, y);
		} catch (Exception e) {
			throw new IllegalGeoPositionException("Invalid zoom level", e); //$NON-NLS-1$
		}
	}

	/**
	 * @see PixelConverter#pixelToGeo(Point2D, int)
	 */
	@Override
	public GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom) {
		// calculate map width/height
		int mapWidth = tileProvider.getMapWidthInTiles(zoom) * tileProvider.getTileWidth(zoom);
		int mapHeight = tileProvider.getMapHeightInTiles(zoom) * tileProvider.getTileHeight(zoom);

		// calculate pixel fractions
		double xFrac = pixelCoordinate.getX() / mapWidth;
		double yFrac = pixelCoordinate.getY() / mapHeight;

		// swap axes if needed
		if (swapAxes) {
			double tmpFrac = xFrac;
			xFrac = yFrac;
			yFrac = tmpFrac;
		}

		// reverse fractions if needed
		if (reverseX)
			xFrac = 1.0 - xFrac;
		if (reverseY)
			yFrac = 1.0 - yFrac;

		// calculate coordinates
		double x = minX + xFrac * xRange;
		double y = minY + yFrac * yRange;

		return new GeoPosition(x, y, epsg);
	}

	/**
	 * @see PixelConverter#getMapEpsg()
	 */
	@Override
	public int getMapEpsg() {
		return epsg;
	}

	/**
	 * @see PixelConverter#supportsBoundingBoxes()
	 */
	@Override
	public boolean supportsBoundingBoxes() {
		return true;
	}

}
