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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcString;

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
	LineString interpolateArc(Arc arc);

	/**
	 * Interpolate an arc string.
	 * 
	 * @param arcs the arc string to interpolate
	 * @return the interpolated geometry
	 */
	default LineString interpolateArcString(ArcString arcs) {
		List<Coordinate> coords = new ArrayList<>();

		List<Arc> arcList = new ArrayList<>(arcs.getArcs());
		for (int i = 0; i < arcList.size(); i++) {
			Arc arc = arcList.get(i);
			LineString interpolated = interpolateArc(arc);
			Coordinate[] lineCoords = interpolated.getCoordinates();
			int startIndex = 1;
			if (i == 0) {
				startIndex = 0;
			}
			for (int j = startIndex; j < lineCoords.length; j++) {
				Coordinate coord = lineCoords[j];

				coords.add(coord);
			}
		}

		return new InterpolatedLineString(getGeometryFactory().getCoordinateSequenceFactory()
				.create(coords.toArray(new Coordinate[coords.size()])), getGeometryFactory(), arcs);
	}

	/**
	 * @return the geometry factory associated with the algorithm
	 */
	GeometryFactory getGeometryFactory();

}
