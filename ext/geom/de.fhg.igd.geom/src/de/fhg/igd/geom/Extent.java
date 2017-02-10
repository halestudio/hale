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
import de.fhg.igd.geom.util.MathHelper;

/**
 * This class represents an Extent in 2D, as defined by the lower left and upper
 * right corners.
 *
 * @author Thorsten Reitz
 */
public final class Extent implements Serializable, Localizable, Comparable<Extent>, Cloneable {

	/**
	 * The class' serial version UID
	 */
	private static final long serialVersionUID = 7033392945091905596L;

	// member variables ........................................................

	/**
	 * lower left corner, x value.
	 */
	private double minX;

	/**
	 * lower left corner, y value.
	 */
	private double minY;

	/**
	 * upper right corner, x value.
	 */
	private double maxX;

	/**
	 * upper right corner, y value.
	 */
	private double maxY;

	/**
	 * Constructs a new infinitely negative Extent
	 */
	public Extent() {
		this.minX = Double.POSITIVE_INFINITY;
		this.minY = Double.POSITIVE_INFINITY;
		this.maxX = Double.NEGATIVE_INFINITY;
		this.maxY = Double.NEGATIVE_INFINITY;
	}

	/**
	 * Full constructor with all parameters.
	 * 
	 * @param minX lower left corner, x value.
	 * @param minY lower left corner, y value.
	 * @param maxX upper right corner, x value.
	 * @param maxY upper right corner, y value.
	 */
	public Extent(double minX, double minY, double maxX, double maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	/**
	 * Creates an extent such that it spans both points.
	 * 
	 * @param a the lower left corner
	 * @param b the upper right corner
	 */
	public Extent(Point2D a, Point2D b) {
		this.minX = a.getX();
		this.minY = a.getY();
		this.maxX = b.getX();
		this.maxY = b.getY();
		normalize();
	}

	/**
	 * Copy constructor
	 * 
	 * @param e The source extent
	 */
	public Extent(Extent e) {
		this(e.minX, e.minY, e.maxX, e.maxY);
	}

	// functional methods.......................................................

	/**
	 * @param point the point that may be touched by this Extent
	 * @return true if the specified Point2D touches this Extent.
	 */
	private boolean touches(Point2D point) {
		return touches(point.getX(), point.getY());
	}

	/**
	 * Test if a point, specified by two coordinates, touches this extent.
	 * 
	 * @param x x coordinate of point to test
	 * @param y x coordinate of point to test
	 * @return true if the specified Point2D touches this Extent.
	 */
	private boolean touches(double x, double y) {
		// We keep the "unsafe" FP comparison since we prefer a proper touches()
		// over
		// something which cannot hold basic topological properties. That
		// touches isn't the most stable criteria is another thing.
		if (x == this.minX || x == this.maxX) {
			if (y <= this.maxY && y >= this.minY) {
				return true;
			}
		}
		if (y == this.minY || y == this.maxY) {
			if (x <= this.maxX && x >= this.minX) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param ex the other extent
	 * @return true if the given Extent has corners on this extent's edges.
	 */
	private boolean touchesHelper(Extent ex) {
		int touch_counter = 0;
		Point2D[] corner_points = new Point2D[4];
		corner_points[0] = new Point2D(ex.minX, ex.minY);
		corner_points[1] = new Point2D(ex.minX, ex.maxY);
		corner_points[2] = new Point2D(ex.maxX, ex.minY);
		corner_points[3] = new Point2D(ex.maxX, ex.maxY);
		for (int i = 0; i < 4; i++) {
			if (this.touches(corner_points[i])) {
				touch_counter++;
			}
		}
		return touch_counter > 0 && touch_counter < 3;
	}

	/**
	 * @param ex the other extent
	 * @return true if the given Extent touches (and does NOT intersect) this
	 *         Extent.
	 */
	private boolean touches(Extent ex) {
		return (touchesHelper(ex) || ex.touchesHelper(this)) && !this.intersects(ex)
				&& !this.covers(ex) && !ex.covers(this);
	}

	/**
	 * @param ext the Extent that may have any relation to this Extent
	 * @return true if the specified Extent has ANY spacial relation to this
	 *         Extent.
	 */
	public boolean any(Extent ext) {
		return (this.intersects(ext) || this.covers(ext) || ext.covers(this) || ext.equals(this)
				|| ext.touches(this));
	}

	/**
	 * This method will return true if 1..2 corners of the given extent lie
	 * within this Extent. If you want to check for all spatial relationships,
	 * use intersectsOrCovers(Extent). It will also return true if the extents
	 * have a cross-shaped intersections, that is, if no points lie in the other
	 * extent but when the lines of the extent actually cut each other.
	 * 
	 * @param ex the extent that may be intersected by this extent
	 * @return true if this Extent intersects the given one
	 */
	private boolean intersects(Extent ex) {
		if (this.getMinX() >= ex.getMaxX() || this.getMaxX() <= ex.getMinX()) {
			return false;
		}
		else if (this.getMinY() >= ex.getMaxY() || this.getMaxY() <= ex.getMinY()) {
			return false;
		}
		return (!this.contains(ex) && !ex.contains(this) && !this.equals(ex));
	}

	/**
	 * This checks if this Extent covers the parameter extent.
	 * 
	 * @param ex the Extent that may be covered by this Extent
	 * @return true if this Extent covers the given one
	 */
	private boolean covers(Extent ex) {
		return (this.getMinX() <= ex.getMinX() && this.getMaxX() >= ex.getMaxX()
				&& this.getMinY() <= ex.getMinY() && this.getMaxY() >= ex.getMaxY());
	}

	/**
	 * This checks if this Extent completely contains the parameter extent.
	 * 
	 * @param ex the Extent that may be contained by this Extent
	 * @return true if this Extent contains the given one
	 */
	private boolean contains(Extent ex) {
		return (this.getMinX() < ex.getMinX() && this.getMaxX() > ex.getMaxX()
				&& this.getMinY() < ex.getMinY() && this.getMaxY() > ex.getMaxY());
	}

	/**
	 * @return the width (delta of minx and maxX) of this Extent.
	 */
	public double getWidth() {
		return Math.abs(this.getMaxX() - this.getMinX());
	}

	/**
	 * @return the height (delta of minY and maxY) of this Extent.
	 */
	public double getHeight() {
		return Math.abs(this.getMaxY() - this.getMinY());
	}

	// canonical java methods ..................................................

	/**
	 * Two extents are defined as being equal when their LL and UR coordinates
	 * are equal.
	 * 
	 * @param o the extent to compare to
	 * @return true if both extents are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o != null && o instanceof Extent) {
			Extent other = (Extent) o;
			return (this.getMinX() == other.getMinX() && this.getMinY() == other.getMinY()
					&& this.getMaxX() == other.getMaxX() && this.getMaxY() == other.getMaxY());
		}
		return false;
	}

	/**
	 * checks if min* and max* are actually in the expected relation. If a pair
	 * is real and in the wrong order, it is swapped.
	 */
	private void normalize() {
		if (MathHelper.isReal(minX) && MathHelper.isReal(maxX) && maxX < minX) {
			double tmp = minX;
			minX = maxX;
			maxX = tmp;
		}
		if (MathHelper.isReal(minY) && MathHelper.isReal(maxY) && maxY < minY) {
			double tmp = minY;
			minY = maxY;
			maxY = tmp;
		}
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
		hash = BlochHashCode.addFieldToHash(hash, this.getMinX());
		hash = BlochHashCode.addFieldToHash(hash, this.getMinY());
		hash = BlochHashCode.addFieldToHash(hash, this.getMaxX());
		return BlochHashCode.addFieldToHash(hash, this.getMaxY());
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder(80);
		buffer.append("Extent[");
		buffer.append("maxX = ").append(maxX);
		buffer.append(" maxY = ").append(maxY);
		buffer.append(" minX = ").append(minX);
		buffer.append(" minY = ").append(minY);
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * @see de.fhg.igd.mapviewer.geom.Localizable#getBoundingBox()
	 */
	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(this.minX, this.minY, 0d, this.maxX, this.maxY, 0d);
	}

	// getter / setter methods .................................................

	/**
	 * Access method for the minX property.
	 *
	 * @return the current value of the minX property
	 */
	public double getMinX() {
		return minX;
	}

	/**
	 * Sets the value of the minX property.
	 *
	 * @param aMinX the new value of the minX property
	 */
	public void setMinX(double aMinX) {
		minX = aMinX;
	}

	/**
	 * Access method for the minY property.
	 *
	 * @return the current value of the minY property
	 */
	public double getMinY() {
		return minY;
	}

	/**
	 * Sets the value of the minY property.
	 *
	 * @param aMinY the new value of the minY property
	 */
	public void setMinY(double aMinY) {
		minY = aMinY;
	}

	/**
	 * Access method for the maxX property.
	 *
	 * @return the current value of the maxX property
	 */
	public double getMaxX() {
		return maxX;
	}

	/**
	 * Sets the value of the maxX property.
	 *
	 * @param aMaxX the new value of the maxX property
	 */
	public void setMaxX(double aMaxX) {
		maxX = aMaxX;
	}

	/**
	 * Access method for the maxY property.
	 *
	 * @return the current value of the maxY property
	 */
	public double getMaxY() {
		return maxY;
	}

	/**
	 * Sets the value of the maxY property.
	 *
	 * @param aMaxY the new value of the maxY property
	 */
	public void setMaxY(double aMaxY) {
		maxY = aMaxY;
	}

	/**
	 * @return the point at the minimum coordinate on this extent boundary
	 */
	public Point2D getMin() {
		return new Point2D(minX, minY);
	}

	/**
	 * @return the point at the maximum coordinate on this extent boundary
	 */
	public Point2D getMax() {
		return new Point2D(maxX, maxY);
	}

	/**
	 * Compares the area of this extent to another one
	 * 
	 * @param e the other extent to compare to
	 * @return -1, 0, 1 if the area of this extent is greater than, equal to or
	 *         less than the other one.
	 */
	@Override
	public int compareTo(Extent e) {
		double a1 = this.getWidth() * this.getHeight();
		double a2 = e.getWidth() * e.getHeight();
		if (a1 < a2) {
			return -1;
		}
		else if (a1 > a2) {
			return 1;
		}
		return 0;
	}

	/**
	 * @see Object#clone()
	 */
	@Override
	public Object clone() {
		return new Extent(this.minX, this.minY, this.maxX, this.maxY);
	}

}
