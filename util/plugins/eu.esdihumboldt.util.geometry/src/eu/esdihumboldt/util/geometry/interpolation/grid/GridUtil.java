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

package eu.esdihumboldt.util.geometry.interpolation.grid;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Coordinate;

/**
 * Interpolation grid utilities.
 * 
 * @author Simon Templer
 */
public class GridUtil {

	/**
	 * Factor for grid size based on the maximum position error.
	 */
	private static final double GRID_FACTOR = (new BigDecimal(Double.toString(Math.sqrt(2)))
			.setScale(10, BigDecimal.ROUND_DOWN)).doubleValue();

	/**
	 * Determine the grid size given a maximum positional error.
	 * 
	 * @param maxPositionalError the maximum positional error
	 * @return the grid size, i.e. the height/width of grid cells
	 */
	public static double getGridSize(double maxPositionalError) {
		return GRID_FACTOR * maxPositionalError;
	}

	/**
	 * relocate geometry coordinate to nearest grid point.
	 * 
	 * @param coordinate geometry coordinate
	 * @param gridSize the grid size, i.e. the height/width of grid cells
	 * @return relocated grid coordinate
	 */
	public static Coordinate movePointToGrid(Coordinate coordinate, final double gridSize) {

		double x = moveOrdinateToGrid(coordinate.x, gridSize);
		double y = moveOrdinateToGrid(coordinate.y, gridSize);

		return new Coordinate(x, y);
	}

	private static double moveOrdinateToGrid(double ord, double gridSize) {
		double ordFactor = ord / gridSize;
		ordFactor = Math.round(ordFactor);
		return ordFactor * gridSize;
	}

}
