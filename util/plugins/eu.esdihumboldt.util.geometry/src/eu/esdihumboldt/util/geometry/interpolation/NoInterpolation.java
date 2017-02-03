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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;

/**
 * Interpolation algorithm that always represents arcs by three points.
 * 
 * @author Simon Templer
 */
public class NoInterpolation extends AbstractInterpolationAlgorithm {

	/**
	 * Identifier of the algorithm in the extension point.
	 */
	public static final String EXTENSION_ID = "none";

	@Override
	public LineString interpolateArc(Arc arc) {
		ArcByPoints byPoints = arc.toArcByPoints();
		// return points as-is
		return createLineString(new Coordinate[] { byPoints.getStartPoint(),
				byPoints.getMiddlePoint(), byPoints.getEndPoint() }, arc);
	}

}
