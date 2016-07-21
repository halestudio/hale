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

package de.fhg.igd.mapviewer.marker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.PixelConverter;

import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.marker.area.Area;
import de.fhg.igd.mapviewer.marker.area.BoxArea;
import de.fhg.igd.mapviewer.waypoints.SelectableWaypoint;

/**
 * Marker that paints way-point bounding boxes
 * 
 * @author Simon Templer
 * @param <T> the context type
 */
public abstract class BoundingBoxMarker<T extends SelectableWaypoint<T>> extends AbstractMarker<T> {

	/**
	 * @see AbstractMarker#paintMarker(Graphics2D, Object, PixelConverter, int,
	 *      Rectangle, boolean)
	 */
	@Override
	protected Area paintMarker(Graphics2D g, T context, PixelConverter converter, int zoom,
			Rectangle gBounds, boolean calculateArea) {
		if (context.isPoint()) {
			return paintFallback(g, context, converter, zoom, gBounds, calculateArea);
		}

		BoundingBox bb = context.getBoundingBox();
		int code = SelectableWaypoint.COMMON_EPSG;

		GeoPosition pos1 = new GeoPosition(bb.getMinX(), bb.getMinY(), code);
		GeoPosition pos2 = new GeoPosition(bb.getMaxX(), bb.getMaxY(), code);

		try {
			Point2D p1 = converter.geoToPixel(pos1, zoom);
			Point2D p2 = converter.geoToPixel(pos2, zoom);

			int minX = (int) Math.min(p1.getX(), p2.getX());
			int minY = (int) Math.min(p1.getY(), p2.getY());
			int maxX = (int) Math.max(p1.getX(), p2.getX());
			int maxY = (int) Math.max(p1.getY(), p2.getY());

			int width = maxX - minX;
			int height = maxY - minY;

			// decide whether it is to small to paint
			if (isToSmall(width, height, zoom)) {
				return paintFallback(g, context, converter, zoom, gBounds, calculateArea);
			}

			return doPaintMarker(g, context, converter, zoom, minX, minY, maxX, maxY, gBounds,
					calculateArea);
		} catch (IllegalGeoPositionException e) {
			// use fallback marker instead
			return paintFallback(g, context, converter, zoom, gBounds, calculateArea);
		}
	}

	/**
	 * Paint the marker.
	 * 
	 * @param g the graphics device
	 * @param context the painting context
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * @param minX the bounding box minimum x pixel coordinate
	 * @param minY the bounding box minimum y pixel coordinate
	 * @param maxX the bounding box maximum x pixel coordinate
	 * @param maxY the bounding box maximum y pixel coordinate
	 * @param gBounds the graphics bounds
	 * @param calculateArea if the area representing the marker should be
	 *            calculated, if <code>false</code> is given here the return
	 *            value is ignored and should be <code>null</code>
	 * @return the area that represents the marker
	 */
	protected Area doPaintMarker(Graphics2D g, T context, PixelConverter converter, int zoom,
			int minX, int minY, int maxX, int maxY, Rectangle gBounds, boolean calculateArea) {
		if (applyFill(g, context)) {
			g.fillRect(minX, minY, maxX - minX + 1, maxY - minY + 1);
		}

		if (applyStroke(g, context)) {
			g.drawRect(minX, minY, maxX - minX + 1, maxY - minY + 1);
		}

		if (calculateArea) {
			return new BoxArea(minX, minY, maxX, maxY);
		}
		else {
			return null;
		}
	}

	/**
	 * Determines if painting a fill is allowed, if yes applies the fill style
	 * to the given graphics.<br>
	 * <br>
	 * By default sets the paint color to
	 * {@link #getPaintColor(SelectableWaypoint)} and allows painting a fill.
	 * 
	 * @param g the graphics
	 * @param context the paint context
	 * @return if painting a fill is allowed
	 */
	protected boolean applyFill(Graphics2D g, T context) {
		g.setPaint(getPaintColor(context));
		return true;
	}

	/**
	 * Determines if painting lines is allowed, if yes applies the stroke style
	 * to the given graphics.<br>
	 * <br>
	 * By default sets the drawing color to
	 * {@link #getBorderColor(SelectableWaypoint)} and allows painting lines.
	 * 
	 * @param g the graphics
	 * @param context the paint context
	 * @return if painting lines is allowed
	 */
	protected boolean applyStroke(Graphics2D g, T context) {
		g.setColor(getBorderColor(context));
		return true;
	}

	/**
	 * Decides whether a bounding box is to small to paint
	 * 
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @param zoom the zoom level
	 * @return if the bounding box is to small and instead the fall-back marker
	 *         should be used
	 */
	protected boolean isToSmall(int width, int height, int zoom) {
		return width <= 2 || height <= 2 || (width <= 4 && height <= 4);
	}

	/**
	 * Paint the fall-back marker
	 * 
	 * @param g the graphics device
	 * @param context the way-point
	 * @param converter the converter
	 * @param zoom the zoom level
	 * @param gBounds the graphics bounds
	 * @param calculateArea if the area representing the marker should be
	 *            calculated, if <code>false</code> is given here the return
	 *            value is ignored and should be <code>null</code>
	 * @return the area that represents the marker
	 */
	protected Area paintFallback(Graphics2D g, T context, PixelConverter converter, int zoom,
			Rectangle gBounds, boolean calculateArea) {
		Marker<? super T> marker = getFallbackMarker(context);
		marker.paint(g, converter, zoom, context, gBounds);
		if (calculateArea) {
			return marker.getArea(zoom);
		}
		else {
			return null;
		}
	}

	/**
	 * Get the paint color
	 * 
	 * @param context the painting context
	 * @return the paint color
	 */
	protected abstract Color getPaintColor(T context);

	/**
	 * Get the border color
	 * 
	 * @param context the painting context
	 * @return the border color
	 */
	protected abstract Color getBorderColor(T context);

	/**
	 * Get the fall-back marker to use for painting when geo conversion fails or
	 * the bounding box would be to small
	 * 
	 * @param context the context object
	 * @return the fall-back marker
	 */
	protected abstract Marker<? super T> getFallbackMarker(T context);

}
