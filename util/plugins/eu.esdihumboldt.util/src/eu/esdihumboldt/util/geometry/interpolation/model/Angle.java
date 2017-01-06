/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.util.geometry.interpolation.model;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Represents an angle.
 * 
 * @author Simon Templer
 */
public class Angle {

	/**
	 * 2 Pi
	 */
	public static final double PI_TIMES_2 = 2.0 * Math.PI;

	/**
	 * Create a new angle.
	 * 
	 * @param degrees the angle value in degrees
	 * @return the angle object
	 */
	public static Angle fromDegrees(double degrees) {
		return new Angle(Math.toRadians(degrees));
	}

	/**
	 * Create a new angle.
	 * 
	 * @param radians the angle value in radians
	 * @return the angle object
	 */
	public static Angle fromRadians(double radians) {
		return new Angle(radians);
	}

	/**
	 * Returns the angle of the vector from p0 to p1, relative to the positive
	 * X-axis. The angle is normalized to be in the range [ -Pi, Pi ].
	 * 
	 * @param p0 the start point of the vector
	 * @param p1 the end point of the vector
	 *
	 * @return the normalized angle that p0-p1 makes with the positive x-axis.
	 */
	public static Angle angle(Coordinate p0, Coordinate p1) {
		return new Angle(com.vividsolutions.jts.algorithm.Angle.angle(p0, p1));
	}

	/**
	 * Angle value in radians
	 */
	private final double angle;

	private Angle(double radians) {
		this.angle = radians;
	}

	/**
	 * @return the angle value as radians
	 */
	public double getRadians() {
		return angle;
	}

	/**
	 * @return the angle value as degrees
	 */
	public double getDegrees() {
		return Math.toDegrees(angle);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(angle);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Angle other = (Angle) obj;
		if (Double.doubleToLongBits(angle) != Double.doubleToLongBits(other.angle))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.valueOf(angle);
	}

}
