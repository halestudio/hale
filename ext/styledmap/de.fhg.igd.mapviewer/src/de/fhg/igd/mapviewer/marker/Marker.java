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
 * A marker
 * 
 * @author Simon Templer
 * @param <T> the context type
 */
public interface Marker<T> {

	/**
	 * Paint on a graphics device
	 * 
	 * @param g the graphics device
	 * @param converter the pixel converter
	 * @param zoom the current map zoom level
	 * @param context the painting context
	 * @param gBounds the graphics bounds
	 */
	public void paint(Graphics2D g, PixelConverter converter, int zoom, T context,
			Rectangle gBounds);

	/**
	 * Get the marker area for the respective zoom level
	 * 
	 * @param zoom the zoom level
	 * 
	 * @return the area
	 */
	public abstract Area getArea(int zoom);

	/**
	 * Reset the marker when the map was changed
	 */
	public abstract void reset();

}
