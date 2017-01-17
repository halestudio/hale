/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.util.geometry.interpolation;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Interpolation of a circle geometry
 * 
 * @author Arun
 */
@Deprecated
public class CircleInterpolation extends ArcInterpolation {

	/**
	 * Constructor
	 * 
	 * @param coordinates the circle coordinates
	 * @param maxPositionalError maximum positional error
	 * @param keepOriginal keep original coordinates intact
	 */
	public CircleInterpolation(Coordinate[] coordinates, double maxPositionalError,
			boolean keepOriginal) {
		this(coordinates, maxPositionalError, keepOriginal, null, 0);
	}

	/**
	 * Constructor
	 * 
	 * @param coordinates the circle coordinates
	 * @param maxPositionalError maximum positional error
	 * @param keepOriginal keep original coordinates intact
	 * @param center center coordinate of circle
	 * @param radius the radius of circle
	 */
	public CircleInterpolation(Coordinate[] coordinates, double maxPositionalError,
			boolean keepOriginal, Coordinate center, double radius) {
		super(coordinates, maxPositionalError, keepOriginal, true, center, radius);
	}

}
