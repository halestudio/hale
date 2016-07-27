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

import gnu.trove.TIntObjectHashMap;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.jdesktop.swingx.mapviewer.PixelConverter;

import de.fhg.igd.mapviewer.marker.area.Area;

/**
 * Abstract marker implementation
 * 
 * @author Simon Templer
 * @param <T> the context type
 */
public abstract class AbstractMarker<T> implements Marker<T> {

	private final TIntObjectHashMap<Area> areas = new TIntObjectHashMap<Area>();

	/**
	 * @see Marker#paint(Graphics2D, PixelConverter, int, Object, Rectangle)
	 */
	@Override
	public void paint(Graphics2D g, PixelConverter converter, int zoom, T context,
			Rectangle gBounds) {
		synchronized (areas) {
			if (!areas.containsKey(zoom)) {
				Area area = paintMarker(g, context, converter, zoom, gBounds, true);
				areas.put(zoom, area);
				return;
			}
		}
		paintMarker(g, context, converter, zoom, gBounds, false);
	}

	/**
	 * Paint a marker.
	 * 
	 * @param g the graphics device
	 * @param context the painting context
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * @param gBounds the graphics bounds
	 * @param calculateArea if the area representing the marker should be
	 *            calculated, if <code>false</code> is given here the return
	 *            value is ignored and should be <code>null</code>
	 * @return the area that represents the marker, this may be
	 *         <code>null</code> if the marker represents no area, or if
	 *         calculateArea is <code>false</code>
	 */
	protected abstract Area paintMarker(Graphics2D g, T context, PixelConverter converter, int zoom,
			Rectangle gBounds, boolean calculateArea);

	/**
	 * @see Marker#getArea(int)
	 */
	@Override
	public Area getArea(int zoom) {
		synchronized (areas) {
			return areas.get(zoom);
		}
	}

	/**
	 * @see Marker#reset()
	 */
	@Override
	public void reset() {
		synchronized (areas) {
			areas.clear();
		}
	}

}
