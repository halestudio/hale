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
 * An arc represented by center point and radius.
 * 
 * @author Simon Templer
 */
public class ArcByCenterPointImpl implements ArcByCenterPoint {

	private final Coordinate centerPoint;
	private final double radius;
	private final Angle startAngle;
	private final Angle endAngle;
	private final boolean clockwise;

	private ArcByPoints pointsRepresentation;

	/**
	 * Create an Arc represented by center point and radius.
	 * 
	 * @param centerPoint the center point
	 * @param radius the radius
	 * @param startAngle the start angle
	 * @param endAngle the end angle
	 * @param clockwise if the Arc is clockwise (rather than counter-clockwise)
	 */
	public ArcByCenterPointImpl(Coordinate centerPoint, double radius, Angle startAngle,
			Angle endAngle, boolean clockwise) {
		super();
		this.centerPoint = centerPoint;
		this.radius = radius;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.clockwise = clockwise;
	}

	@Override
	public boolean isCircle() {
		return startAngle.equals(endAngle);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @author Arun Verma
	 */
	@Override
	public ArcByPoints toArcByPoints() {
		if (pointsRepresentation == null) {
			double startAngleFromX;
			double endAngleFromX;
			double middleAngleFromX;
			// FIXME special handling for circle?
			// FIXME this angle conversion needed for GML Arcs?
			// for circle
//			if (isCircle) {
//				startAngleFromX = 0;
//				middleAngleFromX = 90;
//				endAngleFromX = 180;
//			}
//			else {
//				// As angles are bearings, we have to evaluate angles from X axis to
//				// generate coordinate
//				if (startAngle < 90)
//					startAngleFromX = 90 - startAngle;
//				else
//					startAngleFromX = 360 - (startAngle - 90);
//
//				if (endAngle < 90)
//					endAngleFromX = 90 - endAngle;
//				else
//					endAngleFromX = 360 - (endAngle - 90);
//
//				double middleAngle = round(startAngle + (0.5 * (endAngle - startAngle)), 3);
//				if (middleAngle < 90)
//					middleAngleFromX = 90 - middleAngle;
//				else
//					middleAngleFromX = 360 - (middleAngle - 90);
//			}

			startAngleFromX = startAngle.getDegrees();
			endAngleFromX = endAngle.getDegrees();
			middleAngleFromX = startAngleFromX + 0.5 * getAngleBetween().getDegrees();

			// FIXME rounding necessary?
			// XXX and does it make sense? e.g. for lat/lon?!!!

			// getting start coordinate
			double x = round(centerPoint.x + (radius * Math.cos(Math.toRadians(startAngleFromX))),
					4);
			double y = round(centerPoint.y + (radius * Math.sin(Math.toRadians(startAngleFromX))),
					4);
			Coordinate startPoint = new Coordinate(x, y);

			// getting end coordinate
			x = round(centerPoint.x + (radius * Math.cos(Math.toRadians(endAngleFromX))), 4);
			y = round(centerPoint.y + (radius * Math.sin(Math.toRadians(endAngleFromX))), 4);
			Coordinate endPoint = new Coordinate(x, y);

			// will generate middle coordinate to use already coded arc
			// interpolation
			x = round(centerPoint.x + (radius * Math.cos(Math.toRadians(middleAngleFromX))), 4);
			y = round(centerPoint.y + (radius * Math.sin(Math.toRadians(middleAngleFromX))), 4);
			Coordinate middlePoint = new Coordinate(x, y);

			pointsRepresentation = new ArcByPointsImpl(startPoint, middlePoint, endPoint);
		}

		return pointsRepresentation;
	}

	@Override
	public Coordinate getCenterPoint() {
		return centerPoint;
	}

	@Override
	public double getRadius() {
		return radius;
	}

	@Override
	public Angle getStartAngle() {
		return startAngle;
	}

	@Override
	public Angle getEndAngle() {
		return endAngle;
	}

	@Override
	public boolean isClockwise() {
		return clockwise;
	}

	@Override
	public Angle getAngleBetween() {
		double diff = endAngle.getRadians() - startAngle.getRadians();
		if (!isClockwise()) {
			// normalise to positive value between 0 (exclusive) and 2Pi
			if (diff > 0) {
				while (diff > Angle.PI_TIMES_2) {
					diff = diff - Angle.PI_TIMES_2;
				}
			}
			else {
				while (diff <= 0) {
					diff = diff + Angle.PI_TIMES_2;
				}
			}
		}
		else {
			// normalise to negative value between 0 (exclusive) and -2Pi
			if (diff < 0) {
				while (diff < -Angle.PI_TIMES_2) {
					diff = diff + Angle.PI_TIMES_2;
				}
			}
			else {
				while (diff >= 0) {
					diff = diff - Angle.PI_TIMES_2;
				}
			}
		}

		return Angle.fromRadians(diff);
	}

	@Override
	public String toString() {
		return MessageFormat.format("Arc(c: {0}, r: {1}, {2} - {3} ", centerPoint, radius,
				startAngle.getDegrees(), endAngle.getDegrees())
				+ ((clockwise) ? ("CW)") : ("CCW)"));
	}

}
