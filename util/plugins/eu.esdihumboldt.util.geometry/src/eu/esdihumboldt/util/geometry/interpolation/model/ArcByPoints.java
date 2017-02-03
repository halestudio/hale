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
 * Arc represented by three points.
 * 
 * @author Simon Templer
 */
public interface ArcByPoints extends Arc {

	/**
	 * @return the arc start point
	 */
	Coordinate getStartPoint();

	/**
	 * @return the arc end point
	 */
	Coordinate getEndPoint();

	/**
	 * @return the arc middle point (not the center point)
	 */
	Coordinate getMiddlePoint();

	@Override
	default ArcByPoints toArcByPoints() {
		return this;
	}

}
