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

import java.util.Comparator;

/**
 * Compares Point2D objects to sort them by their increasing x ordinate and
 * increasing y ordinate.
 * 
 * @author Michel Kraemer
 */
public class SweepPoint2DEventComparator implements Comparator<Point2DEvent> {

	/**
	 * This comparator is used internally to compare xy ordinates
	 */
	private SweepPoint2DXYComparator _xycomparator = new SweepPoint2DXYComparator();

	/**
	 * Compares two Point2D objects.
	 * 
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return -1 if the x ordinate of p1 is lower than the one of p2, +1 if
	 *         it's greater. If both ordinates are equal the method will compare
	 *         the y ordinates.
	 */
	@Override
	public int compare(Point2DEvent p1, Point2DEvent p2) {
		if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
			if (p1.isLeft() && !p2.isLeft()) {
				return -1;
			}
			else if (!p1.isLeft() && p2.isLeft()) {
				return 1;
			}
			return 0;
		}
		return _xycomparator.compare(p1, p2);
	}
}
