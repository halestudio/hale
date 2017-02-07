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

import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.fhg.igd.geom.BoundingBox;

/**
 * GenericWaypoint
 *
 * @param <T> the value type
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 * @version $Id$
 * @param <W> the way-point type
 */
public class GenericWaypoint<T, W extends GenericWaypoint<T, W>> extends SelectableWaypoint<W> {

	private final T value;

	/**
	 * Constructor
	 * 
	 * @param pos the way-point position
	 * @param bb the bounding box, may be <code>null</code>
	 * @param value the value
	 */
	public GenericWaypoint(GeoPosition pos, BoundingBox bb, T value) {
		super(pos, bb);

		this.value = value;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getValue().toString();
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericWaypoint<?, ?> other = (GenericWaypoint<?, ?>) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}

}
