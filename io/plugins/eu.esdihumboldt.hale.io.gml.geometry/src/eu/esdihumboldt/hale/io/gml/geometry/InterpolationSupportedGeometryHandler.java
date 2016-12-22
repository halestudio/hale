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

package eu.esdihumboldt.hale.io.gml.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vividsolutions.jts.geom.Coordinate;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.geometry.curve.InterpolationConstant;
import eu.esdihumboldt.util.geometry.interpolation.Interpolation;

/**
 * Base class for geometry handler that supported interpolation
 * 
 * @author Arun
 */
public abstract class InterpolationSupportedGeometryHandler
		extends FixedConstraintsGeometryHandler {

	private static final ALogger log = ALoggerFactory
			.getLogger(InterpolationSupportedGeometryHandler.class);
	private static final AtomicBoolean reportedWarning = new AtomicBoolean(false);
	private boolean keepOriginal;
	private double maxPositionalError;

	/**
	 * @return the keepOriginal
	 */
	protected boolean isKeepOriginal() {
		return keepOriginal;
	}

	/**
	 * @return the maxPositionalError
	 */
	protected double getMaxPositionalError() {
		return maxPositionalError;
	}

	/**
	 * Get required parameter for interpolation operation
	 * 
	 * @param reader {@link IOProvider} for reading parameters
	 */
	protected void getInterpolationRequiredParameter(IOProvider reader) {

		Double maxPositionalError = reader
				.getParameter(InterpolationConstant.INTERPOL_MAX_POSITION_ERROR).as(Double.class);
		if (maxPositionalError == null || maxPositionalError.doubleValue() <= 0) {
			if (reportedWarning.compareAndSet(false, true)) {
				log.warn(
						"Value of Max positional error parameter, for interpolation operation, is not valid. Default value has been taken.");
			}
			maxPositionalError = InterpolationConstant.DEFAULT_INTERPOL_MAX_POSITION_ERROR;
		}

		this.maxPositionalError = maxPositionalError.doubleValue();
		this.keepOriginal = !reader
				.getParameter(InterpolationConstant.INTERPOL_GEOMETRY_MOVE_ALL_TO_GRID)
				.as(Boolean.class,
						InterpolationConstant.DEFAULT_INTERPOL_GEOMETRY_MOVE_ALL_TO_GRID);
	}

	/**
	 * Move geometry coordinate to universal grid based on keepOriginal
	 * parameter
	 * 
	 * @param coordinates geometry coordinates
	 * @param reader the IO provider
	 * @return relocated geometry or same geometry
	 */
	protected Coordinate[] moveToUniversalGrid(final Coordinate[] coordinates, IOProvider reader) {
		getInterpolationRequiredParameter(reader);
		return moveToUniversalGrid(coordinates);
	}

	/**
	 * Move geometry coordinate to universal grid based on keepOriginal
	 * parameter
	 * 
	 * @param coordinates geometry coordinates
	 * @return relocated geometry or same geometry
	 */
	protected Coordinate[] moveToUniversalGrid(final Coordinate[] coordinates) {

		if (keepOriginal)
			return coordinates;
		else {
			List<Coordinate> newCoordinates = new ArrayList<>(coordinates.length);
			for (Coordinate coordinate : coordinates) {
				newCoordinates.add(Interpolation.pointToGrid(coordinate, maxPositionalError));
			}

			return newCoordinates.toArray(new Coordinate[newCoordinates.size()]);
		}
	}
}
