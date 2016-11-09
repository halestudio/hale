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

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Base class of interpolation
 * 
 * @author Arun
 * @param <T> the type of interpolated Geometry
 */
public abstract class Interpolation<T extends Geometry> {

	/**
	 * Grid step size factor
	 */
	protected final int GRID_FACTOR = 2;
	/**
	 * Next coordinate distance factor.
	 */
	protected final double NEXT_COORDINATE_DISTANCE_FACTOR = 4;
	/**
	 * Minimum distance of next coordinate from current coordinate
	 */
	protected final double NEXT_COORDINATE_DISTANCE;

	/**
	 * Maximum positional error
	 */
	protected final double MAX_POSITIONAL_ERROR;

	/**
	 * Coordinates of Geometry, which wanted to be interpolated
	 */
	protected final Coordinate[] rawGeometryCoordinates;

	/**
	 * Constructor
	 * 
	 * @param coordinates Coordinates of geometry that need to be interpolated
	 * @param maxPositionalError maximum positional error for interpolation
	 */
	public Interpolation(Coordinate[] coordinates, double maxPositionalError) {
		this.MAX_POSITIONAL_ERROR = maxPositionalError;
		this.rawGeometryCoordinates = coordinates;
		this.NEXT_COORDINATE_DISTANCE = NEXT_COORDINATE_DISTANCE_FACTOR * maxPositionalError;
	}

	/**
	 * interpolate raw geometry
	 * 
	 * @return interpolated Geometry of type T or <code>null</code>
	 */
	public T interpolateRawGeometry() {
		if (!validateRawCoordinates()) {
			return null;
		}

		return interpolatedGeometry();
	}

	/**
	 * validate raw geometry
	 * 
	 * @return true if validation successful else false.
	 */
	protected abstract boolean validateRawCoordinates();

	/**
	 * get interpolate raw geometry in to
	 * 
	 * @return interpolated Geometry
	 * 
	 */
	protected abstract T interpolatedGeometry();

	/**
	 * relocate geometry coordinate to nearest grid point.
	 * 
	 * @param coordinate geometry coordinate
	 * @return relocated grid coordinate
	 */
	protected Coordinate pointToGrid(Coordinate coordinate) {

		// Start form 0,0 always
		long gridMinXMultiplier = (long) (coordinate.x / (GRID_FACTOR * MAX_POSITIONAL_ERROR));
		long gridMinYMultiplier = (long) (coordinate.y / (GRID_FACTOR * MAX_POSITIONAL_ERROR));

		double gridMinXNearPoint = gridMinXMultiplier * (GRID_FACTOR * MAX_POSITIONAL_ERROR);
		double gridMinYNearPoint = gridMinYMultiplier * (GRID_FACTOR * MAX_POSITIONAL_ERROR);

		while ((gridMinXNearPoint + (GRID_FACTOR * MAX_POSITIONAL_ERROR)) < coordinate.x) {
			gridMinXNearPoint = gridMinXNearPoint + (GRID_FACTOR * MAX_POSITIONAL_ERROR);
		}

		while ((gridMinYNearPoint + (GRID_FACTOR * MAX_POSITIONAL_ERROR)) < coordinate.y) {
			gridMinYNearPoint = gridMinYNearPoint + (GRID_FACTOR * MAX_POSITIONAL_ERROR);
		}

		// Now we have to evaluate nearest grid point for arc point
		Coordinate minDistGridPoint = null;

		// for loop on grid cell
		for (Coordinate cellCoordinate : getGridCellCoordinates(gridMinXNearPoint,
				gridMinYNearPoint)) {
			double distance = coordinate.distance(cellCoordinate);
			if (distance <= MAX_POSITIONAL_ERROR) {
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

	private List<Coordinate> getGridCellCoordinates(double gridMinXNearPoint,
			double gridMinYNearPoint) {

		List<Coordinate> coordinates = new ArrayList<>();

		// up left corner
		coordinates.add(new Coordinate(gridMinXNearPoint, gridMinYNearPoint));

		// up right corner of the grid cell
		coordinates.add(new Coordinate(gridMinXNearPoint + (GRID_FACTOR * MAX_POSITIONAL_ERROR),
				gridMinYNearPoint));
		// bottom left corner of the grid cell
		coordinates.add(new Coordinate(gridMinXNearPoint,
				gridMinYNearPoint + (GRID_FACTOR * MAX_POSITIONAL_ERROR)));

		// bottom right corner of the grid cell
		coordinates.add(new Coordinate(gridMinXNearPoint + (GRID_FACTOR * MAX_POSITIONAL_ERROR),
				gridMinYNearPoint + (GRID_FACTOR * MAX_POSITIONAL_ERROR)));

		// center of the grid cell
		coordinates
				.add(new Coordinate(gridMinXNearPoint + ((GRID_FACTOR / 2) * MAX_POSITIONAL_ERROR),
						gridMinYNearPoint + ((GRID_FACTOR / 2) * MAX_POSITIONAL_ERROR)));

		return coordinates;
	}

}
