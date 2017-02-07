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

import de.fhg.igd.geom.Point2D;

/**
 * Area represented by a polygon
 * 
 * @author Simon Templer
 */
public class PolygonArea implements Area {

	private final Polygon poly;

	private double area;

	private boolean areaInitialized = false;

	private de.fhg.igd.geom.shape.Polygon mmPoly = null;

	/**
	 * Constructor
	 * 
	 * @param poly the polygon
	 */
	public PolygonArea(Polygon poly) {
		super();
		this.poly = poly;
	}

	/**
	 * @see Area#getArea()
	 */
	@Override
	public double getArea() {
		if (!areaInitialized) {
			double area = 0;

			for (int i = 0; i < poly.npoints; i++) {
				int j = (i + 1) % poly.npoints;
				area += poly.xpoints[i] * poly.ypoints[j];
				area -= poly.ypoints[i] * poly.xpoints[j];
			}
			area /= 2.0;

			this.area = ((area < 0) ? (-area) : (area));
			areaInitialized = true;
		}

		return area;
	}

	/**
	 * @see Area#contains(int, int)
	 */
	@Override
	public boolean contains(int x, int y) {
		return poly.contains(x, y);
	}

	/**
	 * @see Area#containedIn(Polygon)
	 */
	@Override
	public boolean containedIn(Polygon poly) {
		return toModelPolygon(poly).contains(toModelPolygon());
	}

	/**
	 * @see Area#containedIn(Rectangle)
	 */
	@Override
	public boolean containedIn(Rectangle rect) {
		return toModelPolygon(rect).contains(toModelPolygon());
	}

	private de.fhg.igd.geom.shape.Polygon toModelPolygon() {
		if (mmPoly == null) {
			mmPoly = toModelPolygon(poly);
		}
		return mmPoly;
	}

	private static de.fhg.igd.geom.shape.Polygon toModelPolygon(Polygon poly) {
		Point2D[] points = new Point2D[poly.npoints];
		for (int i = 0; i < poly.npoints; i++) {
			points[i] = new Point2D(poly.xpoints[i], poly.ypoints[i]);
		}
		return new de.fhg.igd.geom.shape.Polygon(points);
	}

	private static de.fhg.igd.geom.shape.Polygon toModelPolygon(Rectangle rect) {
		Point2D[] points = new Point2D[4];
		points[0] = new Point2D(rect.x, rect.y);
		points[1] = new Point2D(rect.x + rect.width, rect.y);
		points[2] = new Point2D(rect.x + rect.width, rect.y + rect.height);
		points[3] = new Point2D(rect.x, rect.y + rect.height);
		return new de.fhg.igd.geom.shape.Polygon(points);
	}

}
