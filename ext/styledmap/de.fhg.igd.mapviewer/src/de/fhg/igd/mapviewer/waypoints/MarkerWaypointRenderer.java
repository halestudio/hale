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

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.jdesktop.swingx.mapviewer.Waypoint;

import de.fhg.igd.mapviewer.marker.area.Area;

/**
 * Waypoint renderer using markers.
 * 
 * @param <W> the way-point type
 * @author Simon Templer
 */
public class MarkerWaypointRenderer<W extends SelectableWaypoint<W>>
		implements WaypointRenderer<W> {

	/**
	 * @see WaypointRenderer#paintWaypoint(Graphics2D, PixelConverter, int,
	 *      Waypoint, Rectangle)
	 */
	@Override
	public Area paintWaypoint(Graphics2D g, PixelConverter converter, int zoom, W w,
			Rectangle gBounds) {

		w.getMarker().paint(g, converter, zoom, w, gBounds);

		return w.getMarker().getArea(zoom);
	}
}
