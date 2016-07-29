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

import java.io.Serializable;
import java.util.Arrays;

import de.fhg.igd.mapviewer.geom.Localizable;
import de.fhg.igd.mapviewer.geom.Point2D;
import de.fhg.igd.mapviewer.geom.util.BlochHashCode;

/**
 * More or less what <code>geom.feature.Geometry_2d</code> was in the older
 * versions. Shapes are pure 2D objects to begin with, but can be used freely in
 * 3D space using the <code>Transform</code> objects.
 * 
 * @author Thorsten Reitz
 */
public abstract class Shape implements Localizable, Serializable {

	/**
	 * The class' serial version UID
	 */
	private static final long serialVersionUID = -6411820671639726195L;

	/**
	 * The 2D Points that define a Shape.
	 */
	protected Point2D[] points;

	/**
	 * Default constructor
	 */
	public Shape() {
		super();
	}

	// functional methods ......................................................

	// canonical java methods ..................................................

	/**
	 * This method checks if two given shapes are equal in its points
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Shape)) {
			return false;
		}

		Shape other = (Shape) o;
		Point2D[] thisPoints = this.getPoints();
		Point2D[] otherPoints = other.getPoints();

		return Arrays.equals(thisPoints, otherPoints);
	}

	@Override
	public int hashCode() {
		int hash = BlochHashCode.HASH_CONSTANT;
		for (Point2D p2d : getPoints()) {
			hash = BlochHashCode.addFieldToHash(hash, p2d);
		}
		return hash;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Shape[");
		buffer.append(super.toString());
		if (points == null) {
			buffer.append("points = ").append("null");
		}
		else {
			buffer.append("points = ").append(Arrays.asList(points).toString());
		}
		buffer.append("]");
		return buffer.toString();
	}

	// getters / setters .......................................................

	/**
	 * @return the Point2D Array.
	 */
	public Point2D[] getPoints() {
		return points;
	}

	/**
	 * @param points The Point2D[] to set.
	 */
	public void setPoints(Point2D[] points) {
		this.points = points;
	}

	/**
	 * Inverts the order of the shape's points
	 */
	public void reverse() {
		int left = 0;
		if (points == null)
			return;
		int right = points.length - 1;
		while (left < right) {
			Point2D temp = points[left];
			points[left] = points[right];
			points[right] = temp;
			++left;
			--right;
		}
	}
}
