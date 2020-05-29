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

package eu.esdihumboldt.hale.common.instance.geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.util.geometry.interpolation.InterpolationAlgorithm;
import eu.esdihumboldt.util.geometry.interpolation.InterpolationUtil;
import eu.esdihumboldt.util.geometry.interpolation.extension.InterpolationAlgorithmFactory;
import eu.esdihumboldt.util.geometry.interpolation.extension.InterpolationExtension;
import eu.esdihumboldt.util.geometry.interpolation.grid.GridInterpolation;
import eu.esdihumboldt.util.geometry.interpolation.grid.GridUtil;
import eu.esdihumboldt.util.geometry.interpolation.split.SplitInterpolation;

/**
 * Helper for handling interpolation for instance readers.
 * 
 * @author Simon Templer
 */
public class InterpolationHelper {

	private static final ALogger log = ALoggerFactory.getLogger(InterpolationHelper.class);

	/**
	 * Identifier of the default algorithm.
	 */
	public static final String DEFAULT_ALGORITHM = SplitInterpolation.EXTENSION_ID;

	/**
	 * Parameter name for the selection of the interpolation algorithm.
	 */
	public static final String PARAMETER_INTERPOLATION_ALGORITHM = "interpolation.algorithm";

	/**
	 * Parameter name for the interpolation maximum positional error setting.
	 */
	public static final String PARAMETER_MAX_POSITION_ERROR = "interpolation.maxError";

	/**
	 * Default parameter value for the interpolation setting
	 */
	public static final double DEFAULT_MAX_POSITION_ERROR = 0.1;

	/**
	 * Get the interpolation algorithm for a given instance reader.
	 * 
	 * @param instanceReader the instance reader
	 * @param factory the geometry factory
	 * @return the interpolation algorithm
	 */
	public static InterpolationAlgorithm getInterpolation(IOProvider instanceReader,
			GeometryFactory factory) {
		// FIXME weak cache based on reader?

		String algorithmId = instanceReader.getParameter(PARAMETER_INTERPOLATION_ALGORITHM)
				.as(String.class, DEFAULT_ALGORITHM);

		InterpolationAlgorithmFactory fact = InterpolationExtension.getInstance()
				.getFactory(algorithmId);
		if (fact == null) {
			log.warn("Could not find interpolation algorithm with ID " + algorithmId);
			fact = InterpolationExtension.getInstance().getFactory(DEFAULT_ALGORITHM);
		}

		if (fact == null) {
			throw new IllegalStateException("Default interpolation algorithm could not be found");
		}

		InterpolationAlgorithm result;
		try {
			result = fact.createExtensionObject();
		} catch (Exception e) {
			log.error("Interpolation algorithm could be created", e);
			result = new SplitInterpolation();
		}

		double maxPositionalError = getMaxPositionalError(instanceReader);

		// configure the algorithm
		Map<String, Value> configuration = new HashMap<>();
		instanceReader.storeConfiguration(configuration);
		Map<String, String> properties = new HashMap<>();
		for (Entry<String, Value> entry : configuration.entrySet()) {
			if (!entry.getValue().isRepresentedAsDOM()) {
				properties.put(entry.getKey(), entry.getValue().getStringRepresentation());
			}
		}
		result.configure(factory, maxPositionalError, properties);

		return result;
	}

	/**
	 * Get the maximum positional error for geometry interpolation.
	 * 
	 * @param instanceReader the instanced reader configured with interpolation
	 *            settings
	 * @return the maximum positional error
	 */
	public static double getMaxPositionalError(IOProvider instanceReader) {
		// determine maximum positional error
		// FIXME smart default? probably depends on the reference system
		return instanceReader.getParameter(PARAMETER_MAX_POSITION_ERROR).as(Double.class,
				DEFAULT_MAX_POSITION_ERROR);
	}

	/**
	 * Determine if the interpolation algorithm requires all geometries being
	 * moved to a grid.
	 * 
	 * @param instanceReader the instance reader w/ the interpolation
	 *            configuration
	 * @return if geometries should be moved to a grid
	 */
	public static boolean requiresGeometriesMovedToGrid(IOProvider instanceReader) {
		String algorithmId = instanceReader.getParameter(PARAMETER_INTERPOLATION_ALGORITHM)
				.as(String.class, DEFAULT_ALGORITHM);
		if (GridInterpolation.EXTENSION_ID.equals(algorithmId)) {
			// fixed check for specific interpolation algorithm and parameter
			// XXX rather handle via extension point?
			return instanceReader.getParameter(GridInterpolation.PARAMETER_MOVE_ALL_TO_GRID)
					.as(Boolean.class, false);
		}
		else {
			return false;
		}
	}

	/**
	 * Move the given coordinates to a grid if required.
	 * 
	 * @param instanceReader the instance reader with the interpolation
	 *            configuration
	 * @param coordinates the coordinates to process
	 * @return the original or moved coordinates
	 */
	public static Coordinate[] moveCoordinates(IOProvider instanceReader,
			final Coordinate[] coordinates) {
		if (requiresGeometriesMovedToGrid(instanceReader)) {
			// move all coordinates to the grid
			double gridSize = GridUtil.getGridSize(getMaxPositionalError(instanceReader));

			List<Coordinate> newCoordinates = new ArrayList<>(coordinates.length);
			for (Coordinate coordinate : coordinates) {
				InterpolationUtil.addIfDifferent(newCoordinates,
						GridUtil.movePointToGrid(coordinate, gridSize));
			}

			return newCoordinates.toArray(new Coordinate[newCoordinates.size()]);
		}
		else {
			// keep original
			return coordinates;
		}
	}

	/**
	 * Move the given coordinate to a grid if required.
	 * 
	 * @param instanceReader the instance reader with the interpolation
	 *            configuration
	 * @param coordinate the coordinate to process
	 * @return the original or moved coordinates
	 */
	public static Coordinate moveCoordinate(IOProvider instanceReader,
			final Coordinate coordinate) {
		if (requiresGeometriesMovedToGrid(instanceReader)) {
			// move coordinate to the grid
			double gridSize = GridUtil.getGridSize(getMaxPositionalError(instanceReader));
			return GridUtil.movePointToGrid(coordinate, gridSize);
		}
		else {
			// keep original
			return coordinate;
		}
	}

}
