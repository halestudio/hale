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

import static eu.esdihumboldt.util.geometry.interpolation.InterpolationUtil.round;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Interpolation grid utilities.
 * 
 * @author Arun Verma
 * @author Simon Templer
 */
public class GridUtil {

	// FIXME review, tests, refactoring

	/**
	 * Grid step size factor
	 */
	private static final int GRID_FACTOR = 2;

	/**
	 * Rounding scale for grid cell value
	 */
	protected static final int ROUNDING_SCALE = 6;

	/**
	 * relocate geometry coordinate to nearest grid point.
	 * 
	 * @param coordinate geometry coordinate
	 * @param maxPositionalError maximum positional error
	 * @return relocated grid coordinate
	 */
	public static Coordinate movePointToGrid(Coordinate coordinate,
			final double maxPositionalError) {

		// Start form 0,0 always
		long gridMinXMultiplier = (long) round((coordinate.x / (GRID_FACTOR * maxPositionalError)),
				ROUNDING_SCALE);
		long gridMinYMultiplier = (long) round((coordinate.y / (GRID_FACTOR * maxPositionalError)),
				ROUNDING_SCALE);

		double gridMinXNearPoint = round(gridMinXMultiplier * (GRID_FACTOR * maxPositionalError),
				ROUNDING_SCALE);
		double gridMinYNearPoint = round(gridMinYMultiplier * (GRID_FACTOR * maxPositionalError),
				ROUNDING_SCALE);

		while ((gridMinXNearPoint + (GRID_FACTOR * maxPositionalError)) < coordinate.x) {
			gridMinXNearPoint = gridMinXNearPoint + (GRID_FACTOR * maxPositionalError);
		}

		while ((gridMinYNearPoint + (GRID_FACTOR * maxPositionalError)) < coordinate.y) {
			gridMinYNearPoint = gridMinYNearPoint + (GRID_FACTOR * maxPositionalError);
		}

		// Now we have to evaluate nearest grid point for arc point
		Coordinate minDistGridPoint = null;

		// for loop on grid cell
		for (Coordinate cellCoordinate : getGridCellCoordinates(gridMinXNearPoint,
				gridMinYNearPoint, coordinate.z, maxPositionalError)) {
			double distance = round(coordinate.distance(cellCoordinate), ROUNDING_SCALE);
			if (distance <= maxPositionalError) {
				if (minDistGridPoint != null) {
					if (distance < coordinate.distance(minDistGridPoint))
						minDistGridPoint = cellCoordinate;
				}
				else {
					minDistGridPoint = cellCoordinate;
				}
			}
		}
		return minDistGridPoint;
	}

	/**
	 * Method to find all cells near given grid X and grid Y
	 * 
	 * @param gridX grid minimum X value
	 * @param gridY grid minimum Y value
	 * @param z coordinate's z value
	 * @param maxPositionalError maximum positional error
	 * @return list of 4 cells surrounding minimum X and minimum Y
	 */
	private static List<Coordinate> getGridCellCoordinates(double gridX, double gridY, double z,
			double maxPositionalError) {

		List<Coordinate> coordinates = new ArrayList<>();

		// adding all 4 grid cell connecting to the specified points.
		// this will be helpful for negative coordinates
		// '_ _ _ _
		// | 3 | 2 |
		// |_ _|_ _|
		// | 4 | 1 |
		// |_ _|_ _|
		//

		// adding cell 1
		// up left corner
		coordinates.add(new Coordinate(gridX, gridY, z));

		// up right corner of the grid cell
		coordinates.add(new Coordinate( //
				round(gridX + (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				gridY, //
				z));
		// bottom left corner of the grid cell
		coordinates.add(new Coordinate( //
				gridX, //
				round(gridY + (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				z));

		// bottom right corner of the grid cell
		coordinates.add(new Coordinate( //
				round(gridX + (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				round(gridY + (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				z));

		// center of the grid cell
		coordinates.add(new Coordinate( //
				round(gridX + ((GRID_FACTOR / 2) * maxPositionalError), ROUNDING_SCALE),
				round(gridY + ((GRID_FACTOR / 2) * maxPositionalError), ROUNDING_SCALE), //
				z));

		// adding cell 2
		// up left corner
		coordinates.add(new Coordinate( //
				gridX, //
				round(gridY - (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				z));

		// up right corner of the grid cell
		coordinates.add(new Coordinate( //
				round(gridX + (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE),
				round(gridY - (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				z));

		// center of the grid cell
		coordinates.add(new Coordinate( //
				round(gridX + ((GRID_FACTOR / 2) * maxPositionalError), ROUNDING_SCALE), //
				round(gridY - ((GRID_FACTOR / 2) * maxPositionalError), ROUNDING_SCALE), //
				z));

		// adding cell 3
		// up left corner
		coordinates.add(new Coordinate( //
				round(gridX - (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				round(gridY - (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				z));

		// up right corner of the grid cell(already added in cell 2)

		// bottom left corner of the grid cell
		coordinates.add(new Coordinate(//
				round(gridX - (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				gridY, //
				z));

		// bottom right corner of the grid cell(already added in 1)

		// center of the grid cell
		coordinates.add(new Coordinate(//
				round(gridX - ((GRID_FACTOR / 2) * maxPositionalError), ROUNDING_SCALE), //
				round(gridY - ((GRID_FACTOR / 2) * maxPositionalError), ROUNDING_SCALE), //
				z));

		// adding cell 4
		// up left corner(already added in 3)
		// up right corner of the grid cell (already added in 1)
		// bottom left corner of the grid cell
		coordinates.add(new Coordinate(//
				round(gridX - (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				round(gridY + (GRID_FACTOR * maxPositionalError), ROUNDING_SCALE), //
				z));

		// bottom right corner of the grid cell(already added in 1)

		// center of the grid cell
		coordinates.add(new Coordinate( //
				round(gridX - ((GRID_FACTOR / 2) * maxPositionalError), ROUNDING_SCALE), //
				round(gridY + ((GRID_FACTOR / 2) * maxPositionalError), ROUNDING_SCALE), //
				z));

		return coordinates;
	}

}
