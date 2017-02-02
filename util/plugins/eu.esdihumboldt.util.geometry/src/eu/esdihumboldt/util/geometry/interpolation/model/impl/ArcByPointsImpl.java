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

package eu.esdihumboldt.util.geometry.interpolation.model.impl;

import static eu.esdihumboldt.util.geometry.interpolation.InterpolationUtil.round;

import java.text.MessageFormat;

import com.vividsolutions.jts.geom.Coordinate;

import eu.esdihumboldt.util.geometry.interpolation.model.Angle;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;

/**
 * An arc represented by three points.
 * 
 * @author Simon Templer
 */
public class ArcByPointsImpl implements ArcByPoints {

	private final Coordinate startPoint;
	private final Coordinate middlePoint;
	private final Coordinate endPoint;

	private ArcByCenterPoint centerRepresentation;

	/**
	 * Create an Arc represented by three points.
	 * 
	 * @param startPoint the arc start point
	 * @param middlePoint the arc middle point (on the arc)
	 * @param endPoint the arc end point
	 */
	public ArcByPointsImpl(Coordinate startPoint, Coordinate middlePoint, Coordinate endPoint) {
		super();
		this.startPoint = startPoint;
		this.middlePoint = middlePoint;
		this.endPoint = endPoint;
	}

	@Override
	public boolean isCircle() {
		return startPoint.equals(endPoint);
	}

	@Override
	public ArcByCenterPoint toArcByCenterPoint() {
		if (centerRepresentation == null) {
			Coordinate centerPoint = calculateCenterPoint();

			double radius = Math.sqrt(Math.pow((startPoint.x - centerPoint.x), 2)
					+ Math.pow((startPoint.y - centerPoint.y), 2));

			Angle startAngle = Angle.angle(centerPoint, startPoint);
			Angle endAngle = Angle.angle(centerPoint, endPoint);

			boolean clockwise = isClockwise(centerPoint);

			centerRepresentation = new ArcByCenterPointImpl(centerPoint, radius, startAngle,
					endAngle, clockwise);
		}

		return centerRepresentation;
	}

	/**
	 * Determine if the arc is clockwise.
	 * 
	 * @author Arun Verma
	 * @param centerPoint the arc center point
	 * @return <code>true</code> if the arc is clockwise, false otherwise
	 */
	private boolean isClockwise(Coordinate centerPoint) {
		boolean cw = true;

		double c1Angle = Math.atan2(startPoint.y - centerPoint.y, startPoint.x - centerPoint.x);
		c1Angle = c1Angle * 360 / (2 * Math.PI);
		double c2Angle = Math.atan2(middlePoint.y - centerPoint.y, middlePoint.x - centerPoint.x);
		c2Angle = c2Angle * 360 / (2 * Math.PI);
		double c3Angle = Math.atan2(endPoint.y - centerPoint.y, endPoint.x - centerPoint.x);
		c3Angle = c3Angle * 360 / (2 * Math.PI);

		if (c1Angle > 0) {
			if (c2Angle > 0) {
				if (c3Angle > 0) {
					if (c3Angle > c2Angle && c2Angle > c1Angle) {
						cw = false;
					}
				}
				else { // c3Angle <0
					if (c2Angle > c1Angle) {
						cw = false;
					}
				}
			}
			else { // c2Angle<0
				if (c3Angle > 0) {
					if (c3Angle < c1Angle) {
						cw = false;
					}
				}
				else { // c3Angle <0
					if (c2Angle < c3Angle) {
						cw = false;
					}
				}
			}
		}
		else { // c1Angle <0
			if (c2Angle > 0) {
				if (c3Angle > 0) {
					if (c3Angle > c2Angle) {
						cw = false;
					}
				}
				else { // c3Angle <0
					if (c3Angle < c1Angle) {
						cw = false;
					}
				}
			}
			else { // c2Angle<0
				if (c3Angle > 0) {
					if (c2Angle > c1Angle) {
						cw = false;
					}
				}
				else { // c3Angle <0
					if (c2Angle > c1Angle) {
						if (c3Angle > c2Angle) {
							cw = false;
						}
						else if (c3Angle < c1Angle) {
							cw = false;
						}
					}
					else { // c2Angle < C1Angle
						if (c3Angle > c2Angle) {
							cw = false;
						}
					}
				}
			}

		}

		return cw;
	}

	@Override
	public Coordinate getStartPoint() {
		return startPoint;
	}

	@Override
	public Coordinate getEndPoint() {
		return endPoint;
	}

	@Override
	public Coordinate getMiddlePoint() {
		return middlePoint;
	}

	/**
	 * Calculate the center point.
	 * 
	 * @author Arun Verma
	 * @return the center point
	 */
	protected Coordinate calculateCenterPoint() {
		// we can get two lines using above slope let's say p1p2: (ya =
		// m_a(x1-x0) + y0) and p2p3: (yb = m_a(x2-x1) + y1)

		Coordinate AB_Mid = new Coordinate((startPoint.x + middlePoint.x) / 2,
				(startPoint.y + middlePoint.y) / 2);

		if (startPoint.equals(endPoint)) {
			// start is end point -> circle
			return AB_Mid;
		}

		Coordinate BC_Mid = new Coordinate((middlePoint.x + endPoint.x) / 2,
				(middlePoint.y + endPoint.y) / 2);

		// The center of the circle is the intersection of the two lines
		// perpendicular to and passing through the midpoints of the lines p1p2
		// and p2p3.

		double yDelta_a = middlePoint.y - startPoint.y;
		double xDelta_a = middlePoint.x - startPoint.x;
		double yDelta_b = endPoint.y - middlePoint.y;
		double xDelta_b = endPoint.x - middlePoint.x;

		double aSlope = yDelta_a / xDelta_a;

		double bSlope = yDelta_b / xDelta_b;

		double centerX = 0;
		double centerY = 0;
		if (yDelta_a == 0) // aSlope == 0
		{
			centerX = AB_Mid.x;
			if (xDelta_b == 0) // bSlope == INFINITY
			{
				centerY = BC_Mid.y;
			}
			else {
				centerY = BC_Mid.y + (BC_Mid.x - centerX) / bSlope;
			}
		}
		else if (yDelta_b == 0) // bSlope == 0
		{
			centerX = BC_Mid.x;
			if (xDelta_a == 0) // aSlope == INFINITY
			{
				centerY = AB_Mid.y;
			}
			else {
				centerY = AB_Mid.y + (AB_Mid.x - centerX) / aSlope;
			}
		}
		else if (xDelta_a == 0) // aSlope == INFINITY
		{
			centerY = AB_Mid.y;
			centerX = bSlope * (BC_Mid.y - centerY) + BC_Mid.x;
		}
		else if (xDelta_b == 0) // bSlope == INFINITY
		{
			centerY = BC_Mid.y;
			centerX = aSlope * (AB_Mid.y - centerY) + AB_Mid.x;
		}
		else //
		{
			if (round(aSlope, 4) != round(bSlope, 4)) {
				centerX = ((aSlope * bSlope * (BC_Mid.y - AB_Mid.y)) + (aSlope * BC_Mid.x)
						- (bSlope * AB_Mid.x)) / (aSlope - bSlope);
				centerY = AB_Mid.y - ((centerX - AB_Mid.x) / aSlope);
			}
			else {
				centerX = Double.POSITIVE_INFINITY;
				centerY = Double.POSITIVE_INFINITY;
			}

		}
		return new Coordinate(centerX, centerY);
	}

	@Override
	public String toString() {
		return MessageFormat.format("Arc({0}, {1}, {2})", startPoint, middlePoint, endPoint);
	}

}
