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

package de.fhg.igd.geom.algorithm.sweepline;

import de.fhg.igd.geom.Point2D;
import de.fhg.igd.geom.shape.Line2D;
import de.fhg.igd.geom.shape.Polygon;
import de.fhg.igd.geom.util.BlochHashCode;

/**
 * Wraps around a Point2D and marks it as left-end point or right-end point.
 * 
 * @author Michel Kraemer
 */
public class Point2DEvent extends Point2D {

	/**
	 * The serial version UID
	 */
	private static final long serialVersionUID = -3964554499606572201L;

	/**
	 * The _line segment p is assigned to
	 */
	private final Line2D _line;

	/**
	 * True if it is a left-end point, false if it is a right-end point
	 */
	private final boolean _isLeft;

	/**
	 * The Polygon this Point2DEvent belongs to
	 */
	private final Polygon _poly;

	/**
	 * Constructs a new EndPoint
	 * 
	 * @param p the point to wrap around
	 * @param line the _line segment p is assigned to
	 * @param isLeft true if it is a left-end point, false if it is a right-end
	 *            point
	 * @param poly the Polygon this Point2DEvent belongs to
	 */
	public Point2DEvent(Point2D p, Line2D line, boolean isLeft, Polygon poly) {
		super(p.getX(), p.getY());
		_line = line;
		_isLeft = isLeft;
		_poly = poly;
	}

	/**
	 * @return true if it is a left-end point, false if it is a right-end point
	 */
	public boolean isLeft() {
		return _isLeft;
	}

	/**
	 * @return the _line segment p is assigned to
	 */
	public Line2D getLineSegment() {
		return _line;
	}

	/**
	 * @return the Polygon this Point2DEvent belongs to
	 */
	public Polygon getPolygon() {
		return _poly;
	}

	/**
	 * @see Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Point2DEvent)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		Point2DEvent p = (Point2DEvent) o;
		if (p._isLeft != _isLeft) {
			return false;
		}
		if (!p._line.equals(_line)) {
			return false;
		}

		return true;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = BlochHashCode.addFieldToHash(hash, _isLeft);
		return BlochHashCode.addFieldToHash(hash, _line);
	}
}
