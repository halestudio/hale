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

package de.fhg.igd.geom.shape;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;

import de.fhg.igd.geom.BoundingBox;
import de.fhg.igd.geom.Point2D;

/**
 * This class describes open 2D polylines.
 * 
 * @author Thorsten Reitz
 */
public class Line2D extends Shape {

	/**
	 * The class' serial version UID
	 */
	private static final long serialVersionUID = -658765289316063637L;

	/**
	 * This constructor builds a 2D Line from an array of Point2D.
	 * 
	 * @param points Point2D[]
	 */
	public Line2D(Point2D[] points) {
		this.setPoints(points);
	}

	/**
	 * Creates an empty line
	 */
	public Line2D() {
		// empty
	}

	// functional methods ......................................................

	/**
	 * This method will return this Line as a Java Topology Suite LineString.
	 * 
	 * @param geometryFactory the factory for JTS geometries
	 * @return the converted LineString
	 */
	private com.vividsolutions.jts.geom.LineString toJTSLineString(
			GeometryFactory geometryFactory) {
		Coordinate[] coords = new Coordinate[this.getPoints().length];
		for (int i = 0; i < this.getPoints().length; i++) {
			coords[i] = new Coordinate(this.getPoints()[i].getX(), this.getPoints()[i].getY());
		}
		CoordinateSequenceFactory csf = CoordinateArraySequenceFactory.instance();
		CoordinateSequence cs = csf.create(coords);
		com.vividsolutions.jts.geom.LineString jts_ls = new com.vividsolutions.jts.geom.LineString(
				cs, geometryFactory);
		return jts_ls;
	}

	/**
	 * @param buffer - a value that defines how far around the line the buffer
	 *            shall extend.
	 * @return a Polygon that is the buffer area.
	 */
	public Surface computeBuffer(double buffer) {
		GeometryFactory geometryFactory = new GeometryFactory();
		// transform to JTS LineString
		com.vividsolutions.jts.geom.LineString jts_ls = this.toJTSLineString(geometryFactory);

		// Buffer generation
		com.vividsolutions.jts.geom.Geometry jts_geom = jts_ls.buffer(buffer, 3);

		// transfrom back to CS3D Polygon.
		Surface result = new Surface();
		com.vividsolutions.jts.geom.Polygon jts_poly = (com.vividsolutions.jts.geom.Polygon) jts_geom;

		// transform outer Shell...
		com.vividsolutions.jts.geom.LineString jts_linering_outer = jts_poly.getExteriorRing();
		Coordinate[] coords = jts_linering_outer.getCoordinates();
		Point2D[] p2d_outer = new Point2D[coords.length];
		for (int i = 0; i < coords.length; i++) {
			p2d_outer[i] = new Point2D(coords[i].x, coords[i].y);
		}
		Polygon outer = new Polygon();
		outer.setPoints(p2d_outer);
		result.setExterior_boundary(outer);

		// transform and add inner rings, if existing.
		if (jts_poly.getNumInteriorRing() > 0) {
			Polygon[] inner_polys = new Polygon[jts_poly.getNumInteriorRing()];
			for (int n = 0; n < jts_poly.getNumInteriorRing(); n++) {
				com.vividsolutions.jts.geom.LineString jts_linering_inner = jts_poly
						.getInteriorRingN(n);
				coords = jts_linering_inner.getCoordinates();
				Point2D[] p2d_inner = new Point2D[coords.length];
				for (int i = 0; i < coords.length; i++) {
					p2d_inner[i] = new Point2D(coords[i].x, coords[i].y);
				}
				inner_polys[n] = new Polygon();
				inner_polys[n].setPoints(p2d_inner);
			}
			result.setInterior_boundaries(inner_polys);
		}

		return result;
	}

	/**
	 * Returns a BoundingBox for this object that takes into account possible
	 * transforms. TODO actually take transforms into Account :-).
	 * 
	 * @return de.fhg.igd.CityServer3D.dbLayer.helperGeometry.BoundingBox
	 */
	@Override
	public BoundingBox getBoundingBox() {
		BoundingBox bb = BoundingBox.compute(this.getPoints());
		return bb;
	}

	// canonical java methods ..................................................

	/**
	 * Standard toString method. super.toString() is included.
	 * 
	 * @return a string representation of this Line
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("[Line2D (");
		for (int i = 0; i < this.points.length; i++) {
			result.append(this.points[i].toString() + "\n");
		}
		result.append(")]\n");
		return result.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Line2D))
			return false;
		return true;
	}

}
