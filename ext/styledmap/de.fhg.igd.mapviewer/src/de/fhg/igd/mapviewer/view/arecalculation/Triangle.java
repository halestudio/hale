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

package de.fhg.igd.mapviewer.view.arecalculation;

import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * This class represents a triangle in 2D. It provides functionality to
 * calculate it's surface area.
 *
 * @author <a href="mailto:andreas.burchert@igd.fhg.de">Andreas Burchert</a>
 */
public class Triangle {

	/**
	 * Contains all vertices.
	 */
	private GeoPosition p1, p2, p3;

	/**
	 * Contains the surface area.
	 */
	private double area = 0.0;

	/**
	 * Constructor.
	 * 
	 * @param p1 first {@link GeoPosition}
	 * @param p2 second {@link GeoPosition}
	 * @param p3 third {@link GeoPosition}
	 */
	public Triangle(GeoPosition p1, GeoPosition p2, GeoPosition p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;

		this.area = this.heronFormula();
	}

	/**
	 * Calculates the area with use of Heron's formula.
	 * 
	 * @return area
	 */
	private double heronFormula() {
		double result = 0.0;

		double a, b, c, s;
		a = AreaCalc.calculateDistance(p1, p2);
		b = AreaCalc.calculateDistance(p1, p3);
		c = AreaCalc.calculateDistance(p2, p3);

		s = (a + b + c) / 2;

		result = Math.sqrt(s * (s - a) * (s - b) * (s - c));

		return result;
	}

	/**
	 * Returns the surface area for this triangle.
	 * 
	 * @return area
	 */
	public double getArea() {
		return this.area;
	}
}
