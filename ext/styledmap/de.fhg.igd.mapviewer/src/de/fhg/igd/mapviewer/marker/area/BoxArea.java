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
 * Area represented by a bounding box
 * 
 * @author Simon Templer
 */
public class BoxArea implements Area {

	private final int minX;
	private final int minY;
	private final int maxX;
	private final int maxY;

	/**
	 * Constructor
	 * 
	 * @param minX the minimum x pixel ordinate
	 * @param minY the minimum y pixel ordinate
	 * @param maxX the maximum x pixel ordinate
	 * @param maxY the maximum y pixel ordinate
	 */
	public BoxArea(int minX, int minY, int maxX, int maxY) {
		super();
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	/**
	 * @see Area#getArea()
	 */
	@Override
	public double getArea() {
		return (maxX - minX) * (maxY - minY);
	}

	/**
	 * @see Area#contains(int, int)
	 */
	@Override
	public boolean contains(int x, int y) {
		return x <= maxX && x >= minX && y <= maxY && y >= minY;
	}

	/**
	 * @see Area#containedIn(Polygon)
	 */
	@Override
	public boolean containedIn(Polygon poly) {
		return poly.contains(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * @see Area#containedIn(Rectangle)
	 */
	@Override
	public boolean containedIn(Rectangle rect) {
		return rect.contains(minX, minY, maxX - minX, maxY - minY);
	}

}
