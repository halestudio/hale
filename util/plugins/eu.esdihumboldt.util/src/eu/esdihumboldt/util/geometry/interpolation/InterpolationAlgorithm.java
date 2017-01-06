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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import eu.esdihumboldt.util.geometry.interpolation.model.Arc;

/**
 * Interface for interpolations algorithms.
 * 
 * @author Simon Templer
 */
public interface InterpolationAlgorithm {

	/**
	 * Configure the algorithm.
	 * 
	 * @param factory the geometry factory
	 * @param maxPositionalError the maximum positional error
	 * @param properties interpolation configuration properties
	 */
	void configure(GeometryFactory factory, double maxPositionalError,
			Map<String, String> properties);

	/**
	 * Interpolate an arc.
	 * 
	 * @param arc the arc to interpolate
	 * @return the interpolated geometry
	 */
	Geometry interpolateArc(Arc arc);

}
