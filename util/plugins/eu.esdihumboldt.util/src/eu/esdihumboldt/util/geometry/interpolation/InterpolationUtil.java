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

package eu.esdihumboldt.util.geometry.interpolation;

import java.math.BigDecimal;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;

/**
 * Uutility functions related to interpolation.
 * 
 * @author Arun Verma
 * @author Simon Templer
 */
public class InterpolationUtil {

	/**
	 * Scale used when comparing slopes.
	 */
	public static final int SLOPE_SCALE = 4;

	/**
	 * Determines if the Arc is very close to a straight line.
	 * 
	 * @param arc the arc to check
	 * @return if the arc closely represents a straight line
	 */
	public static boolean isStraightLine(Arc arc) {
		if (arc.isCircle()) {
			return false;
		}

		if (arc instanceof ArcByCenterPoint
				&& Double.isInfinite(((ArcByCenterPoint) arc).getRadius())) {
			// treat as straight line if the radius is infinite
			return true;
		}

		ArcByPoints a = arc.toArcByPoints();
		double yDelta_a = a.getMiddlePoint().y - a.getStartPoint().y;
		double xDelta_a = a.getMiddlePoint().x - a.getStartPoint().x;
		double yDelta_b = a.getEndPoint().y - a.getMiddlePoint().y;
		double xDelta_b = a.getEndPoint().x - a.getMiddlePoint().x;

		double aSlope = yDelta_a / xDelta_a;
		double bSlope = yDelta_b / xDelta_b;

		if (round(aSlope, SLOPE_SCALE) == round(bSlope, SLOPE_SCALE)) {
			return true;
		}
		return false;
	}

	/**
	 * Rounding the given double value in a standardized manner.
	 * 
	 * @param x a double value to be round off
	 * @param scale location of decimal points in
	 * @return rounded double value
	 */
	public static double round(double x, int scale) {
		return round(x, scale, BigDecimal.ROUND_HALF_UP);
	}

	private static double round(double x, int scale, int roundingMethod) {
		try {
			return (new BigDecimal(Double.toString(x)).setScale(scale, roundingMethod))
					.doubleValue();
		} catch (NumberFormatException ex) {
			if (Double.isInfinite(x)) {
				return x;
			}
			else {
				return Double.NaN;
			}
		}
	}

	/**
	 * Add a coordinate to a coordinate list only if it is not equal to the last
	 * coordinate in the list.
	 * 
	 * @param coords the coordinates list
	 * @param c the coordinate to add
	 */
	public static void addIfDifferent(List<Coordinate> coords, Coordinate c) {
		if (coords.isEmpty()) {
			coords.add(c);
		}
		else {
			Coordinate last = coords.get(coords.size() - 1);
			if (!c.equals(last)) {
				coords.add(c);
			}
		}

	}

}
