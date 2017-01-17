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
 * Arc represented by center point and radius.
 * 
 * @author Simon Templer
 */
public interface ArcByCenterPoint extends Arc {

	/**
	 * @return if the arc is clockwise (rather than counter-clockwise)
	 */
	boolean isClockwise();

	/**
	 * @return the arc center point
	 */
	Coordinate getCenterPoint();

	/**
	 * @return the arc radius
	 */
	double getRadius();

	/**
	 * @return the start angle
	 */
	Angle getStartAngle();

	/**
	 * @return the end angle
	 */
	Angle getEndAngle();

	/**
	 * Get the angle between start and end of the arc. This is a positive value
	 * if the arc is counter-clockwise, a negative value if the arc is
	 * clockwise.
	 * 
	 * @return the angle between start and end angle (a positive value is
	 *         counter-clockwise)
	 */
	Angle getAngleBetween();

	@Override
	default ArcByCenterPoint toArcByCenterPoint() {
		return this;
	}

}
