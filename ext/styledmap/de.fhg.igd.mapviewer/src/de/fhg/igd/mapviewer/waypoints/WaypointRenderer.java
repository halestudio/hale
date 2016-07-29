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
 * WaypointRenderer
 *
 * @param <W> the way-point type
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 * @version $Id: CustomWaypointRenderer.java 582 2009-07-09 07:21:27Z stempler $
 */
public interface WaypointRenderer<W extends Waypoint> {

	/**
	 * Paints the given way-point
	 * 
	 * @param g the graphics device
	 * @param zoom the map zoom level (needed if the rendering is dependent on
	 *            the zoom level)
	 * @param converter the pixel converter
	 * @param w the way-point
	 * @param gBounds the graphics bounds
	 * @return the bounding shape
	 */
	public Area paintWaypoint(final Graphics2D g, PixelConverter converter, int zoom, final W w,
			Rectangle gBounds);

}
