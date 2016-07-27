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

package de.fhg.igd.mapviewer.geom;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import de.fhg.igd.mapviewer.geom.util.BlochHashCode;
import de.fhg.igd.mapviewer.geom.util.MathHelper;

/**
 * This class represents a Bounding Box in 3D, as defined by the lower left
 * minimum height and the upper right maximum height corners.
 *
 * @author Thorsten Reitz
 */
public final class BoundingBox implements Serializable, Localizable, Cloneable {

	/**
	 * The class' serial version UID
	 */
	private static final long serialVersionUID = 713293043787375488L;

	/**
	 * The lower left bottom corner's x value.
	 */
	private double minX;

	/**
	 * The lower left bottom corner's x value.
	 */
	private double minY;

	/**
	 * The lower left bottom corner's z value.
	 */
	private double minZ;

	/**
	 * The upper right top corner's x value.
	 */
	private double maxX;

	/**
	 * The upper right top corner's y value.
	 */
	private double maxY;

	/**
	 * The upper right top corner's z value.
	 */
	private double maxZ;

	/**
	 * Default constructor
	 */
	public BoundingBox() {
		reset();
	}

	/**
	 * Construct a BB from two points defined by three double values.
	 * 
	 * @param x1 - xmin
	 * @param y1 - ymin
	 * @param z1 - zmin
	 * @param x2 - xmax
	 * @param y2 - ymax
	 * @param z2 - zmax
	 */
	public BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.minX = x1;
		this.minY = y1;
		this.minZ = z1;
		this.maxX = x2;
		this.maxY = y2;
		this.maxZ = z2;
	}

	/**
	 * Copy constructor
	 * 
	 * @param boundingBox The source bounding box
	 */
	public BoundingBox(BoundingBox boundingBox) {
		this(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX,
				boundingBox.maxY, boundingBox.maxZ);
	}

	// functional methods
	// .......................................................

	/**
	 * Resets the bounding box
	 */
	public void reset() {
		this.minX = Double.POSITIVE_INFINITY;
		this.minY = Double.POSITIVE_INFINITY;
		this.minZ = Double.POSITIVE_INFINITY;
		this.maxX = Double.NEGATIVE_INFINITY;
		this.maxY = Double.NEGATIVE_INFINITY;
		this.maxZ = Double.NEGATIVE_INFINITY;
	}

	/**
	 * @see Localizable#getBoundingBox()
	 */
	@Override
	public BoundingBox getBoundingBox() {
		return this;
	}

	/**
	 * This method expands this BoundingBox in volume by factor stepsize. If the
	 * original extent is 0.0, stepsize will be used as absolute value.
	 * 
	 * @param stepsize the size factor
	 * @return the extended BoundingBox (this)
	 * @see Localizable#getBoundingBox()
	 */
	public BoundingBox expand(double stepsize) {
		double delta_x = this.maxX - this.minX;
		double delta_y = this.maxY - this.minY;
		double delta_z = this.maxZ - this.minZ;

		if (delta_x == 0.0) {
			delta_x = stepsize;
		}
		else {
			delta_x = delta_x * (1 + stepsize);
		}
		if (delta_y == 0.0) {
			delta_y = stepsize;
		}
		else {
			delta_y = delta_y * (1 + stepsize);
		}
		if (delta_z == 0.0) {
			delta_z = stepsize;
		}
		else {
			delta_z = delta_z * (1 + stepsize);
		}

		double min_temp = (this.maxX + this.minX) / 2 - delta_x / 2;
		this.maxX = (this.maxX + this.minX) / 2 + delta_x / 2;
		this.minX = min_temp;

		min_temp = (this.maxY + this.minY) / 2 - delta_y / 2;
		this.maxY = (this.maxY + this.minY) / 2 + delta_y / 2;
		this.minY = min_temp;

		min_temp = (this.maxZ + this.minZ) / 2 - delta_z / 2;
		this.maxZ = (this.maxZ + this.minZ) / 2 + delta_z / 2;
		this.minZ = min_temp;

		return this;
	}

	/**
	 * This method will add the given Bounding Box to this Bounding Box, thus
	 * possibly enlarging it.
	 * 
	 * @param bbox the BoundingBox to add
	 */
	public void add(BoundingBox bbox) {
		if (bbox == null) {
			return;
		}
		Point3D center = bbox.getCenter();
		if (!(Double.isNaN(center.getX()) || Double.isNaN(center.getY())
				|| Double.isNaN(center.getZ()))) {
			if (bbox.covers(this)) {
				this.setMinX(bbox.getMinX());
				this.setMaxX(bbox.getMaxX());
				this.setMinY(bbox.getMinY());
				this.setMaxY(bbox.getMaxY());
				this.setMinZ(bbox.getMinZ());
				this.setMaxZ(bbox.getMaxZ());
			}
			else if (this.covers(bbox)) {
				// do nothing
			}
			else {
				if (this.minX > bbox.minX) {
					this.minX = bbox.minX;
				}
				if (this.minY > bbox.minY) {
					this.minY = bbox.minY;
				}
				if (this.minZ > bbox.minZ) {
					this.minZ = bbox.minZ;
				}
				if (this.maxX < bbox.maxX) {
					this.maxX = bbox.maxX;
				}
				if (this.maxY < bbox.maxY) {
					this.maxY = bbox.maxY;
				}
				if (this.maxZ < bbox.maxZ) {
					this.maxZ = bbox.maxZ;
				}
			}
		}
	}

	/**
	 * @return the center of this BB as a Point3D.
	 */
	public Point3D getCenter() {
		return new Point3D((this.maxX + this.minX) / 2, (this.maxY + this.minY) / 2,
				(this.maxZ + this.minZ) / 2);
	}

	/**
	 * @param bb the BoundingBox that may have any relation to this one
	 * @return true if the given BoundingBox has any spatial relation to this
	 *         one.
	 */
	public boolean any(BoundingBox bb) {
		return (this.intersects(bb) || this.covers(bb) || bb.covers(this) || bb.equals(this)
				|| bb.touches(this));
	}

	/**
	 * This checks if this Bounding Box completely covers the parameter Bounding
	 * Box.
	 * 
	 * @param bbox the other BoundingBox
	 * @return true if this Box covers bbox, false otherwise
	 */
	public boolean covers(BoundingBox bbox) {
		return (this.getMinX() <= bbox.getMinX() && this.getMaxX() >= bbox.getMaxX()
				&& this.getMinY() <= bbox.getMinY() && this.getMaxY() >= bbox.getMaxY()
				&& this.getMinZ() <= bbox.getMinZ() && this.getMaxZ() >= bbox.getMaxZ());
	}

	/**
	 * This checks if this Bounding Box completely contains the parameter
	 * Bounding Box.
	 * 
	 * @param bbox the other BoundingBox
	 * @return true if this Box contains bbox, false otherwise
	 */
	private boolean contains(BoundingBox bbox) {
		return (this.getMinX() < bbox.getMinX() && this.getMaxX() > bbox.getMaxX()
				&& this.getMinY() < bbox.getMinY() && this.getMaxY() > bbox.getMaxY()
				&& this.getMinZ() < bbox.getMinZ() && this.getMaxZ() > bbox.getMaxZ());
	}

	/**
	 * @param point a point that may touch this BoundingBox
	 * @return true if the given Point touches the BoundingBox
	 */
	private boolean touches(Point3D point) {
		// We keep the "unsafe" FP comparison since we prefer a proper touches()
		// over
		// something which cannot hold basic topological properties. That
		// touches isn't the most stable criteria is another thing.
		if (point.getX() == this.minX || point.getX() == this.maxX) {
			if (point.getY() <= this.maxY && point.getY() >= this.minY && point.getZ() <= this.maxZ
					&& point.getZ() >= this.minZ) {
				return true;
			}
		}
		if (point.getY() == this.minY || point.getY() == this.maxY) {
			if (point.getX() <= this.maxX && point.getX() >= this.minX && point.getZ() <= this.maxZ
					&& point.getZ() >= this.minZ) {
				return true;
			}
		}
		if (point.getZ() == this.minZ || point.getZ() == this.maxZ) {
			if (point.getX() <= this.maxX && point.getX() >= this.minX && point.getY() <= this.maxY
					&& point.getY() >= this.minY) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param bb the BoundingBox that may touch this one
	 * @return true if the given BoundingBox has corners on an edge of this one
	 */
	private boolean touchesHelper(BoundingBox bb) {
		int count = 0;
		if (this.touches(new Point3D(bb.minX, bb.minY, bb.minZ))) {
			count++;
		}
		if (this.touches(new Point3D(bb.minX, bb.minY, bb.maxZ))) {
			count++;
		}
		if (this.touches(new Point3D(bb.minX, bb.maxY, bb.minZ))) {
			count++;
		}
		if (this.touches(new Point3D(bb.minX, bb.maxY, bb.maxZ))) {
			count++;
		}
		if (this.touches(new Point3D(bb.maxX, bb.minY, bb.minZ))) {
			count++;
		}
		if (this.touches(new Point3D(bb.maxX, bb.minY, bb.maxZ))) {
			count++;
		}
		if (this.touches(new Point3D(bb.maxX, bb.maxY, bb.minZ))) {
			count++;
		}
		if (this.touches(new Point3D(bb.maxX, bb.maxY, bb.maxZ))) {
			count++;
		}
		return count > 0 && count < 5;
	}

	/**
	 * @param bb the BoundingBox that may touch this one
	 * @return true if the given BoundingBox touches this one
	 */
	private boolean touches(BoundingBox bb) {
		return (touchesHelper(bb) || bb.touchesHelper(this)) && !this.intersects(bb)
				&& !this.covers(bb) && !bb.covers(this);
	}

	/**
	 * Test emptiness, i.e. whether at least one axis has zero width and the box
	 * is regular.
	 * 
	 * @return whether the bounding box is empty.
	 */
	public boolean isEmpty() {
		return (getWidth() == 0 || getHeight() == 0 || getDepth() == 0) && isRealValued();
	}

	/**
	 * Test regularity, i.e. whether all axes have zero or positive length.
	 * 
	 * @return whether the bounding box is regular.
	 * @see #checkIntegrity()
	 */
	private boolean isRegular() {
		return getWidth() >= 0 && getHeight() >= 0 && getDepth() >= 0;
	}

	/**
	 * This checks if this Bounding Box intersects with another Bounding Box.
	 * 
	 * @param bb the other BoundingBox
	 * @return true if the Boxes intersect, false otherwise
	 */
	public boolean intersects(BoundingBox bb) {
		if (this.getMinX() >= bb.getMaxX() || this.getMaxX() <= bb.getMinX()) {
			return false;
		}
		else if (this.getMinY() >= bb.getMaxY() || this.getMaxY() <= bb.getMinY()) {
			return false;
		}
		else if (this.getMinZ() >= bb.getMaxZ() || this.getMaxZ() <= bb.getMinZ()) {
			return false;
		}
		return (!this.contains(bb) && !this.equals(bb));
	}

	/**
	 * Checks if this BoundingBox intersects with or covers the given
	 * BoundingBox bb
	 * 
	 * @param bb the BoundingBox to check against
	 * @return true if this BoundingBox intersects with or covers bb, false
	 *         otherwise
	 */
	public boolean intersectsOrCovers(BoundingBox bb) {
		return (this.intersects(bb) || this.covers(bb));
	}

	/**
	 * This method is used internally to add a point to a given extent
	 * 
	 * @param x the x ordinate of the point to add
	 * @param y the y ordinate
	 * @param result an array containing the current extent. This array will be
	 *            updated by this method. The structure is as follows:<br />
	 *            result[0] - maximum x<br />
	 *            result[1] - minimum x<br />
	 *            result[2] - maximum y<br />
	 *            result[3] - minimum y
	 */
	private static void computeInternal(double x, double y, double[] result) {
		if (x > result[0]) {
			result[0] = x;
		}
		if (x < result[1]) {
			result[1] = x;
		}
		if (y > result[2]) {
			result[2] = y;
		}
		if (y < result[3]) {
			result[3] = y;
		}
	}

	/**
	 * This method is used internally to add a point to a given bounding box
	 * 
	 * @param x the x ordinate of the point to add
	 * @param y the y ordinate
	 * @param z the z ordinate
	 * @param result an array containing the current bounding box. This array
	 *            will be updated by this method. The structure is as follows:
	 *            <br />
	 *            result[0] - maximum x<br />
	 *            result[1] - minimum x<br />
	 *            result[2] - maximum y<br />
	 *            result[3] - minimum y<br />
	 *            result[4] - maximum z<br />
	 *            result[5] - minimum z<br />
	 */
	private static void computeInternal(double x, double y, double z, double[] result) {
		computeInternal(x, y, result);
		if (z > result[4]) {
			result[4] = z;
		}
		if (z < result[5]) {
			result[5] = z;
		}
	}

	/**
	 * This static method will return a BoundingBox for a given Array of
	 * Localizables.
	 * 
	 * @param locs the Localizables to add to the BoundingBox
	 * @return a BoundingBox that contains all Localizables
	 */
	public static BoundingBox compute(Localizable[] locs) {
		return computeLocalizable(Arrays.asList(locs));
	}

	/**
	 * This static method will return a BoundingBox for a given collection of
	 * Point2D objects
	 * 
	 * @param points a collection with Point3D objects
	 * @return a BoundingBox containing all points
	 */
	public static BoundingBox computePoint2D(Collection<Point2D> points) {
		double[] result = new double[4];
		result[0] = Double.NEGATIVE_INFINITY;
		result[1] = Double.POSITIVE_INFINITY;
		result[2] = Double.NEGATIVE_INFINITY;
		result[3] = Double.POSITIVE_INFINITY;

		for (Point2D p2d : points) {
			computeInternal(p2d.getX(), p2d.getY(), result);
		}

		BoundingBox bb = new BoundingBox();
		bb.setMaxX(result[0]);
		bb.setMinX(result[1]);
		bb.setMaxY(result[2]);
		bb.setMinY(result[3]);
		bb.setMinZ(0.0);
		bb.setMaxZ(0.0);

		return bb;
	}

	/**
	 * This static method will return a BoundingBox for a given collection of
	 * Point3D objects
	 * 
	 * @param points a collection with Point3D objects
	 * @return a BoundingBox containing all points
	 */
	private static BoundingBox computePoint3D(Collection<Point3D> points) {
		double[] result = new double[6];
		result[0] = Double.NEGATIVE_INFINITY;
		result[1] = Double.POSITIVE_INFINITY;
		result[2] = Double.NEGATIVE_INFINITY;
		result[3] = Double.POSITIVE_INFINITY;
		result[4] = Double.NEGATIVE_INFINITY;
		result[5] = Double.POSITIVE_INFINITY;

		for (Point3D p3d : points) {
			computeInternal(p3d.getX(), p3d.getY(), p3d.getZ(), result);
		}

		BoundingBox bb = new BoundingBox();
		bb.setMaxX(result[0]);
		bb.setMinX(result[1]);
		bb.setMaxY(result[2]);
		bb.setMinY(result[3]);
		bb.setMaxZ(result[4]);
		bb.setMinZ(result[5]);

		return bb;
	}

	/**
	 * This static method will return a BoundingBox for a given collection of
	 * Localizable objects
	 * 
	 * @param locs a collection of Localizable objects
	 * @return a BoundingBox containing all Localizables
	 */
	private static BoundingBox computeLocalizable(Iterable<? extends Localizable> locs) {
		double[] result = new double[6];
		result[0] = Double.NEGATIVE_INFINITY;
		result[1] = Double.POSITIVE_INFINITY;
		result[2] = Double.NEGATIVE_INFINITY;
		result[3] = Double.POSITIVE_INFINITY;
		result[4] = Double.NEGATIVE_INFINITY;
		result[5] = Double.POSITIVE_INFINITY;

		for (Localizable loc : locs) {
			BoundingBox locbb = loc.getBoundingBox();
			computeInternal(locbb.minX, locbb.minY, locbb.minZ, result);
			computeInternal(locbb.maxX, locbb.maxY, locbb.maxZ, result);
		}

		BoundingBox bb = new BoundingBox();
		bb.setMaxX(result[0]);
		bb.setMinX(result[1]);
		bb.setMaxY(result[2]);
		bb.setMinY(result[3]);
		bb.setMaxZ(result[4]);
		bb.setMinZ(result[5]);

		return bb;
	}

	/**
	 * This static method will return a BoundingBox for a given Array of Point3D
	 * objects.
	 * 
	 * @param points the array of points
	 * @return a BoundingBox containing all points
	 */
	public static BoundingBox compute(Point3D[] points) {
		return computePoint3D(Arrays.asList(points));
	}

	/**
	 * This static method will return a BoundingBox for a given Array of Point2D
	 * objects.
	 * 
	 * @param points the array of points
	 * @return a BoundingBox containing all points
	 */
	public static BoundingBox compute(Point2D[] points) {
		return computePoint2D(Arrays.asList(points));
	}

	/**
	 * checks if min* and max* are actually in the expected relation. If a pair
	 * is real and in the wrong order, it is swapped.
	 */
	public void normalize() {
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
		if (MathHelper.isReal(minZ) && MathHelper.isReal(maxZ) && maxZ < minZ) {
			double tmp = minZ;
			minZ = maxZ;
			maxZ = tmp;
		}
	}

	/**
	 * This method will return a 2D Extent that uses only the X and Y
	 * coordinates of this BoundingBox.
	 * 
	 * @return Extent
	 */
	public Extent toExtent() {
		Extent result = new Extent();
		result.setMinX(this.minX);
		result.setMinY(this.minY);
		result.setMaxX(this.maxX);
		result.setMaxY(this.maxY);
		return result;
	}

	/**
	 * @return this Bounding Box' Width (along x axis).
	 */
	public double getWidth() {
		return this.maxX - this.minX;
	}

	/**
	 * @return this Bounding Box' Height (along y axis).
	 */
	public double getHeight() {
		return this.maxY - this.minY;
	}

	/**
	 * @return this Bounding Box' Depth (along z axis).
	 */
	public double getDepth() {
		return this.maxZ - this.minZ;
	}

	// canonical java methods
	// ...................................................

	/**
	 * Two BoundingBoxes are defined as being equal when their LLB and URT
	 * coordinates are equal.
	 * 
	 * @see Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o != null && o instanceof BoundingBox) {
			BoundingBox other = (BoundingBox) o;
			if (this.getMinX() == other.getMinX() && this.getMinY() == other.getMinY()
					&& this.getMinZ() == other.getMinZ() && this.getMaxX() == other.getMaxX()
					&& this.getMaxY() == other.getMaxY() && this.getMaxZ() == other.getMaxZ()) {
				return true;
			}
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
		int hash = BlochHashCode.addFieldToHash(BlochHashCode.HASH_CONSTANT, this.getMinX());
		hash = BlochHashCode.addFieldToHash(hash, this.getMinY());
		hash = BlochHashCode.addFieldToHash(hash, this.getMinZ());
		hash = BlochHashCode.addFieldToHash(hash, this.getMaxX());
		hash = BlochHashCode.addFieldToHash(hash, this.getMaxY());
		return BlochHashCode.addFieldToHash(hash, this.getMaxZ());
	}

	/**
	 * Standard issue toString method.
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("BoundingBox[");
		buffer.append("maxX = ").append(maxX);
		buffer.append(" maxY = ").append(maxY);
		buffer.append(" maxZ = ").append(maxZ);
		buffer.append(" minX = ").append(minX);
		buffer.append(" minY = ").append(minY);
		buffer.append(" minZ = ").append(minZ);
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * @return true if all limits of this bb have been initialized in a
	 *         meaningful way (i.e. not NaN, Infinity or NegativeInfinty) and is
	 *         regular.
	 * @see #isRegular()
	 */
	public boolean checkIntegrity() {
		return isRealValued() && isRegular();
	}

	/**
	 * @return true if all limits of this bb have been initialized in a
	 *         meaningful way (i.e. not NaN, Infinity or NegativeInfinty)
	 */
	private boolean isRealValued() {
		double[] values = new double[] { minX, minY, minZ, maxX, maxY, maxZ };
		boolean result = true;
		for (int i = 0; i < values.length && result == true; i++) {
			if (Double.isNaN(values[i]) || Double.isInfinite(values[i])) {
				result = false;
			}
		}
		return result;
	}

	// getter / setter methods
	// ..................................................

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
	 * Access method for the minZ property.
	 *
	 * @return the current value of the minZ property
	 */
	public double getMinZ() {
		return minZ;
	}

	/**
	 * Sets the value of the minZ property.
	 *
	 * @param aMinZ the new value of the minZ property
	 */
	public void setMinZ(double aMinZ) {
		minZ = aMinZ;
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
	 * Access method for the maxZ property.
	 *
	 * @return the current value of the maxZ property
	 */
	public double getMaxZ() {
		return maxZ;
	}

	/**
	 * Sets the value of the maxZ property.
	 *
	 * @param aMaxZ the new value of the maxZ property
	 */
	public void setMaxZ(double aMaxZ) {
		maxZ = aMaxZ;
	}

	/**
	 * @see Object#clone()
	 */
	@Override
	public Object clone() {
		BoundingBox boundingBox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
		return boundingBox;
	}
}
