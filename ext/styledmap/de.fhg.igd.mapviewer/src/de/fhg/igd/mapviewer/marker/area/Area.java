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

package de.fhg.igd.mapviewer.marker.area;

import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * A pixel area on the map, represents a marker
 * 
 * @author Simon Templer
 */
public interface Area {

	/**
	 * Get the area
	 * 
	 * @return the area in square pixels
	 */
	public double getArea();

	/**
	 * Determines if the given pixel coordinates are contained in the area
	 * 
	 * @param x the pixel x ordinate
	 * @param y the pixel y ordinate
	 * @return if the pixel is contained in the area
	 */
	public boolean contains(int x, int y);

	/**
	 * Determines if the area is contained in the given polygon
	 * 
	 * @param poly the polygon
	 * @return if the polygon contains the area
	 */
	public boolean containedIn(Polygon poly);

	/**
	 * Determines if the area is contained in the given rectangle
	 * 
	 * @param rect the rectangle
	 * @return if the rectangle contains the area
	 */
	public boolean containedIn(Rectangle rect);

}
