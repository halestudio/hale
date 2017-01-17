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

package eu.esdihumboldt.util.geometry.interpolation.segment;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.util.geometry.interpolation.AbstractInterpolationAlgorithm;
import eu.esdihumboldt.util.geometry.interpolation.ArcSegment;
import eu.esdihumboldt.util.geometry.interpolation.InterpolationUtil;
import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;

/**
 * Grid based interpolation algorithm.
 * 
 * @author Simon Templer
 */
public class SplitInterpolation extends AbstractInterpolationAlgorithm {

	@Override
	public LineString interpolateArc(Arc arc) {
		if (InterpolationUtil.isStraightLine(arc)) {
			// this happens when slopes are close to equal

			ArcByPoints byPoints = arc.toArcByPoints();
			// return points as-is
			return createLineString(new Coordinate[] { byPoints.getStartPoint(),
					byPoints.getMiddlePoint(), byPoints.getEndPoint() }, arc);
		}

		return interpolateToLineString(arc);
	}

	private LineString interpolateToLineString(Arc arc) {
		// arc segments to process
		Deque<ArcSegment> toProcess = new LinkedList<>();
		// start with full arc as segment
		toProcess.addFirst(new ArcSplitSegment(arc, getMaxPositionalError()));
		// list to collect atomic parts
		List<ArcSegment> parts = new LinkedList<>();

		// for every segment to process...
		while (!toProcess.isEmpty()) {
			ArcSegment segment = toProcess.pop();

			if (segment.isAtomic()) {
				// segment cannot be split
				// -> use for result
				parts.add(segment);
			}
			else {
				// otherwise split the segment in two and handle the parts
				toProcess.addFirst(segment.getSecondPart());
				toProcess.addFirst(segment.getFirstPart());
			}
		}

		// combine the segments to a single LineString
		List<Coordinate> coords = new ArrayList<>();
		for (int i = 0; i < parts.size(); i++) {
			ArcSegment part = parts.get(i);
			if (i == 0) {
				coords.add(part.getStartPoint());
			}
			Coordinate middle = part.getMiddlePoint();
			if (middle != null) {
				// should actually not occur
				InterpolationUtil.addIfDifferent(coords, middle);
			}
			InterpolationUtil.addIfDifferent(coords, part.getEndPoint());
		}

		return createLineString(coords.toArray(new Coordinate[coords.size()]), arc);
	}

}
