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
package de.fhg.igd.mapviewer.waypoints;

import java.util.HashMap;
import java.util.Map;

import de.fhg.igd.mapviewer.Refresher;

/**
 * GenericWaypointPainter
 *
 * @param <T> the way-point value type
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 * @version $Id$
 * @param <W> the way-point type
 */
public abstract class GenericWaypointPainter<T, W extends GenericWaypoint<T, W>>
		extends CustomWaypointPainter<W> {

	private final Map<T, W> waypointMap = new HashMap<T, W>();

	/**
	 * @see CustomWaypointPainter#CustomWaypointPainter()
	 */
	public GenericWaypointPainter() {
		super();
	}

	/**
	 * @see CustomWaypointPainter#CustomWaypointPainter(WaypointRenderer, int)
	 */
	public GenericWaypointPainter(WaypointRenderer<W> renderer, int numberOfThreads) {
		super(renderer, numberOfThreads);
	}

	/**
	 * @see CustomWaypointPainter#CustomWaypointPainter(WaypointRenderer)
	 */
	public GenericWaypointPainter(WaypointRenderer<W> renderer) {
		super(renderer);
	}

	/**
	 * @see CustomWaypointPainter#addWaypoint(SelectableWaypoint, Refresher)
	 */
	@Override
	public void addWaypoint(W wp, Refresher refresh) {
		W previous = waypointMap.put(wp.getValue(), wp);
		if (previous != null) {
			// remove way-point previously associated with the object
			// (because the RTree doesn't know if there are duplicates)
			removeWaypoint(previous, refresh);
		}

		super.addWaypoint(wp, refresh);
	}

	/**
	 * @see CustomWaypointPainter#clearWaypoints()
	 */
	@Override
	public void clearWaypoints() {
		super.clearWaypoints();
		waypointMap.clear();
	}

	/**
	 * Remove a way-point
	 * 
	 * @param object the object associated with the way-point
	 * @param refresh the refresher
	 */
	protected void removeWaypoint(T object, Refresher refresh) {
		if (waypointMap.containsKey(object)) {
			W wp = waypointMap.get(object);
			super.removeWaypoint(wp, refresh);
			waypointMap.remove(object);
		}
	}

	/**
	 * Find the way-point associated with the given object
	 * 
	 * @param object the object
	 * @return the way-point or <code>null</code> if no way-point is associated
	 *         with the object
	 */
	protected W findWaypoint(T object) {
		return waypointMap.get(object);
	}

	/**
	 * {@link Iterable} over the way-points
	 * 
	 * @return all way-points
	 */
	protected Iterable<W> iterateWaypoints() {
		return waypointMap.values();
	}

}
