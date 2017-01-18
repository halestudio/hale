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

package eu.esdihumboldt.hale.io.gml.geometry.handler.internal;

import static eu.esdihumboldt.hale.common.instance.geometry.InterpolationHelper.PARAMETER_INTERPOLATION_ALGORITHM;
import static eu.esdihumboldt.hale.common.instance.geometry.InterpolationHelper.PARAMETER_MAX_POSITION_ERROR;
import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.geometry.InterpolationHelper;
import eu.esdihumboldt.util.geometry.interpolation.NoInterpolation;
import eu.esdihumboldt.util.geometry.interpolation.grid.GridInterpolation;
import eu.esdihumboldt.util.geometry.interpolation.grid.GridUtil;
import eu.esdihumboldt.util.geometry.interpolation.split.SplitInterpolation;

/**
 * Helper for easily creating/accessing interpolation configurations.
 * 
 * @author Simon Templer
 */
public abstract class InterpolationConfigurations {

	/**
	 * Interpolation configuration to skip interpolation (arc are basically
	 * represented by three points)
	 */
	public static final ReaderConfiguration NONE = new ReaderConfiguration() {

		@Override
		public void apply(IOProvider instanceReader) {
			instanceReader.setParameter(PARAMETER_INTERPOLATION_ALGORITHM,
					Value.of(NoInterpolation.EXTENSION_ID));
		}

	};

	/**
	 * Create an interpolation configuration with segment interpolation and the
	 * given maximum positional error
	 * 
	 * @param maxPositionalError the maximum positional error
	 * @return the interpolation configuration
	 */
	public static ReaderConfiguration segment(double maxPositionalError) {
		return new ReaderConfiguration() {

			@Override
			public void apply(IOProvider instanceReader) {
				instanceReader.setParameter(PARAMETER_INTERPOLATION_ALGORITHM,
						Value.of(SplitInterpolation.EXTENSION_ID));
				instanceReader.setParameter(PARAMETER_MAX_POSITION_ERROR,
						Value.of(maxPositionalError));
			}

		};
	}

	/**
	 * Create an interpolation configuration with grid interpolation and the
	 * given maximum positional error
	 * 
	 * @param maxPositionalError the maximum positional error
	 * @param moveToGrid if all geometries/coordinates should be moved to the
	 *            grid
	 * @return the interpolation configuration
	 */
	public static ReaderConfiguration grid(double maxPositionalError, boolean moveToGrid) {
		final double gridSize = GridUtil.getGridSize(maxPositionalError);
		return new ReaderConfiguration() {

			@Override
			public void apply(IOProvider instanceReader) {
				instanceReader.setParameter(PARAMETER_INTERPOLATION_ALGORITHM,
						Value.of(GridInterpolation.EXTENSION_ID));
				instanceReader.setParameter(PARAMETER_MAX_POSITION_ERROR,
						Value.of(maxPositionalError));
				instanceReader.setParameter(GridInterpolation.PARAMETER_MOVE_ALL_TO_GRID,
						Value.of(moveToGrid));
			}

			@Override
			public Consumer<Geometry> geometryChecker() {
				return (geom) -> {
					// check if every coordinate is on the grid
					for (Coordinate c : geom.getCoordinates()) {
						checkOnGrid(c);
					}
				};
			}

			public void checkOnGrid(Coordinate c) {
				checkOnGrid(c.x, gridSize);
				checkOnGrid(c.y, gridSize);
			}

			public void checkOnGrid(double ord, double gridSize) {
				double fact = ord / gridSize;
				assertEquals("Ordinate does not align with the grid", Math.round(fact), fact, 1e-8);
			}

		};
	}

	/**
	 * Grid interpolation with default settings and all coordinates moved to
	 * grid.
	 */
	public static final ReaderConfiguration ALL_TO_GRID_DEFAULT = grid(
			InterpolationHelper.DEFAULT_MAX_POSITION_ERROR, true);

}
