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

package de.fhg.igd.mapviewer.geom.shape;

import java.util.ArrayList;
import java.util.List;

import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.geom.Extent;
import de.fhg.igd.mapviewer.geom.Point2D;
import de.fhg.igd.mapviewer.geom.algorithm.sweepline.Point2DEvent;
import de.fhg.igd.mapviewer.geom.algorithm.sweepline.Point2DEventQueue;
import de.fhg.igd.mapviewer.geom.algorithm.sweepline.SweepLine;
import de.fhg.igd.mapviewer.geom.algorithm.sweepline.SweepLineSegment;

/**
 * This class describes closed 2D polylines with straight segments. The
 * underlying Point2D sequence may be explicitly closed or not. This shouldn't
 * make a difference for spatial computations, but may affect some operations.
 * 
 * @author Thorsten Reitz
 */
public class Polygon extends Shape {

	/**
	 * The class' serial version UID
	 */
	private static final long serialVersionUID = 3575023575724612793L;

	/**
	 * The BoundingBox of this Polygon
	 */
	private BoundingBox boundingBox;

	/**
	 * Default Constructor.
	 */
	public Polygon() {
		super();
	}

	/**
	 * Constructor with zero ID and a given Point2D[].
	 * 
	 * @param points An Array of Point2D objects.
	 */
	public Polygon(Point2D[] points) {
		this();
		this.setPoints(points);
	}

	// functional methods ......................................................

	/**
	 * @return true if the given Point2D is inside this Polygon.
	 * @see #contains(double, double)
	 * @param p2d the Point that may be inside of this Polygon
	 */
	private boolean contains(Point2D p2d) {
		return contains(p2d.getX(), p2d.getY());
	}

	/**
	 * @return true if the given point is inside this Polygon. Uses the standard
	 *         contains algorithm that checks how often a ray projected form the
	 *         point parallel to the y axis cuts a segment of the polygon. If
	 *         the number is even, it's outide, if it is odd, it's inside.
	 * @param x the x ordinate of the point that may be inside of this Polygon
	 * @param y the y ordinate of the point that may be inside of this Polygon
	 */
	private boolean contains(double x, double y) {
		// First, make sure that this Polygon is at least a triangle, otherwise
		// it has no area. Also, test if the point is within this Polygon's
		// Extent.
		if (this.getPoints().length <= 2 || this.inExtent(x, y) == false) {
			return false;
		}

		int hits = 0;
		int points_length = this.getPoints().length;
		// save the coordinates of the last point.
		double lastx = this.getPoints()[points_length - 1].getX();
		double lasty = this.getPoints()[points_length - 1].getY();

		// variables for the current point.
		double curx;
		double cury;

		// Now, test all edges of the polygon.
		for (int i = 0; i < points_length; lastx = curx, lasty = cury, i++) {
			curx = this.getPoints()[i].getX();
			cury = this.getPoints()[i].getY();
			if (cury == lasty) {
				continue;
			}
			double leftx;
			if (curx < lastx) {
				if (x >= lastx) {
					continue;
				}
				leftx = curx;
			}
			else {
				if (x >= curx) {
					continue;
				}
				leftx = lastx;
			}
			double test1, test2;
			if (cury < lasty) {
				if (y < cury || y >= lasty) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - curx;
				test2 = y - cury;
			}
			else {
				if (y < lasty || y >= cury) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - lastx;
				test2 = y - lasty;
			}
			if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
				hits++;
			}
		}
		return ((hits & 1) != 0);
	}

	/**
	 * Checks if the extent lies within the polygon
	 * 
	 * @param e the extent
	 * @return true if e lies within the polygon
	 */
	public boolean contains(Extent e) {
		return this.contains(e.getMinX(), e.getMinY()) && this.contains(e.getMaxX(), e.getMinY())
				&& this.contains(e.getMinX(), e.getMaxY())
				&& this.contains(e.getMaxX(), e.getMaxY());
	}

	/**
	 * Adds a Polygon to a Point2DEventQueue queue.
	 * 
	 * @param p the Polygon to add
	 * @param q the queue to add to
	 * @return a list of line2d objects which are vertical and which could not
	 *         be added.
	 */
	private static List<Line2D> addToPoint2DEventQueue(Polygon p, Point2DEventQueue q) {
		List<Line2D> verticalLines = new ArrayList<Line2D>();

		// add line segments
		for (int i = 0; i < p.getPoints().length - 1; ++i) {
			Point2D p1 = p.getPoints()[i];
			Point2D p2 = p.getPoints()[i + 1];
			if (p1.equals(p2)) {
				// don't add degenerated lines
				continue;
			}

			Line2D l = new Line2D(new Point2D[] { p1, p2 });
			if (p1.getX() == p2.getX()) {
				// save this vertical line, but don't add it
				verticalLines.add(l);
				continue;
			}

			q.add(l, p);
		}

		return verticalLines;
	}

	/**
	 * Tests if this Polygon intersects another one
	 * 
	 * @param p the other Polygon
	 * @return true if this Polygon intersects the other one, false otherwise
	 */
	private boolean intersects(Polygon p) {
		// initialize EventQueue and add this Polygon and p
		Point2DEventQueue eq = new Point2DEventQueue();
		List<Line2D> v1 = addToPoint2DEventQueue(this, eq);
		List<Line2D> v2 = addToPoint2DEventQueue(p, eq);

		// check vertical lines separately
		for (Line2D vl : v1) {
			for (int i = 0; i < p.getPoints().length - 1; ++i) {
				Point2D p1 = p.getPoints()[i];
				Point2D p2 = p.getPoints()[i + 1];
				if (doLinesIntersect(vl.getPoints()[0], vl.getPoints()[1], p1, p2)) {
					return true;
				}
			}
		}

		for (Line2D vl : v2) {
			for (int i = 0; i < this.getPoints().length - 1; ++i) {
				Point2D p1 = this.getPoints()[i];
				Point2D p2 = this.getPoints()[i + 1];
				if (doLinesIntersect(vl.getPoints()[0], vl.getPoints()[1], p1, p2)) {
					return true;
				}
			}
		}

		// initialize Sweep-line
		SweepLine sl = new SweepLine();

		while (eq.size() > 0) {
			Point2DEvent e = eq.remove();
			if (e.isLeft()) {
				Line2D sege = e.getLineSegment();
				SweepLineSegment sls = sl.add(e);

				// get elements above and below sege
				SweepLineSegment above = sl.getAboveSegment(sls);
				SweepLineSegment below = sl.getBelowSegment(sls);
				if (above != null) {
					if (sls.getPolygon() != above.getPolygon()) {
						// check for intersection between sege and the above
						// segment
						Line2D sega = above.getLine();
						if (doLinesIntersect(sege.getPoints()[0], sege.getPoints()[1],
								sega.getPoints()[0], sega.getPoints()[1])) {
							return true;
						}
					}
				}
				if (below != null) {
					if (sls.getPolygon() != below.getPolygon()) {
						// check for intersection between sege and the above
						// segment
						Line2D segb = below.getLine();
						if (doLinesIntersect(sege.getPoints()[0], sege.getPoints()[1],
								segb.getPoints()[0], segb.getPoints()[1])) {
							return true;
						}
					}
				}
			}
			else {
				SweepLineSegment sls = sl.get(e);

				// get elements above and below sege
				SweepLineSegment above = sl.getAboveSegment(sls);
				SweepLineSegment below = sl.getBelowSegment(sls);

				// remove e from sweep-line
				sl.remove(sls);

				// check for intersection between the segments above and below
				if (above != null && below != null) {
					if (above.getPolygon() != below.getPolygon()) {
						Line2D sega = above.getLine();
						Line2D segb = below.getLine();
						if (doLinesIntersect(sega.getPoints()[0], sega.getPoints()[1],
								segb.getPoints()[0], segb.getPoints()[1])) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * Tests if this Polygon completely contains another one. First it checks
	 * for intersection (because the two Polygons must not intersect). Then it
	 * checks if this Polygon contains at least one point (actually the first
	 * one) of the other Polygon.
	 * 
	 * @param p the other Polygon
	 * @return true if this Polygon completely contains p, false otherwise
	 */
	public boolean contains(Polygon p) {
		if (this.intersects(p)) {
			return false;
		}
		return this.contains(p.points[0]);
	}

	/**
	 * <p>
	 * This method will test if there is an intersection point between the
	 * vectors defined by (a1,a2) and (b1,b2). If there is none, it will return
	 * null, otherwise it will return the parameter (usually called "lambda")
	 * where the intersection point can be found on the first line.
	 * </p>
	 * <p>
	 * <b>Attention</b>: This method may also return lambda values lower than
	 * 0.0 or greater than 1.0. In this case the intersection point lies outside
	 * the first line!
	 * </p>
	 * <p>
	 * <b>Attention</b>: If the result value is
	 * <code>0.0 <= lambda <= 1.0</code> this does not mean that the
	 * intersection point lies on both lines in all cases. This method
	 * intersects vectors and so the intersection point may lie on the first
	 * line but not on the second one. If you want to make sure the intersection
	 * point lies on both lines, always call this method as follows:
	 * </p>
	 * 
	 * <pre>
	 * Double lambda1 = findIntersectionParameter(a1x, a1y, a2x, a2y, b1x, b1y, b2x, b2y);
	 * if (lambda1 == null || lambda1 < 0.0 || lambda1 > 1.0) {
	 * 	// there is no intersection point!
	 * }
	 * Double lambda2 = findIntersectionParameter(b1x, b1y, b2x, b2y, a1x, a1y, a2x, a2y);
	 * if (lambda1 == null || lambda1 < 0.0 || lambda1 > 1.0) {
	 * 	// there is no intersection point!
	 * }
	 * //there is an intersection point:
	 * double dx = a2x - a1x;
	 * double dy = a2y - a1y;
	 * Point2D intersectionPoint = new Point2D(a1x + lambda * dx, a1y + lambda * dy);
	 * </pre>
	 * 
	 * @param a1x the x ordinate of the first point of the first line
	 * @param a1y the y ordinate of the first point of the first line
	 * @param a2x the x ordinate of the second point of the first line
	 * @param a2y the y ordinate of the second point of the first line
	 * @param b1x the x ordinate first point of the second line
	 * @param b1y the y ordinate first point of the second line
	 * @param b2x the x ordinate second point of the second line
	 * @param b2y the y ordinate second point of the second line
	 * @return lambda
	 */
	private static Double findIntersectionParameter(double a1x, double a1y, double a2x, double a2y,
			double b1x, double b1y, double b2x, double b2y) {
		double abx = a2x - a1x;
		double aby = a2y - a1y;
		double bbx = b2x - b1x;
		double bby = b2y - b1y;

		// calculate first Determinant value:
		double D1 = abx * bby - aby * bbx;

		// if the determinant is not 0, there is an intersection point and thus,
		// a solution to the LGS. We do not check againt exaclty 0 since
		// this could lead to false negatives with double values.
		if (Math.abs(D1) > 0.0000001) {
			return ((b1x - a1x) * bby - (b1y - a1y) * bbx) / D1;
		}
		return null;
	}

	/**
	 * <p>
	 * This method will test if there is an intersection point between the lines
	 * defined by (a1,a2) and (b1,b2). If there is none, it will return null,
	 * otherwise it will return the coordinate of the intersection point.
	 * </p>
	 * <p>
	 * Other that
	 * {@link #findIntersectionParameter(double, double, double, double, double, double, double, double)}
	 * this method makes sure the intersection point lies on the given lines.
	 * </p>
	 * 
	 * @param a1 the first point of the first line
	 * @param a2 the second point of the first line
	 * @param b1 the first point of the second line
	 * @param b2 the second point of the second line
	 * @return true if there is an intersection
	 */
	private static boolean doLinesIntersect(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
		double a1x = a1.getX();
		double a1y = a1.getY();
		double a2x = a2.getX();
		double a2y = a2.getY();

		double b1x = b1.getX();
		double b1y = b1.getY();
		double b2x = b2.getX();
		double b2y = b2.getY();

		Double s = findIntersectionParameter(a1x, a1y, a2x, a2y, b1x, b1y, b2x, b2y);
		if (s == null || s < 0.0 || s > 1.0) {
			return false;
		}

		// double check the other line
		Double s2 = findIntersectionParameter(b1x, b1y, b2x, b2y, a1x, a1y, a2x, a2y);
		if (s2 == null || s2 < 0.0 || s2 > 1.0) {
			return false;
		}

		return true;
	}

	/**
	 * This method tests if a given point falls within this Polygon's Extent.
	 * 
	 * @param x the x ordinate of the point
	 * @param y the y ordinate
	 * @return true if the point falls within this Polygon's Extent
	 */
	private boolean inExtent(double x, double y) {
		if (x >= this.getBoundingBox().getMinX() && x <= this.getBoundingBox().getMaxX()
				&& y >= this.getBoundingBox().getMinY() && y <= this.getBoundingBox().getMaxY()) {
			return true;
		}

		return false;
	}

	/**
	 * Converts this Polygon to an AWT Polygon. Only suitable for output, is not
	 * null-safe!
	 * 
	 * @param scale_x the scale factor in x direction
	 * @param scale_y the scale factor in y direction
	 * @param offset the offset to be added to the converted Polygon
	 * @return the converted Polygon
	 */
	public java.awt.Polygon toAWTPolygon(double scale_x, double scale_y, Point2D offset) {
		double offset_x = 0;
		double offset_y = 0;
		if (offset != null) {
			offset_x = offset.getX();
			offset_y = offset.getY();
		}

		java.awt.Polygon poly = new java.awt.Polygon();
		int[] xpoints = new int[this.getPoints().length];
		int[] ypoints = new int[this.getPoints().length];
		for (int i = 0; i < this.getPoints().length; i++) {
			xpoints[i] = (int) ((this.getPoints()[i].getX() - offset_x) * scale_x);
			ypoints[i] = (int) ((this.getPoints()[i].getY() - offset_y) * scale_y);
		}
		poly.xpoints = xpoints;
		poly.ypoints = ypoints;
		poly.npoints = this.getPoints().length;
		return poly;
	}

	/**
	 * @return The BoundingBox for this Polygon. Takes into account possible
	 *         Transformations.
	 */
	@Override
	public BoundingBox getBoundingBox() {
		if (this.boundingBox == null) {
			this.boundingBox = BoundingBox.compute(this.points);
		}
		return this.boundingBox;
	}

	@Override
	public void setPoints(Point2D[] points) {
		super.setPoints(points);
		boundingBox = null;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (!(o instanceof Polygon)) {
			return false;
		}
		return true;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * standard toString() method. Contains call to super.toString().
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Polygon[");
		buffer.append(super.toString());
		buffer.append("]");
		return buffer.toString();
	}
}
