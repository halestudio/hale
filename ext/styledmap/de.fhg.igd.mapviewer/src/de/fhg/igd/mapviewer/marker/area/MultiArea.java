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
 * Area represented by multiple areas
 * 
 * @author Simon Templer
 */
public class MultiArea implements Area {

	private final Iterable<Area> areas;

	/**
	 * Constructor
	 * 
	 * @param areas the areas (iterable will not be copied)
	 */
	public MultiArea(Iterable<Area> areas) {
		super();
		this.areas = areas;
	}

	/**
	 * @see Area#getArea()
	 */
	@Override
	public double getArea() {
		double sum = 0;
		for (Area area : areas) {
			sum += area.getArea();
		}
		return sum;
	}

	/**
	 * @see Area#contains(int, int)
	 */
	@Override
	public boolean contains(int x, int y) {
		for (Area area : areas) {
			if (area.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see Area#containedIn(Polygon)
	 */
	@Override
	public boolean containedIn(Polygon poly) {
		for (Area area : areas) {
			if (!area.containedIn(poly)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see Area#containedIn(Rectangle)
	 */
	@Override
	public boolean containedIn(Rectangle rect) {
		for (Area area : areas) {
			if (!area.containedIn(rect)) {
				return false;
			}
		}
		return true;
	}

}
