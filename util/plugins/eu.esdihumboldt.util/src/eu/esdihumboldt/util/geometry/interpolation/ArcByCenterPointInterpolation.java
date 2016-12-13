/*
 * Copyright (c) 2016 wetransform GmbH
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

/**
 * Arc by center point interpolation
 * 
 * @author Arun
 */
public class ArcByCenterPointInterpolation extends Interpolation<LineString> {

	private final Coordinate center;
	private final double radius;
	private final boolean isCircle;

	/**
	 * Constructor for circle interpolation by center point
	 * 
	 * <p>
	 * As there are no coordinates, internally generated coordinates of circle
	 * will be moved to universal grid
	 * </p>
	 * 
	 * @param center center coordinate
	 * @param radius radius of circle
	 * @param maxPositionalError max positional error
	 */
	public ArcByCenterPointInterpolation(Coordinate center, double radius,
			double maxPositionalError) {
		this(center, radius, -1, -1, maxPositionalError, true);
	}

	/**
	 * Constructor for arc interpolation by center point
	 * 
	 * <p>
	 * As there are no coordinates, internally generated coordinates of arc will
	 * be moved to universal grid
	 * </p>
	 * 
	 * @param center center coordinate
	 * @param radius radius of arc
	 * @param startAngle specifies the bearing of the arc at the start
	 * @param endAngle specifies the bearing of the arc at the end
	 * @param maxPositionalError max positional error
	 */
	public ArcByCenterPointInterpolation(Coordinate center, double radius, double startAngle,
			double endAngle, double maxPositionalError) {
		this(center, radius, startAngle, endAngle, maxPositionalError, false);
	}

	/**
	 * Constructor
	 * 
	 * As there are no coordinates given, every generated coordinates of
	 * arc/circle will be moved to universal grid
	 * 
	 * @param center center coordinate
	 * @param radius radius of arc
	 * @param startAngle specifies the bearing of the arc at the start
	 * @param endAngle specifies the bearing of the arc at the end
	 * @param maxPositionalError max positional error
	 * @param isCircle true, if geometry is circle else false
	 */
	private ArcByCenterPointInterpolation(Coordinate center, double radius, double startAngle,
			double endAngle, double maxPositionalError, boolean isCircle) {
		super(evaluateArcCoordinates(center, radius, startAngle, endAngle, isCircle),
				maxPositionalError, false);

		this.center = center;
		this.isCircle = isCircle;
		this.radius = radius;
	}

	/**
	 * @see eu.esdihumboldt.util.geometry.interpolation.Interpolation#validateRawCoordinates()
	 */
	@Override
	protected boolean validateRawCoordinates() {
		return true;
	}

	/**
	 * @see eu.esdihumboldt.util.geometry.interpolation.Interpolation#getInterpolatedGeometry()
	 */
	@Override
	protected LineString getInterpolatedGeometry() {

		ArcInterpolation arc = new ArcInterpolation(this.rawGeometryCoordinates,
				this.MAX_POSITIONAL_ERROR, this.keepOriginal, this.isCircle, this.center,
				this.radius);

		return arc.getInterpolatedGeometry();
	}

	private static Coordinate[] evaluateArcCoordinates(Coordinate centerPoint, double radius,
			double startAngle, double endAngle, boolean isCircle) {

		double startAngleFromX;
		double endAngleFromX;
		double middleAngleFromX;
		// for circle
		if (isCircle) {
			startAngleFromX = 0;
			middleAngleFromX = 90;
			endAngleFromX = 180;
		}
		else {
			// As angles are bearings, we have to evaluate angles from X axis to
			// generate coordinate
			if (startAngle < 90)
				startAngleFromX = 90 - startAngle;
			else
				startAngleFromX = 360 - (startAngle - 90);

			if (endAngle < 90)
				endAngleFromX = 90 - endAngle;
			else
				endAngleFromX = 360 - (endAngle - 90);

			double middleAngle = round(startAngle + (0.5 * (endAngle - startAngle)), 3);
			if (middleAngle < 90)
				middleAngleFromX = 90 - middleAngle;
			else
				middleAngleFromX = 360 - (middleAngle - 90);
		}

		// getting start coordinate
		double x = round(centerPoint.x + (radius * Math.cos(Math.toRadians(startAngleFromX))), 4);
		double y = round(centerPoint.y + (radius * Math.sin(Math.toRadians(startAngleFromX))), 4);
		Coordinate startArcCoordinate = new Coordinate(x, y);

		// getting end coordinate
		x = round(centerPoint.x + (radius * Math.cos(Math.toRadians(endAngleFromX))), 4);
		y = round(centerPoint.y + (radius * Math.sin(Math.toRadians(endAngleFromX))), 4);
		Coordinate endArcCoordinate = new Coordinate(x, y);

		// will generate middle coordinate to use already coded arc
		// interpolation
		x = round(centerPoint.x + (radius * Math.cos(Math.toRadians(middleAngleFromX))), 4);
		y = round(centerPoint.y + (radius * Math.sin(Math.toRadians(middleAngleFromX))), 4);
		Coordinate middleArcCoordinate = new Coordinate(x, y);

		return new Coordinate[] { startArcCoordinate, middleArcCoordinate, endArcCoordinate };
	}

}
