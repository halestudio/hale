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

package de.fhg.igd.mapviewer.geom.algorithm.sweepline;

import de.fhg.igd.mapviewer.geom.Point2D;
import de.fhg.igd.mapviewer.geom.shape.Line2D;
import de.fhg.igd.mapviewer.geom.shape.Polygon;

/**
 * Represents a line segment in a sweep-line
 * 
 * @author Michel Kraemer
 */
public class SweepLineSegment {

	/**
	 * The line segement represented by this sweep-line segment
	 */
	private final Line2D _line;

	/**
	 * The left point of line
	 */
	private Point2D _left;

	/**
	 * The right point of line
	 */
	private Point2D _right;

	/**
	 * The Polygon this segment belongs to
	 */
	private final Polygon _poly;

	/**
	 * Default constructor
	 * 
	 * @param line the line segement represented by this sweep-line segment
	 * @param poly the Polygon this segment belongs to
	 */
	public SweepLineSegment(Line2D line, Polygon poly) {
		_line = line;
		_poly = poly;
	}

	/**
	 * Sets the left point of line
	 * 
	 * @param p the left point of line
	 */
	public void setLeftPoint(Point2D p) {
		_left = p;
	}

	/**
	 * Sets the right point of line
	 * 
	 * @param p the right point of line
	 */
	public void setRightPoint(Point2D p) {
		_right = p;
	}

	/**
	 * @return the left point of line
	 */
	public Point2D getLeftPoint() {
		return _left;
	}

	/**
	 * @return the right point of line
	 */
	public Point2D getRightPoint() {
		return _right;
	}

	/**
	 * @return the line segement represented by this sweep-line segment
	 */
	public Line2D getLine() {
		return _line;
	}

	/**
	 * @return the Polygon this segment belongs to
	 */
	public Polygon getPolygon() {
		return _poly;
	}

	/**
	 * Calculates the y value on the line segment for a given x value
	 * 
	 * @param x the x value
	 * @return the y value
	 */
	public double getY(double x) {
		double x2 = x - _left.getX();
		double dx = _right.getX() - _left.getX();
		double dy = _right.getY() - _left.getY();
		double m = dy / dx;
		return _left.getY() + m * x2;
	}
}
