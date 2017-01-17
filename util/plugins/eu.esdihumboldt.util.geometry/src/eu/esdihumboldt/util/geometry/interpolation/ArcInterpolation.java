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

import static eu.esdihumboldt.util.geometry.interpolation.InterpolationUtil.round;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Interpolation of a arc geometry
 * 
 * @author Arun
 */
@Deprecated
public class ArcInterpolation extends Interpolation<LineString> {

	private static final ALogger log = ALoggerFactory.getLogger(ArcInterpolation.class);

	private Coordinate lastGridCoordinateOfArc = null;

	private boolean isArcClockWise;

	private final boolean isCircle;

	private final Coordinate center;
	private final double radius;

	private Coordinate nextArcCoordinate = null;

	private double lastDeservedNeighbourAngle;

	/**
	 * Constructor
	 * 
	 * @param coordinates raw geometry coordinates
	 * @param maxPositionalError maximum positional error
	 * @param keepOriginal keeps original points in interpolation
	 */
	public ArcInterpolation(Coordinate[] coordinates, double maxPositionalError,
			boolean keepOriginal) {
		this(coordinates, maxPositionalError, keepOriginal, false, null, 0);
	}

	/**
	 * Constructor
	 * 
	 * @param coordinates raw geometry coordinates
	 * @param maxPositionalError maximum positional error
	 * @param keepOriginal keeps original points in interpolation
	 * @param isCircle true, if coordinates are of circle type
	 */
	public ArcInterpolation(Coordinate[] coordinates, double maxPositionalError,
			boolean keepOriginal, boolean isCircle) {
		this(coordinates, maxPositionalError, keepOriginal, isCircle, null, 0);
	}

	/**
	 * Constructor
	 * 
	 * @param coordinates raw geometry coordinates
	 * @param maxPositionalError maximum positional error
	 * @param keepOriginal keeps original points in interpolation
	 * @param isCircle true, if coordinates are of circle type
	 * @param center center of arc
	 * @param radius radius of circle
	 */
	public ArcInterpolation(Coordinate[] coordinates, double maxPositionalError,
			boolean keepOriginal, boolean isCircle, Coordinate center, double radius) {
		super(coordinates, maxPositionalError, keepOriginal);
		this.isCircle = isCircle;
		this.center = center;
		this.radius = radius;
	}

	/**
	 * @see eu.esdihumboldt.util.geometry.interpolation.Interpolation#validateRawCoordinates()
	 */
	@Override
	protected boolean validateRawCoordinates() {
		if (rawGeometryCoordinates.length != 3) {
			log.error("Invalid arc geometry. Arc must be represented by 3 points.");
			return false;
		}
		return true;
	}

	/**
	 * @see eu.esdihumboldt.util.geometry.interpolation.Interpolation#getInterpolatedGeometry()
	 */
	@Override
	protected LineString getInterpolatedGeometry() {

		if (areOnStraightLine(rawGeometryCoordinates)) {
			// this is only happen when slopes are getting equal
			Coordinate[] generatedCoordinates = new Coordinate[rawGeometryCoordinates.length];

			LineString lineString = null;
			try {

				for (int i = 0; i < rawGeometryCoordinates.length; i++) {
					if (!keepOriginal)
						generatedCoordinates[i] = pointToGrid(rawGeometryCoordinates[i]);
					else
						generatedCoordinates[i] = rawGeometryCoordinates[i];
				}
				lineString = new GeometryFactory().createLineString(generatedCoordinates);
			} catch (Exception ex) {
				log.error("Error in interpolation of arc", ex);
			}
			return lineString;
		}

		// Calculate center of Arc
		Coordinate centerOfArc = center;
		if (centerOfArc == null) {
			centerOfArc = calculateCenterPoint(rawGeometryCoordinates);
		}

		// is Arc clockwise?
		isArcClockWise = getOrderOfArc(centerOfArc, rawGeometryCoordinates);

		// Calculate radius of Arc
		double radiusOfArc = this.radius;
		if (radiusOfArc == 0) {
			radiusOfArc = Math.sqrt(Math.pow((rawGeometryCoordinates[0].x - centerOfArc.x), 2)
					+ Math.pow((rawGeometryCoordinates[0].y - centerOfArc.y), 2));
		}

		// return Line String Geometry
		return interpolateToLineString(rawGeometryCoordinates, centerOfArc, radiusOfArc);
	}

	private LineString interpolateToLineString(Coordinate[] arcCoordinates, Coordinate center,
			double radius) {

		List<Coordinate> generatedCoordinates = new ArrayList<Coordinate>();

		// Add first Arc coordinate
		generatedCoordinates.add(keepOriginal ? arcCoordinates[0] : pointToGrid(arcCoordinates[0]));

		int coordinatesCount;
		if (isCircle)
			coordinatesCount = arcCoordinates.length;
		else
			coordinatesCount = arcCoordinates.length - 1;

		int current;
		int next;
		for (current = 0; current < coordinatesCount; current++) {
			next = current + 1;
			if (isCircle && next == coordinatesCount)
				next = 0;

			nextArcCoordinate = arcCoordinates[next];

			// Add coordinates up to next arc coordinates
			try {
				generatedCoordinates.addAll(generateLineStringCoordinates(arcCoordinates[current],
						arcCoordinates[next], center, radius));
			} catch (Exception ex) {
				StringBuilder builder = new StringBuilder();
				for (Coordinate c : arcCoordinates) {
					builder.append(c.toString() + ",");
				}
				throw new RuntimeException(ex.toString() + " for " + (isCircle ? "circle" : "arc")
						+ " with coordinates " + builder.toString());
			}

			// Add next Arc coordinate
			if (!keepOriginal) {
				if (!isDuplicateCoordinate(arcCoordinates[next]))
					generatedCoordinates.add(lastGridCoordinateOfArc);
			}
			else {
				if (lastGridCoordinateOfArc == null
						|| !arcCoordinates[next].equals(lastGridCoordinateOfArc))
					generatedCoordinates.add(arcCoordinates[next]);
			}
		}

		// now, we have all coordinates of line string. So then just create it.
		LineString lineString = null;
		try {
			lineString = new GeometryFactory().createLineString(
					generatedCoordinates.toArray(new Coordinate[generatedCoordinates.size()]));
		} catch (Exception ex) {
			log.error("Error creating LineString from interpolated coordinates of arc", ex);
			if (!isCircle) {
				lineString = new GeometryFactory().createLineString(rawGeometryCoordinates);
			}
		}

		return lineString;
	}

	private List<Coordinate> generateLineStringCoordinates(Coordinate c1, Coordinate c2,
			Coordinate center, double radius) throws Exception {

		List<Coordinate> list = new ArrayList<>();

		// calculate angle between given two coordinates
		double angle = getAngleBetweenTwoPoints(c1, c2, center);
		Coordinate deservedNeighbour = c1;
		lastDeservedNeighbourAngle = angle;

		while (!isNextArcCoordinateNeighbour(deservedNeighbour)) {
			deservedNeighbour = generateNextLineStringCoordinate(deservedNeighbour,
					nextArcCoordinate, center, radius, lastDeservedNeighbourAngle,
					NEXT_COORDINATE_DISTANCE);
			if (!isDuplicateCoordinate(deservedNeighbour))
				list.add(lastGridCoordinateOfArc);

		}
		return list;
	}

	private Coordinate generateNextLineStringCoordinate(Coordinate current,
			Coordinate nextArcCoordinate, Coordinate center, double radius, double maxAngle,
			double nextPointDistance) {
		double deservedNeighbourAngle = 0;

		Coordinate mostDeservedNeighour = null;

		while (mostDeservedNeighour == null) {

			if (round(nextPointDistance, ROUNDING_SCALE) == 0)
				throw new RuntimeException("Arc interpolation tends to infinite loop!!");

			// all possible neighbor
			List<Coordinate> allNeighbours = getNeighbouringCoordinates(current, nextPointDistance);

			// will find deserved neighbor
			for (Coordinate c : allNeighbours) {
				double distance = c.distance(center);

				if (Math.abs(distance - radius) <= MAX_POSITIONAL_ERROR) {

					double neighbourAngle = getAngleBetweenTwoPoints(c, nextArcCoordinate, center);
					if (neighbourAngle < maxAngle) {
						if (mostDeservedNeighour == null) {
							mostDeservedNeighour = c;
							deservedNeighbourAngle = neighbourAngle;
						}
						else {
							if (Math.abs(mostDeservedNeighour.distance(center) - radius) > Math
									.abs(distance - radius)) {
								mostDeservedNeighour = c;
								deservedNeighbourAngle = neighbourAngle;
							}
						}
						// break;
					}
				}
			}
			// half the next point distance
			nextPointDistance /= 2;
		}

		lastDeservedNeighbourAngle = deservedNeighbourAngle;

		// most deserved neighbor coordinate found, now will add nearest grid
		// point of that neighbor coordinate
		return mostDeservedNeighour;
	}

	private List<Coordinate> getNeighbouringCoordinates(Coordinate current,
			double nextPointDistance) {

		List<Coordinate> neighbours = new ArrayList<Coordinate>();

		double sliceDegree = 10;
		int points = (int) (360 / sliceDegree);

		for (int i = 0; i < points; i++) {
			double angle = sliceDegree * i;

			double x = current.x + (nextPointDistance * Math.cos(Math.toRadians(angle)));
			double y = current.y + (nextPointDistance * Math.sin(Math.toRadians(angle)));

			neighbours.add(new Coordinate(x, y));
		}

		return neighbours;
	}

	private static Coordinate calculateCenterPoint(Coordinate[] arcCoordinates) {

		double yDelta_a = arcCoordinates[1].y - arcCoordinates[0].y;
		double xDelta_a = arcCoordinates[1].x - arcCoordinates[0].x;
		double yDelta_b = arcCoordinates[2].y - arcCoordinates[1].y;
		double xDelta_b = arcCoordinates[2].x - arcCoordinates[1].x;

		double aSlope = yDelta_a / xDelta_a;

		double bSlope = yDelta_b / xDelta_b;

		// we can get two lines using above slope let's say p1p2: (ya =
		// m_a(x1-x0) + y0) and p2p3: (yb = m_a(x2-x1) + y1)

		Coordinate AB_Mid = new Coordinate((arcCoordinates[0].x + arcCoordinates[1].x) / 2,
				(arcCoordinates[0].y + arcCoordinates[1].y) / 2);
		Coordinate BC_Mid = new Coordinate((arcCoordinates[1].x + arcCoordinates[2].x) / 2,
				(arcCoordinates[1].y + arcCoordinates[2].y) / 2);

		// The center of the circle is the intersection of the two lines
		// perpendicular to and passing through the midpoints of the lines p1p2
		// and p2p3.

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

	private boolean areOnStraightLine(Coordinate[] arcCoordinates) {
		double yDelta_a = arcCoordinates[1].y - arcCoordinates[0].y;
		double xDelta_a = arcCoordinates[1].x - arcCoordinates[0].x;
		double yDelta_b = arcCoordinates[2].y - arcCoordinates[1].y;
		double xDelta_b = arcCoordinates[2].x - arcCoordinates[1].x;

		double aSlope = yDelta_a / xDelta_a;
		double bSlope = yDelta_b / xDelta_b;

		if (round(aSlope, 4) == round(bSlope, 4)) {
			return true;
		}
		return false;
	}

	private double getAngleBetweenTwoPoints(Coordinate c1, Coordinate c2, Coordinate center) {
		double c1Angle = Math.atan2(c1.y - center.y, c1.x - center.x);
		c1Angle = c1Angle * 360 / (2 * Math.PI);
		double c2Angle = Math.atan2(c2.y - center.y, c2.x - center.x);
		c2Angle = c2Angle * 360 / (2 * Math.PI);

		double angle = c2Angle - c1Angle;

		if (c1Angle > 0) {
			if (c2Angle > 0) {
				if (c2Angle > c1Angle) {
					if (isArcClockWise) {
						angle = 360 - (c2Angle - c1Angle);
					}
				}
				else { // c2Angle < c1Angle
					if (isArcClockWise) {
						angle = c1Angle - c2Angle;
					}
					else {
						angle = 360 - (c1Angle - c2Angle);
					}
				}
			}
			else { // c2Angle <0
				if (isArcClockWise) {
					angle = c1Angle - c2Angle;
				}
				else {
					angle = 360 - (c1Angle - c2Angle);
				}
			}
		}
		else { // c1Angle <0
			if (c2Angle > 0) {
				if (isArcClockWise) {
					angle = 360 - (c2Angle - c1Angle);
				}
				else {
					angle = c2Angle - c1Angle;
				}

			}
			else { // c2Angle <0
				if (c2Angle > c1Angle) {
					if (isArcClockWise) {
						angle = 360 - (c2Angle - c1Angle);
					}
					else {
						angle = c2Angle - c1Angle;
					}
				}
				else { // c2Angle <= c1Angle
					if (isArcClockWise) {
						angle = c1Angle - c2Angle;
					}
					else {
						angle = 360 - (c1Angle - c2Angle);
					}
				}
			}

		}
		return angle;
	}

	/**
	 * Is duplicate coordinate to last deserved coordinate
	 * 
	 * @param currentArcCoordinate current generated arc coordinate
	 * @return true if current coordinate matching the last one
	 */
	private boolean isDuplicateCoordinate(Coordinate currentArcCoordinate) {
		Coordinate gridCoordinate = pointToGrid(currentArcCoordinate);
		if (lastGridCoordinateOfArc != null && gridCoordinate.equals(lastGridCoordinateOfArc))
			return true;
		lastGridCoordinateOfArc = gridCoordinate;
		return false;
	}

	private boolean isNextArcCoordinateNeighbour(Coordinate current) {

		if (current.distance(nextArcCoordinate) <= MAX_POSITIONAL_ERROR)
			return true;
		// || round(lastDeservedNeighbourAngle, 4) <= 0
		return false;
	}

	/**
	 * Calculate Center of the Arc coordinates
	 * 
	 * @param coordinates Arc coordinates
	 * @return true, if order is clockwise else false
	 */
	public static boolean getOrderOfArc(Coordinate[] coordinates) {
		Coordinate centerOfArc = calculateCenterPoint(coordinates);
		return getOrderOfArc(centerOfArc, coordinates);
	}

	/**
	 * get order of arc
	 * 
	 * @param center center coordinate of arc
	 * @param coordinates arc coordinates
	 * @return true, if arc is clockwise else false
	 */
	private static boolean getOrderOfArc(Coordinate center, Coordinate[] coordinates) {
		boolean cw = true;

		double c1Angle = Math.atan2(coordinates[0].y - center.y, coordinates[0].x - center.x);
		c1Angle = c1Angle * 360 / (2 * Math.PI);
		double c2Angle = Math.atan2(coordinates[1].y - center.y, coordinates[1].x - center.x);
		c2Angle = c2Angle * 360 / (2 * Math.PI);
		double c3Angle = Math.atan2(coordinates[2].y - center.y, coordinates[2].x - center.x);
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
}
