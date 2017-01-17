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

import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.util.geometry.interpolation.model.ComplexGeometry;

/**
 * Base class for interpolation algorithms.
 * 
 * @author Simon Templer
 */
public abstract class AbstractInterpolationAlgorithm implements InterpolationAlgorithm {

	private double maxPositionalError;
	private GeometryFactory factory;

	@Override
	public void configure(GeometryFactory factory, double maxPositionalError,
			Map<String, String> properties) {
		this.maxPositionalError = maxPositionalError;
		this.factory = factory;
	}

	/**
	 * @return the maximum positional error
	 */
	public double getMaxPositionalError() {
		return maxPositionalError;
	}

	/**
	 * @return the geometry factory
	 */
	@Override
	public GeometryFactory getGeometryFactory() {
		return factory;
	}

	/**
	 * Create a line string geometry.
	 * 
	 * @param coordinates the coordinates forming the line string
	 * @param original the original geometry
	 * @return the created geometry
	 */
	public LineString createLineString(Coordinate[] coordinates, ComplexGeometry original) {
		return new InterpolatedLineString(
				factory.getCoordinateSequenceFactory().create(coordinates), factory, original);
	}

}
