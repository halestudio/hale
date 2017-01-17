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

import com.vividsolutions.jts.geom.Coordinate;

import eu.esdihumboldt.util.geometry.interpolation.ArcSegment;
import eu.esdihumboldt.util.geometry.interpolation.model.Angle;
import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;
import eu.esdihumboldt.util.geometry.interpolation.model.impl.ArcByCenterPointImpl;

/**
 * Segment of an arc for gridded interpolation.
 * 
 * @author Simon Templer
 */
public class ArcGridSegment implements ArcSegment {

	private final Arc arc;

	private final Coordinate start;
	private final Coordinate end;
	private final Coordinate middle;
	private final double gridSize;

	private final boolean atomic;

	/**
	 * Create a new arc segment.
	 * 
	 * @param arc the arc the segment should represent
	 * @param moveAllToGrid if all points should be moved to the grid
	 * @param gridSize the grid size, i.e. the grid cell height and width
	 */
	public ArcGridSegment(Arc arc, boolean moveAllToGrid, double gridSize) {
		this.arc = arc;
		this.gridSize = gridSize;

		ArcByPoints byPoints = arc.toArcByPoints();
		Coordinate startOnGrid = GridUtil.movePointToGrid(byPoints.getStartPoint(), gridSize);
		Coordinate endOnGrid = GridUtil.movePointToGrid(byPoints.getEndPoint(), gridSize);

		// determine if Arc can be split
		Coordinate middleOnGrid = GridUtil.movePointToGrid(byPoints.getMiddlePoint(), gridSize);
		atomic = middleOnGrid.equals(startOnGrid) || middleOnGrid.equals(endOnGrid);

		// determine middle
		if (atomic) {
			// same as start or end
			middle = null;
		}
		else {
			if (moveAllToGrid) {
				middle = middleOnGrid;
			}
			else if (arc instanceof ArcByPoints) {
				// original arc already features the point
				// -> use as is
				middle = byPoints.getMiddlePoint();
			}
			else {
				// point was not explicitly defined - use grid point
				middle = middleOnGrid;
			}
		}

		start = moveAllToGrid ? startOnGrid : byPoints.getStartPoint();
		end = moveAllToGrid ? endOnGrid : byPoints.getEndPoint();
	}

	@Override
	public boolean isAtomic() {
		return atomic;
	}

	@Override
	public ArcSegment getSecondPart() {
		ArcByCenterPoint byCenter = arc.toArcByCenterPoint();
		Angle middleAngle = Angle.fromRadians(byCenter.getStartAngle().getRadians()
				+ 0.5 * byCenter.getAngleBetween().getRadians());
		Arc part = new ArcByCenterPointImpl(byCenter.getCenterPoint(), byCenter.getRadius(),
				middleAngle, byCenter.getEndAngle(), byCenter.isClockwise());

		// always move points in sub-segments to grid, but assure the points
		// match the parent segment's
		return new FixedStartEndGridSegment(part, true, gridSize, getMiddlePoint(), getEndPoint());
	}

	@Override
	public ArcSegment getFirstPart() {
		ArcByCenterPoint byCenter = arc.toArcByCenterPoint();
		Angle middleAngle = Angle.fromRadians(byCenter.getStartAngle().getRadians()
				+ 0.5 * byCenter.getAngleBetween().getRadians());
		Arc part = new ArcByCenterPointImpl(byCenter.getCenterPoint(), byCenter.getRadius(),
				byCenter.getStartAngle(), middleAngle, byCenter.isClockwise());

		// always move points in sub-segments to grid, but assure the points
		// match the parent segment's
		return new FixedStartEndGridSegment(part, true, gridSize, getStartPoint(),
				getMiddlePoint());
	}

	@Override
	public Coordinate getStartPoint() {
		return start;
	}

	@Override
	public Coordinate getMiddlePoint() {
		return middle;
	}

	@Override
	public Coordinate getEndPoint() {
		return end;
	}

}
