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
import java.util.List;

import de.fhg.igd.mapviewer.geom.util.BlochHashCode;

/**
 * This is a base class for the description of a Point in 3D space (using double
 * coordinates).
 *
 * @author Thorsten Reitz
 */
public class Point3D implements Localizable, Serializable, Cloneable {

	/**
	 * The class' serial version UID
	 */
	private static final long serialVersionUID = 7639127877097165234L;

	/**
	 * This Point's x coordinate component.
	 */
	private double x;

	/**
	 * This Point's y coordinate component.
	 */
	private double y;

	/**
	 * This Point's z coordinate component.
	 */
	private double z;

	/**
	 * Default constructor
	 */
	public Point3D() {
		super();
	}

	/**
	 * Constructs a new Point with the x and y coordinate of a 2D point
	 * 
	 * @param p2d the 2D point
	 */
	public Point3D(Point2D p2d) {
		this.x = p2d.getX();
		this.y = p2d.getY();
		this.z = 0;
	}

	/**
	 * Constructs a new Point with the x and y coordinate of a 2D point.
	 * 
	 * @param p2d the 2D point
	 * @param z the missing z coordinate
	 */
	public Point3D(Point2D p2d, double z) {
		this.x = p2d.getX();
		this.y = p2d.getY();
		this.z = z;
	}

	/**
	 * Constructs a new Point with the x, y and z coordinates of another 3D
	 * point
	 * 
	 * @param p3d the other 3D point
	 */
	public Point3D(Point3D p3d) {
		this.x = p3d.getX();
		this.y = p3d.getY();
		this.z = p3d.getZ();
	}

	/**
	 * Constructs a Point with the given ordinates
	 * 
	 * @param x the x ordinate
	 * @param y the y ordinate
	 * @param z the z ordinate
	 */
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// functional methods ......................................................

	/**
	 * @return A BoundingBox for this Point, with min == max values.
	 */
	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(this.x, this.y, this.z, this.x, this.y, this.z);
	}

	// canonical java methods ..................................................

	/**
	 * @see Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof Point3D)) {
			return false;
		}

		Point3D other = (Point3D) o;
		if (this.x == other.getX() && this.y == other.getY() && this.z == other.getZ()) {
			return true;
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
		int hash = BlochHashCode.addFieldToHash(BlochHashCode.HASH_CONSTANT, this.x);
		hash = BlochHashCode.addFieldToHash(hash, this.y);
		return BlochHashCode.addFieldToHash(hash, this.z);
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		String result = "[Point3D: ";
		return (result + this.getX() + ", " + this.getY() + ", " + this.getZ() + "]");
	}

	// getter/setter methods ...................................................

	/**
	 * @return the x ordinate
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * @return the y ordinate
	 */
	public double getY() {
		return this.y;
	}

	/**
	 * @return the z ordinate
	 */
	public double getZ() {
		return this.z;
	}

	/**
	 * @param p3d the other 3D point
	 */
	public void set(Point3D p3d) {
		this.x = p3d.getX();
		this.y = p3d.getY();
		this.z = p3d.getZ();
	}

	/**
	 * @param x the new x ordinate
	 * @param y the new y ordinate
	 * @param z the new z ordinate
	 */
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sets the new x ordinate
	 * 
	 * @param x the new ordinate
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets the new y ordinate
	 * 
	 * @param y the new ordinate
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Sets the new z ordinate
	 * 
	 * @param z the new ordinate
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * @see Object#clone()
	 */
	@Override
	public Point3D clone() throws CloneNotSupportedException {
		return (Point3D) super.clone();
	}

	/**
	 * Calculates the normal of a given vertex p2.
	 * 
	 * @param p1 the point prior to p2
	 * @param p2 the point to calculate the normal for
	 * @param p3 the point next to p2
	 * @return the normal of p2
	 */
	public static Point3D calcNormal(Point3D p1, Point3D p2, Point3D p3) {
		return calcNormal(p1.getX(), p1.getY(), p1.getZ(), p2.getX(), p2.getY(), p2.getZ(),
				p3.getX(), p3.getY(), p3.getZ());
	}

	/**
	 * Calculates the normal of a given vertex p2.
	 * 
	 * @param p1x the x ordinate of the point prior to p2
	 * @param p1y the y ordinate of the point prior to p2
	 * @param p1z the z ordinate of the point prior to p2
	 * @param p2x the x ordinate of the point to calculate the normal for
	 * @param p2y the y ordinate of the point to calculate the normal for
	 * @param p2z the z ordinate of the point to calculate the normal for
	 * @param p3x the x ordinate of the point next to p2
	 * @param p3y the y ordinate of the point next to p2
	 * @param p3z the z ordinate of the point next to p2
	 * @return the normal of p2
	 */
	public static Point3D calcNormal(double p1x, double p1y, double p1z, double p2x, double p2y,
			double p2z, double p3x, double p3y, double p3z) {
		double ax = p3x - p2x;
		double ay = p3y - p2y;
		double az = p3z - p2z;

		double bx = p1x - p2x;
		double by = p1y - p2y;
		double bz = p1z - p2z;

		// calculate vector product
		double x = ay * bz - az * by;
		double y = az * bx - ax * bz;
		double z = ax * by - ay * bx;

		// normalize vector
		double length = Math.sqrt(x * x + y * y + z * z);
		if (length == 0) {
			// a and b are parallel
			x = 0.0;
			y = 0.0;
			z = 1.0;
		}
		else {
			x /= length;
			y /= length;
			z /= length;
		}

		return new Point3D(x, y, z);
	}

	/**
	 * Calculates the average normal of a face by calculating the normals of all
	 * vertices. There must be at least 3 vertices in this face, otherwise this
	 * method returns null.
	 * 
	 * @param vs the list of points representing a face
	 * 
	 * @return the normal of the face
	 */
	public static Point3D calcNormal(List<Point3D> vs) {
		int vslen = vs.size();
		if (vslen < 3) {
			return null;
		}

		// also calculate normal correctly if polygon is closed. refs #811
		if (vs.get(0).equals(vs.get(vslen - 1))) {
			--vslen;
			if (vslen < 3) {
				return null;
			}
		}

		// calculate normal for the first vertex
		Point3D normal = calcNormal(vs.get(vslen - 1), vs.get(0), vs.get(1));

		if (vslen == 3) {
			// already done
			return normal;
		}

		double x = normal.getX();
		double y = normal.getY();
		double z = normal.getZ();

		// calculate normal for each vertex and add it to the normal
		// calculated above to create an average normal
		for (int i = 1; i < vslen; ++i) {
			// calculate current vertex' normal
			Point3D vn;
			if (i < vslen - 1) {
				vn = calcNormal(vs.get(i - 1), vs.get(i), vs.get(i + 1));
			}
			else {
				vn = calcNormal(vs.get(i - 1), vs.get(i), vs.get(0));
			}

			// if current normal has another direction than the average
			// normal, turn it around
			double innerProduct = x * vn.getX() + y * vn.getY() + z * vn.getZ();
			if (innerProduct < 0.0) {
				// invert current normal
				vn.setX(-vn.getX());
				vn.setY(-vn.getY());
				vn.setZ(-vn.getZ());
			}

			// add both normals
			x += vn.getX();
			y += vn.getY();
			z += vn.getZ();
		}
		// normalize normal
		double length = Math.sqrt(x * x + y * y + z * z);
		if (length == 0) {
			// a and b are parallel
			x = 0.0;
			y = 0.0;
			z = 1.0;
		}
		else {
			x /= length;
			y /= length;
			z /= length;
		}

		normal.setX(x);
		normal.setY(y);
		normal.setZ(z);

		return normal;
	}

}
