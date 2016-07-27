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

package de.fhg.igd.mapviewer.waypoints;

import java.awt.Point;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.PixelConverter;

/**
 * Pixel converter that supports a translation to a custom pixel coordinates
 * origin
 * 
 * @author Simon Templer
 */
public class TranslationPixelConverterDecorator implements PixelConverter {

	private final PixelConverter decoratee;

	private final int xPixelOrigin;

	private final int yPixelOrigin;

	/**
	 * Create a new pixel converter with a custom pixel coordinate origin
	 * 
	 * @param decoratee the original pixel converter
	 * @param xPixelOrigin the custom pixel origin x ordinate
	 * @param yPixelOrigin the custom pixel origin y ordinate
	 */
	public TranslationPixelConverterDecorator(PixelConverter decoratee, int xPixelOrigin,
			int yPixelOrigin) {
		super();
		this.decoratee = decoratee;
		this.xPixelOrigin = xPixelOrigin;
		this.yPixelOrigin = yPixelOrigin;
	}

	/**
	 * @see PixelConverter#pixelToGeo(Point2D, int)
	 */
	@Override
	public GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom) {
		// translate given pixel coordinates
		Point2D pixels = new Point((int) pixelCoordinate.getX() + xPixelOrigin,
				(int) pixelCoordinate.getY() + yPixelOrigin);
		return decoratee.pixelToGeo(pixels, zoom);
	}

	/**
	 * @see PixelConverter#geoToPixel(GeoPosition, int)
	 */
	@Override
	public Point2D geoToPixel(GeoPosition pos, int zoom) throws IllegalGeoPositionException {
		Point2D result = decoratee.geoToPixel(pos, zoom);
		// translate conversion result
		return new Point((int) result.getX() - xPixelOrigin, (int) result.getY() - yPixelOrigin);
	}

	/**
	 * @see PixelConverter#getMapEpsg()
	 */
	@Override
	public int getMapEpsg() {
		return decoratee.getMapEpsg();
	}

	/**
	 * @see PixelConverter#supportsBoundingBoxes()
	 */
	@Override
	public boolean supportsBoundingBoxes() {
		return decoratee.supportsBoundingBoxes();
	}

}
