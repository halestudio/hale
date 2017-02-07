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

import java.util.Comparator;

/**
 * Compares SweepLine segments to sort them by their increasing y ordinate.
 * 
 * @author Michel Kraemer
 */
public class SweepLineComparator implements Comparator<SweepLineSegment> {

	/**
	 * The sweep line's current position
	 */
	private double _x;

	/**
	 * This comparator is used internally to compare xy ordinates
	 */
	private SweepPoint2DXYComparator _xycomparator = new SweepPoint2DXYComparator();

	/**
	 * Constructs a new comparator
	 * 
	 * @param x the sweep line's current position
	 */
	public SweepLineComparator(double x) {
		_x = x;
	}

	/**
	 * Sets the sweep line's position
	 * 
	 * @param x the position
	 */
	public void setPosition(double x) {
		_x = x;
	}

	/**
	 * Compares two SweepLine segments.
	 * 
	 * @param s1 the first segment
	 * @param s2 the second segment
	 * @return -1 if the first segment is "above" the second one, 1 if it is
	 *         below and 0 if both segments are equal.
	 */
	@Override
	public int compare(SweepLineSegment s1, SweepLineSegment s2) {
		double y1 = s1.getY(_x);
		double y2 = s2.getY(_x);
		if (y1 < y2) {
			return -1;
		}
		else if (y1 > y2) {
			return 1;
		}

		int result = _xycomparator.compare(s1.getLeftPoint(), s2.getLeftPoint());
		if (result != 0) {
			return result;
		}
		result = _xycomparator.compare(s1.getRightPoint(), s2.getRightPoint());
		if (result != 0) {
			return result;
		}

		return 0;
	}
}
