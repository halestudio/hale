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

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Interpolation of a geometry. Specifically for Arc
 * 
 * @author Arun
 */
public class ArcInterpolation extends Interpolation<LineString> {

	private static final ALogger log = ALoggerFactory.getLogger(ArcInterpolation.class);

	/**
	 * Constructor
	 * 
	 * @param coordinates raw geometry coordinates
	 * @param maxPositionalError maximum positional error
	 */
	public ArcInterpolation(Coordinate[] coordinates, double maxPositionalError) {
		super(coordinates, maxPositionalError);
	}

	/**
	 * @see eu.esdihumboldt.util.geometry.interpolation.Interpolation#validateRawCoordinates()
	 */
	@Override
	protected boolean validateRawCoordinates() {
		if (rawGeometryCoordinates.length != 3) {
			log.error("Invalid arc geometry. Arc must be represented by 3 points in GML.");
			return false;
		}
		return true;
	}

	/**
	 * @see eu.esdihumboldt.util.geometry.interpolation.Interpolation#interpolatedGeometry()
	 */
	@Override
	protected LineString interpolatedGeometry() {
		// Calculate center of Arc
		Coordinate centerOfArc = calculateCenterPoint(rawGeometryCoordinates);

		// Calculate radius of Arc
		double radius = Math.sqrt(Math.pow((rawGeometryCoordinates[0].x - centerOfArc.x), 2)
				+ Math.pow((rawGeometryCoordinates[0].y - centerOfArc.y), 2));

		// return Line String Geometry
		return interpolateToLineString(rawGeometryCoordinates, centerOfArc, radius);
	}

	private Coordinate calculateCenterPoint(Coordinate[] arcCoordinates) {

		double aSlope = (arcCoordinates[1].y - arcCoordinates[0].y)
				/ (arcCoordinates[1].x - arcCoordinates[0].x);

		double bSlope = (arcCoordinates[2].y - arcCoordinates[1].y)
				/ (arcCoordinates[2].x - arcCoordinates[1].x);

		// we can get two lines using above slope let's say p1p2: (ya =
		// m_a(x1-x0) + y0) and p2p3: (yb = m_a(x2-x1) + y1)

		Coordinate AB_Mid = new Coordinate((arcCoordinates[0].x + arcCoordinates[1].x) / 2,
				(arcCoordinates[0].y + arcCoordinates[1].y) / 2);
		Coordinate BC_Mid = new Coordinate((arcCoordinates[1].x + arcCoordinates[2].x) / 2,
				(arcCoordinates[1].y + arcCoordinates[2].y) / 2);

		// The center of the circle is the intersection of the two lines
		// perpendicular to and passing through the midpoints of the lines p1p2
		// and p2p3.

		double centerX = ((aSlope * bSlope * (BC_Mid.y - AB_Mid.y)) + (aSlope * BC_Mid.x)
				- (bSlope * AB_Mid.x)) / (aSlope - bSlope);

		double centerY = AB_Mid.y - ((centerX - AB_Mid.x) / aSlope);

		return new Coordinate(centerX, centerY);
	}

	private LineString interpolateToLineString(Coordinate[] arcCoordinates, Coordinate center,
			double radius) {

		List<Coordinate> generatedCoordinates = new ArrayList<Coordinate>();

		// Add first Arc coordinate
		generatedCoordinates.add(pointToGrid(arcCoordinates[0]));

		double angle = getAngleBetweenTwoPoints(arcCoordinates[0], arcCoordinates[1], radius);

		// Add coordinates up to next arc coordinates
		generatedCoordinates.addAll(generateLineStringCoordinates(arcCoordinates[0],
				arcCoordinates[1], center, radius, angle, NEXT_COORDINATE_DISTANCE));

		// Add second Arc coordinate
		generatedCoordinates.add(pointToGrid(arcCoordinates[1]));

		angle = getAngleBetweenTwoPoints(arcCoordinates[1], arcCoordinates[2], radius);

		// Add coordinates up to next arc coordinates
		generatedCoordinates.addAll(generateLineStringCoordinates(arcCoordinates[1],
				arcCoordinates[2], center, radius, angle, NEXT_COORDINATE_DISTANCE));

		// Add third Arc coordinate
		generatedCoordinates.add(pointToGrid(arcCoordinates[2]));

		// now, we have all coordinates of line string. So then just create it.
		LineString lineString = null;
		try {
			lineString = new GeometryFactory()
					.createLineString(generatedCoordinates.toArray(new Coordinate[0]));
		} catch (Exception ex) {
			log.error("Error in interpolation of arc", ex);
		}

		return lineString;
	}

	private double getAngleBetweenTwoPoints(Coordinate c1, Coordinate c2, double radius) {
		double dx = c1.x - c2.x;
		double dy = c1.y - c2.y;
		double distance = Math.sqrt(dx * dx + dy * dy);

		double angle = Math
				.toDegrees(Math.acos(1 - (Math.pow(distance, 2) / (2 * Math.pow(radius, 2)))));

		return angle;
	}

	private List<Coordinate> generateLineStringCoordinates(Coordinate current,
			Coordinate nextArcCoordinate, Coordinate center, double radius, double maxAngle,
			double nextPointDistance) {

		List<Coordinate> tempList = new ArrayList<>();

		double deservedNeighbourAngle = 0;
		Coordinate deservedNeighour = null;

		while (deservedNeighour == null) {
			// all possible neighbor
			List<Coordinate> allNeighbours = getNeighbouringCoordinates(current, nextPointDistance);

			// will find deserved neighbor
			for (Coordinate c : allNeighbours) {
				double distance = Math
						.sqrt(Math.pow((c.x - center.x), 2) + Math.pow((c.y - center.y), 2));

				if (Math.abs(distance - radius) <= MAX_POSITIONAL_ERROR) {

					deservedNeighbourAngle = getAngleBetweenTwoPoints(c, nextArcCoordinate, radius);

					if (deservedNeighbourAngle < maxAngle) {
						deservedNeighour = c;
						break;
					}
				}
			}
			// half the next point distance
			nextPointDistance /= 2;
		}

		// we found neighbor coordinate, we add nearest grid point of that arc
		// point
		tempList.add(pointToGrid(deservedNeighour));

		// if we reached to the Arc coordinate then just return list, else call
		// this method again
		if (!isArcCoordinateNeighbour(deservedNeighour, nextArcCoordinate, MAX_POSITIONAL_ERROR)) {
			tempList.addAll(generateLineStringCoordinates(deservedNeighour, nextArcCoordinate,
					center, radius, deservedNeighbourAngle, NEXT_COORDINATE_DISTANCE));
		}

		return tempList;
	}

	private static boolean isArcCoordinateNeighbour(Coordinate current,
			Coordinate nextArcCoordinate, double e) {

		double distance = Math.sqrt(Math.pow((current.x - nextArcCoordinate.x), 2)
				+ Math.pow((current.y - nextArcCoordinate.y), 2));
		if (distance <= e)
			return true;

		return false;
	}

	private static List<Coordinate> getNeighbouringCoordinates(Coordinate current,
			double nextPointDistance) {

		// need to check direction of increment decrement of x and y
		boolean xIncrement = true;
		boolean yIncrement = true;
		boolean xDecrement = true;
		boolean yDecrement = true;

		List<Coordinate> neighbours = new ArrayList<Coordinate>();

		if (xIncrement) {
			neighbours.add(new Coordinate(current.x + nextPointDistance, current.y));
			if (yDecrement) {
				neighbours.add(new Coordinate(current.x + nextPointDistance,
						current.y - nextPointDistance));
			}
			if (yIncrement) {
				neighbours.add(new Coordinate(current.x + nextPointDistance,
						current.y + nextPointDistance));
			}
		}

		if (xDecrement) {
			neighbours.add(new Coordinate(current.x - nextPointDistance, current.y));
			if (yDecrement) {
				neighbours.add(new Coordinate(current.x - nextPointDistance,
						current.y - nextPointDistance));
			}
			if (yIncrement) {
				neighbours.add(new Coordinate(current.x - nextPointDistance,
						current.y + nextPointDistance));
			}
		}

		if (yDecrement)
			neighbours.add(new Coordinate(current.x, current.y - nextPointDistance));

		if (yIncrement)
			neighbours.add(new Coordinate(current.x, current.y + nextPointDistance));

		return neighbours;
	}

}
