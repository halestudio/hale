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

import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import eu.esdihumboldt.util.geometry.interpolation.AbstractInterpolationAlgorithm;
import eu.esdihumboldt.util.geometry.interpolation.InterpolationUtil;
import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class GridInterpolation extends AbstractInterpolationAlgorithm {

	private final boolean moveAllToGrid = false;

	@Override
	public void configure(GeometryFactory factory, double maxPositionalError,
			Map<String, String> properties) {
		super.configure(factory, maxPositionalError, properties);

		// FIXME handle setting for moving all coordinates to grid
	}

	@Override
	public Geometry interpolateArc(Arc arc) {
		if (InterpolationUtil.isStraightLine(arc)) {
			// this happens when slopes are close to equal

			ArcByPoints byPoints = arc.toArcByPoints();
			if (moveAllToGrid) {
				// TODO move to grid
			}
			else {
				// return points as-is
				return createLineString(new Coordinate[] { byPoints.getStartPoint(),
						byPoints.getMiddlePoint(), byPoints.getEndPoint() }, arc);
			}
		}

		// TODO Auto-generated method stub
		return null;
	}

}
