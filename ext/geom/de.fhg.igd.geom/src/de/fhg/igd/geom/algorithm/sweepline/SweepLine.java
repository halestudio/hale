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

import de.fhg.igd.geom.shape.Line2D;

/**
 * Contains a set of sweep-line segments.
 * 
 * @author Michel Kraemer
 */
public class SweepLine {

	/**
	 * A SweepPoint2DXYComparator which can compare Point2Ds
	 */
	private static SweepPoint2DXYComparator _p2dxyc = new SweepPoint2DXYComparator();

	/**
	 * The sorted list that contains all sweep-line segments
	 */
	private final SortedCollection<SweepLineSegment> _list;

	/**
	 * A comparator which compares sweep line segments
	 */
	private final SweepLineComparator _slc;

	/**
	 * The current sweep line position
	 */
	private double _position = 0.0;

	/**
	 * Default constructor
	 */
	public SweepLine() {
		_slc = new SweepLineComparator(0.0);
		_list = new SortedCollection<SweepLineSegment>(_slc);
	}

	/**
	 * Converts a Point2DEvent to a SweepLineSegment
	 * 
	 * @param event the event to convert
	 * @return the sweep line segment
	 */
	private SweepLineSegment makeSegment(Point2DEvent event) {
		Line2D line = event.getLineSegment();
		SweepLineSegment s = new SweepLineSegment(line, event.getPolygon());

		if (_p2dxyc.compare(line.getPoints()[0], line.getPoints()[1]) < 0) {
			s.setLeftPoint(line.getPoints()[0]);
			s.setRightPoint(line.getPoints()[1]);
		}
		else {
			s.setLeftPoint(line.getPoints()[1]);
			s.setRightPoint(line.getPoints()[0]);
		}

		return s;
	}

	/**
	 * Adds a Point2DEvent to the sweep-line
	 * 
	 * @param event the Point2DEvent
	 * @return a sweep-line segment created to hold the Point2DEvent
	 */
	public SweepLineSegment add(Point2DEvent event) {
		SweepLineSegment s = makeSegment(event);

		_list.add(s);

		// set new sweep line position and resort the list
		double x = s.getLeftPoint().getX();
		if (x != _position) {
			_position = x;
			_slc.setPosition(x);
			_list.resort();
		}

		return s;
	}

	/**
	 * Gets the SweepLineSegment that represents the given Point2DEvent
	 * 
	 * @param event the Point2DEvent
	 * @return the SweepLineSegment
	 */
	public SweepLineSegment get(Point2DEvent event) {
		SweepLineSegment s = makeSegment(event);
		int i = _list.indexOf(s);
		if (i == -1) {
			return null;
		}
		return _list.get(i);
	}

	/**
	 * Removes a SweepLineSegment from the SweepLine
	 * 
	 * @param s the segment to remove
	 * @return true if the segment was removed, false otherwise
	 */
	public boolean remove(SweepLineSegment s) {
		return _list.remove(s);
	}

	/**
	 * Gets the segment which is below the given one.
	 * 
	 * @param s the given segment
	 * @return the segment below s or null if there is no such segment
	 */
	public SweepLineSegment getBelowSegment(SweepLineSegment s) {
		int i = _list.indexOf(s);
		if (i == -1 || i >= _list.size() - 1) {
			return null;
		}
		return _list.get(i + 1);
	}

	/**
	 * Gets the segment which is above the given one.
	 * 
	 * @param s the given segment
	 * @return the segment above s or null if there is no such segment
	 */
	public SweepLineSegment getAboveSegment(SweepLineSegment s) {
		int i = _list.indexOf(s);
		if (i < 1) {
			return null;
		}
		return _list.get(i - 1);
	}
}
