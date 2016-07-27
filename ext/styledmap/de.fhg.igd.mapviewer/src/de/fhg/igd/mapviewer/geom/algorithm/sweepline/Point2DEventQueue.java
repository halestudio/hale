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

import java.util.Collections;
import java.util.Comparator;

import de.fhg.igd.mapviewer.geom.shape.Line2D;
import de.fhg.igd.mapviewer.geom.shape.Polygon;

/**
 * Represents a queue of events during a sweep-line search
 * 
 * @author Michel Kraemer
 */
public class Point2DEventQueue {

	/**
	 * A sorted list that contains all events (in reverse order, so they can be
	 * removed faster)
	 */
	private final SortedCollection<Point2DEvent> _list;

	/**
	 * A Point2D comparator used to sort points in the list
	 */
	private final Comparator<Point2DEvent> _p2deventc;

	/**
	 * A Point2D comparator used to compare points
	 */
	private final SweepPoint2DXYComparator _p2dxyc;

	/**
	 * Constructs a new EventQueue
	 */
	public Point2DEventQueue() {
		_p2deventc = Collections.reverseOrder(new SweepPoint2DEventComparator());
		_p2dxyc = new SweepPoint2DXYComparator();
		_list = new SortedCollection<Point2DEvent>(_p2deventc);
	}

	/**
	 * Removes an event from the head of this queue
	 * 
	 * @return the event or null if the queue is empty
	 */
	public Point2DEvent remove() {
		if (_list.isEmpty()) {
			return null;
		}
		return _list.remove(_list.size() - 1);
	}

	/**
	 * @return the number of elements in the queue
	 */
	public int size() {
		return _list.size();
	}

	/**
	 * @return true if the queue is empty
	 */
	public boolean isEmpty() {
		return _list.isEmpty();
	}

	/**
	 * Adds a 2D line with exactly two Points to the queue.
	 * 
	 * @param p the line to add
	 * @param poly the Polygon the given line belongs to
	 * @return true if the line has been added, false otherwise
	 */
	public boolean add(Line2D p, Polygon poly) {
		if (p.getPoints().length != 2) {
			throw new IllegalArgumentException(
					"The Line2D must be a simple " + "line with two Points");
		}
		if (p.getPoints()[0].equals(p.getPoints()[1])) {
			throw new IllegalArgumentException("Degenerated line");
		}
		if (p.getPoints()[0].getX() == p.getPoints()[1].getX()) {
			throw new IllegalArgumentException("Line must not be vertical");
		}

		// sort points and create events
		boolean l = _p2dxyc.compare(p.getPoints()[0], p.getPoints()[1]) < 0;
		Point2DEvent p1 = new Point2DEvent(p.getPoints()[0], p, l, poly);
		Point2DEvent p2 = new Point2DEvent(p.getPoints()[1], p, !l, poly);

		// add events to set
		if (!_list.add(p1)) {
			return false;
		}
		if (!_list.add(p2)) {
			// remove p1 to leave the list in
			// a consistent state
			_list.remove(p1);
			return false;
		}

		return true;
	}

	/**
	 * Clears the queue
	 */
	public void clear() {
		_list.clear();
	}
}
