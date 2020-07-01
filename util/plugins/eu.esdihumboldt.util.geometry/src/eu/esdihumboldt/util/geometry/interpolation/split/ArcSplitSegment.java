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

package eu.esdihumboldt.util.geometry.interpolation.split;

import org.locationtech.jts.geom.Coordinate;

import eu.esdihumboldt.util.geometry.interpolation.ArcSegment;
import eu.esdihumboldt.util.geometry.interpolation.model.Angle;
import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;
import eu.esdihumboldt.util.geometry.interpolation.model.impl.ArcByCenterPointImpl;

/**
 * Segment of an arc for split interpolation.
 * 
 * @author Simon Templer
 */
public class ArcSplitSegment implements ArcSegment {

	private final Arc arc;

	private final Coordinate start;
	private final Coordinate end;
	private final Coordinate middle;

	private final boolean atomic;

	private final double maxPositionalError;

	/**
	 * Create a new arc segment.
	 * 
	 * @param arc the arc the segment should represent
	 * @param maxPositionalError the maximum positional error
	 */
	public ArcSplitSegment(Arc arc, double maxPositionalError) {
		this.arc = arc;
		this.maxPositionalError = maxPositionalError;

		ArcByPoints byPoints = arc.toArcByPoints();

		start = byPoints.getStartPoint();
		middle = byPoints.getMiddlePoint();
		end = byPoints.getEndPoint();

		// XXX is this condition adequate?
		atomic = start.distance(middle) < maxPositionalError;
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

		return new FixedStartEndSplitSegment(part, maxPositionalError, getMiddlePoint(),
				getEndPoint());
	}

	@Override
	public ArcSegment getFirstPart() {
		ArcByCenterPoint byCenter = arc.toArcByCenterPoint();
		Angle middleAngle = Angle.fromRadians(byCenter.getStartAngle().getRadians()
				+ 0.5 * byCenter.getAngleBetween().getRadians());
		Arc part = new ArcByCenterPointImpl(byCenter.getCenterPoint(), byCenter.getRadius(),
				byCenter.getStartAngle(), middleAngle, byCenter.isClockwise());

		return new FixedStartEndSplitSegment(part, maxPositionalError, getStartPoint(),
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
