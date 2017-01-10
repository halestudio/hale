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
		// is a circle if the normalised angles are equal
		return com.vividsolutions.jts.algorithm.Angle
				.normalize(startAngle.getRadians()) == com.vividsolutions.jts.algorithm.Angle
						.normalize(endAngle.getRadians());
	}

	/**
	 * Determine the point at the given angle.
	 * 
	 * @param angle the angle
	 * @return the point at the given angle that lies on the arc's circle
	 */
	public Coordinate getPointAtAngle(Angle angle) {
		double x = centerPoint.x + (radius * Math.cos(angle.getRadians()));
		double y = centerPoint.y + (radius * Math.sin(angle.getRadians()));
		return new Coordinate(x, y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @author Arun Verma
	 */
	@Override
	public ArcByPoints toArcByPoints() {
		if (pointsRepresentation == null) {
			Angle middleAngle = Angle
					.fromRadians(startAngle.getRadians() + 0.5 * getAngleBetween().getRadians());

			// getting start coordinate
			Coordinate startPoint = getPointAtAngle(startAngle);

			// getting end coordinate
			Coordinate endPoint = getPointAtAngle(endAngle);

			// will generate middle coordinate to use already coded arc
			// interpolation
			Coordinate middlePoint = getPointAtAngle(middleAngle);

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
