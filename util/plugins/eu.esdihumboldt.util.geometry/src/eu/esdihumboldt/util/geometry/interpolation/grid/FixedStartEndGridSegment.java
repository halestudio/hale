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

import org.locationtech.jts.geom.Coordinate;

import eu.esdihumboldt.util.geometry.interpolation.model.Arc;

/**
 * Segment of an arc for gridded interpolation with provided fixed start and end
 * points.
 * 
 * @author Simon Templer
 */
public class FixedStartEndGridSegment extends ArcGridSegment {

	private final Coordinate start;
	private final Coordinate end;

	/**
	 * Create a new arc segment.
	 * 
	 * @param arc the arc the segment should represent
	 * @param moveAllToGrid if all points should be moved to the grid
	 * @param gridSize the grid size, i.e. the grid cell height and width
	 * @param start the start point
	 * @param end the end point
	 */
	public FixedStartEndGridSegment(Arc arc, boolean moveAllToGrid, double gridSize,
			Coordinate start, Coordinate end) {
		super(arc, moveAllToGrid, gridSize);
		this.start = start;
		this.end = end;
	}

	@Override
	public Coordinate getStartPoint() {
		return start;
	}

	@Override
	public Coordinate getEndPoint() {
		return end;
	}

}
