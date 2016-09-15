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

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.jdesktop.swingx.mapviewer.PixelConverter;

import de.fhg.igd.mapviewer.marker.area.Area;

/**
 * A simple marker that paints itself the same way regardless of the zoom level
 * or map
 * 
 * @param <T> the painter context type
 * @author Simon Templer
 */
public abstract class SimpleMarker<T> extends CachingPainter<T>implements Marker<T> {

	private Area area;

	/**
	 * @see CachingPainter#doPaint(java.lang.Object)
	 */
	@Override
	protected final void doPaint(T context) {
		area = paintMarker(context);
	}

	/**
	 * Paint a marker. Implementations of this method must call
	 * {@link #beginPainting(int, int, int, int)} to get a graphics object and
	 * {@link #endPainting(Graphics2D)} to finish painting.
	 * 
	 * @param context the painting context
	 * 
	 * @return the area that represents the marker
	 */
	protected abstract Area paintMarker(T context);

	/**
	 * @see Marker#paint(Graphics2D, PixelConverter, int, Object, Rectangle)
	 */
	@Override
	public void paint(Graphics2D g, PixelConverter converter, int zoom, T context,
			Rectangle gBounds) {
		paint(g, context, 0, 0);
	}

	/**
	 * @see Marker#reset()
	 */
	@Override
	public void reset() {
		// do nothing
	}

	/**
	 * @see Marker#getArea(int)
	 */
	@Override
	public Area getArea(int zoom) {
		return area;
	}

}
