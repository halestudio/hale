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

package de.fhg.igd.geom.util;

import com.google.common.base.Preconditions;

/**
 * Some mathematical helper methods
 * 
 * @author Simon Thum
 * @author Michel Kraemer
 */
public class MathHelper {

	/**
	 * Enforces a value range
	 * 
	 * @param in the input value
	 * @param lower the lower limit
	 * @param upper the upper limit
	 * @return the saturated value
	 */
	public static int saturate(int in, int lower, int upper) {
		if (in >= lower && in <= upper) {
			return in;
		}
		if (in < lower) {
			return lower;
		}
		return upper;
	}

	/**
	 * Enforces a value range
	 * 
	 * @param in the input value
	 * @param lower the lower limit
	 * @param upper the upper limit
	 * @return the saturated value
	 */
	public static double saturate(double in, double lower, double upper) {
		if (in >= lower && in <= upper) {
			return in;
		}
		if (in < lower) {
			return lower;
		}
		return upper;
	}

	/**
	 * Enforces a value range
	 * 
	 * @param in the input value
	 * @param lower the lower limit
	 * @param upper the upper limit
	 * @return the saturated value
	 */
	public static float saturate(float in, float lower, float upper) {
		if (in >= lower && in <= upper) {
			return in;
		}
		if (in < lower) {
			return lower;
		}
		return upper;
	}

	/**
	 * Translate a value based on input and output ranges. Does not saturate.
	 * 
	 * @param in the value to translate
	 * @param in_min minimum of the input range
	 * @param in_range size of the input range
	 * @param out_min minimum of the output range
	 * @param out_range size of the output range
	 * @return the translated value
	 */
	public static double translate(double in, double in_min, double in_range, double out_min,
			double out_range) {
		return (((in - in_min) / in_range) * out_range) + out_min;
	}

	/**
	 * scales a double range using integer indexed segments.
	 * 
	 * @param start the range start
	 * @param end the range end
	 * @param unit the unit (usually end-start / max )
	 * @param index the index to select
	 * @param max the maximum index
	 * @return a double
	 */
	public static double intScale(double start, double end, double unit, int index, int max) {
		if (index == 0)
			return start;
		else if (index == max)
			return end;
		return start + unit * index;
	}

	/**
	 * SQL-like coalescing using isReal()
	 * 
	 * @param p array of at least unit length
	 * @return the first real value from the given array
	 */
	public static float coalesce(float... p) {
		for (float v : p) {
			if (isReal(v)) {
				return v;
			}
		}
		return p[p.length - 1];
	}

	/**
	 * Checks if a floating point number is real (not infinite and not NaN)
	 * 
	 * @param x the number to check
	 * @return true if x is real, false otherwise
	 */
	public static boolean isReal(float x) {
		return (!Float.isInfinite(x) && !Float.isNaN(x));
	}

	/**
	 * Checks if a floating point number is real (not infinite and not NaN)
	 * 
	 * @param x the number to check
	 * @return true if x is real, false otherwise
	 */
	public static boolean isReal(double x) {
		return (!Double.isInfinite(x) && !Double.isNaN(x));
	}

	/**
	 * The so-called 'euclidean' modulo, a modulo which won't yield negative
	 * results
	 * 
	 * @param x the number to divide
	 * @param mod the divisor
	 * @return the euclidean modulo
	 */
	public static int modulo(int x, int mod) {
		if (x >= 0) {
			return x % mod;
		}
		int n = 1 + (-x / mod);
		x += n * mod;
		return x % mod;
	}

	/**
	 * Calculates the dot product between two vectors
	 * 
	 * @param p1x the x ordinate of the first vector
	 * @param p1y the y ordinate of the first vector
	 * @param p2x the x ordinate of the second vector
	 * @param p2y the y ordinate of the second vector
	 * @return the dot product
	 */
	public static double dot2D(double p1x, double p1y, double p2x, double p2y) {
		return p1x * p2x + p1y * p2y;
	}

	/**
	 * given two angels in radians, returns a difference in radians closest to
	 * zero such that a + angleDiff(a, b) represents b.
	 * 
	 * @param a angle a
	 * @param b angle b
	 * @return the angular difference in radians
	 */
	public static double angleDiff(double a, double b) {
		return angleDiff(a, b, Math.PI * 2);
	}

	/**
	 * given two angels in radians, returns a difference in radians closest to
	 * zero such that a + angleDiff(a, b) represents b.
	 * 
	 * @param a angle a
	 * @param b angle b
	 * @param ring the ring in which the difference is meaningful (2PI is full
	 *            circle)
	 * @return the angular difference in radians
	 */
	public static double angleDiff(double a, double b, double ring) {
		double c = b - a;
		c %= ring;
		if (c >= -ring / 2 && c <= ring / 2)
			return c;
		c += (-ring) * Math.signum(c);
		return c;
	}

	/**
	 * given two angels in radians, returns a difference in radians closest to
	 * zero and >= zero.
	 * 
	 * @param a angle a
	 * @param b angle b
	 * @return the angular difference in radians
	 */
	public static double angleDiffAbs(double a, double b) {
		return Math.abs(angleDiff(a, b));
	}

	/**
	 * given two angels in radians, returns a difference in radians closest to
	 * zero and >= zero, depending on the scale. For example, while two angles
	 * pi/2 and -pi/2 may differ on a full circle (scale 1), they are considered
	 * equal at scale 2. At scale 3, also orthogonal angles would match, and so
	 * on.
	 * 
	 * @param a angle a
	 * @param b angle b
	 * @param scale the scale to base the difference on
	 * @return the angular difference in radians
	 */
	public static double angleDiffAbs(double a, double b, int scale) {
		return Math.abs(angleDiff(a, b, Math.scalb(Math.PI, 2 - scale)));
	}

	/**
	 * Checks if a given integer is a power of two
	 * 
	 * @param i the integer
	 * @return true if i is a power of two
	 */
	public static boolean isPowerOfTwo(int i) {
		return ((i & (i - 1)) == 0);
	}

	/**
	 * Finds the next power of two for a given integer (see
	 * http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2)
	 * 
	 * @param i the integer (must be positive)
	 * @return the next power of two
	 */
	public static int nextPowerOfTwo(int i) {
		Preconditions.checkArgument(i >= 0, "i must be positive");
		i--;
		i |= i >> 1;
		i |= i >> 2;
		i |= i >> 4;
		i |= i >> 8;
		i |= i >> 16;
		i++;
		return i;
	}

	/**
	 * Finds the next power of two lower than the given number
	 * 
	 * @param i the number (must larger than 0)
	 * @return the next power of two lower than i
	 */
	public static int previousPowerOfTwo(int i) {
		Preconditions.checkArgument(i > 0, "i must be larger than 0");
		return nextPowerOfTwo(i) >> 1;
	}

	/**
	 * Calls {@link #nextPowerOfTwo(int)} and {@link #previousPowerOfTwo(int)}
	 * and finds the nearest value to the given number. Prefers the higher
	 * number if the distance is equal.
	 * 
	 * @param i the number (must be positive)
	 * @return the nearest power of two
	 */
	public static int nearestPowerOfTwo(int i) {
		Preconditions.checkArgument(i >= 0, "i must be positive");
		if (i == 0) {
			return 0;
		}

		int n = nextPowerOfTwo(i);
		int p = n >> 1;
		if (Math.abs(i - n) <= Math.abs(i - p)) {
			return n;
		}
		return p;
	}

	/**
	 * Compares two double for equality within e
	 * 
	 * @param a the first double
	 * @param b the second double
	 * @param e the maximum allowed difference
	 * @return true if a - b < e
	 */
	public static boolean robustEqual(double a, double b, double e) {
		return Math.abs(a - b) < e;
	}

}
