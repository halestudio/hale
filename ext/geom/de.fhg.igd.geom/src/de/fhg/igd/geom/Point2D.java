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

package de.fhg.igd.geom;

import java.io.Serializable;

import de.fhg.igd.geom.util.BlochHashCode;

/**
 * This is a base class for the description of a Point in 2D space (using double
 * coordinates).
 *
 * @author Thorsten Reitz
 */
public class Point2D implements Localizable, Serializable, Comparable<Object>, Cloneable {

	/**
	 * The class' serial version UID
	 */
	private static final long serialVersionUID = 7085684939727529847L;

	/**
	 * This Point's x coordinate component.
	 */
	private double x;

	/**
	 * This Point's y coordinate component.
	 */
	private double y;

	/**
	 * Default constructor
	 */
	public Point2D() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param x the new Point's x value
	 * @param y the new Point's y value
	 */
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Copy constructor
	 * 
	 * @param p3d the Point3D to use as a template. x and y values will be
	 *            copied. z will be discarded.
	 */
	public Point2D(Point3D p3d) {
		this.x = p3d.getX();
		this.y = p3d.getY();
	}

	// functional methods ......................................................

	/**
	 * returns this Point's BoundingBox. Min values and max values are
	 * identical.
	 * 
	 * @see Localizable#getBoundingBox()
	 */
	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(this.x, this.y, 0, this.x, this.y, 0);
	}

	// canonical java methods ..................................................

	/**
	 * This equals method is consistent with the compareTo() method implemented
	 * in this class, that means, it determines equality by comparing x and y
	 * values.
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}

		if (o instanceof Point2D) {
			Point2D other = (Point2D) o;
			return this.x == other.getX() && this.y == other.getY();
		}
		return false;
	}

	/**
	 * Provides a hashCode so that x.hashCode() == y.hashCode() when x.equals(y)
	 * == true
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = BlochHashCode.HASH_CONSTANT;
		hash = BlochHashCode.addFieldToHash(hash, this.x);
		return BlochHashCode.addFieldToHash(hash, this.y);
	}

	/**
	 * @return a clone of this object
	 */
	@Override
	public Point2D clone() throws CloneNotSupportedException {
		return (Point2D) super.clone();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		String result = "[Point2D: ";
		return (result + this.getX() + ", " + this.getY() + "]");
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		// Initialize Object that this point is to be compared to.
		Point2D p2d = (Point2D) o;

		// do actual comparison. first, this and evp are only equal if both
		// their coordinates are equal.
		if (p2d.getX() == this.getX() && p2d.getY() == this.getY()) {
			return 0;
		}
		if (this.getX() > p2d.getX()) {
			return 1;
		}
		else if (this.getX() == p2d.getX()) {
			if (this.getY() > p2d.getY()) {
				return 1;
			}
			return -1;
		}
		else {
			return -1;
		}
	}

	// getter/setter methods ...................................................

	/**
	 * @return this point's x value
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return this point's y value
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets this point's x value
	 * 
	 * @param x the new value
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets this point's y value
	 * 
	 * @param y the new value
	 */
	public void setY(double y) {
		this.y = y;
	}

}
