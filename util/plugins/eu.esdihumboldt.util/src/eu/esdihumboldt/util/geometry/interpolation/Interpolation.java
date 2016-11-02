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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Interpolation of a geometry. Specifically for Arc
 * 
 * @author Arun
 */
public class Interpolation {

	private static final ALogger log = ALoggerFactory.getLogger(Interpolation.class);

	private static double GRID_SCALE = 0.25;

	/**
	 * Interpolate the arc
	 * 
	 * @param arcGeometryCoordinates arc Coordinates
	 * @param e maximum positional error
	 * @return interpolated geometry in {@link LineString}
	 */
	public static Geometry interpolateArc(Coordinate[] arcGeometryCoordinates, double e) {
		if (arcGeometryCoordinates.length != 3) {
			log.error("Invalid arc geometry. Arc must be represented by 3 points in GML.");
			return null;
		}

		// Calculate center of Arc
		CustomCoordinate centerOfArc = calculateCenterPoint(arcGeometryCoordinates);

		System.out.println("Center: " + centerOfArc);

		// Calculate radius of Arc
		double radius = Math.sqrt(Math.pow((arcGeometryCoordinates[0].x - centerOfArc.x), 2)
				+ Math.pow((arcGeometryCoordinates[0].y - centerOfArc.y), 2));

		System.out.println("radius: " + radius);
		// set Quadrant of Coordinates

		CustomCoordinate[] arcCoordinates = new CustomCoordinate[3];

		for (int i = 0; i <= 2; i++) {
			arcCoordinates[i] = createCoordinateWithQuadrant(arcGeometryCoordinates[i],
					centerOfArc);
			System.out.println("Arc Coordinates: " + arcCoordinates[i]);
		}

		// return Line String Geometry
		return interpolateToLineString(arcCoordinates, centerOfArc, radius, e);
	}

	private static CustomCoordinate calculateCenterPoint(Coordinate[] arcCoordinates) {

		double aSlope = (arcCoordinates[1].y - arcCoordinates[0].y)
				/ (arcCoordinates[1].x - arcCoordinates[0].x);

		double bSlope = (arcCoordinates[2].y - arcCoordinates[1].y)
				/ (arcCoordinates[2].x - arcCoordinates[1].x);

		// we can get two lines using above slope let's say p1p2: (ya =
		// m_a(x1-x0) + y0) and p2p3: (yb = m_a(x2-x1) + y1)

		CustomCoordinate AB_Mid = new CustomCoordinate(
				(arcCoordinates[0].x + arcCoordinates[1].x) / 2,
				(arcCoordinates[0].y + arcCoordinates[1].y) / 2);
		CustomCoordinate BC_Mid = new CustomCoordinate(
				(arcCoordinates[1].x + arcCoordinates[2].x) / 2,
				(arcCoordinates[1].y + arcCoordinates[2].y) / 2);

		// The center of the circle is the intersection of the two lines
		// perpendicular to and passing through the midpoints of the lines p1p2
		// and p2p3.

		double centerX = ((aSlope * bSlope * (BC_Mid.y - AB_Mid.y)) + (aSlope * BC_Mid.x)
				- (bSlope * AB_Mid.x)) / (aSlope - bSlope);

		double centerY = AB_Mid.y - ((centerX - AB_Mid.x) / aSlope);

		return new CustomCoordinate(centerX, centerY, Quadrant.center);
	}

	private static CustomCoordinate createCoordinateWithQuadrant(Coordinate coordinate,
			CustomCoordinate center) {
		Quadrant quad = extractQuadrant(coordinate.x, coordinate.y, center.x, center.y);
		return new CustomCoordinate(coordinate.x, coordinate.y, quad);
	}

	private static Quadrant extractQuadrant(double x, double y, double centerX, double centerY) {

		Quadrant quad = Quadrant.first;

		if (x > centerX && y > centerY)
			quad = Quadrant.first;
		else if (x > centerX && y < centerY)
			quad = Quadrant.fourth;
		else if (x < centerX && y > centerY)
			quad = Quadrant.second;
		else if (x < centerX && y < centerY)
			quad = Quadrant.third;

		return quad;
	}

	private static Geometry interpolateToLineString(CustomCoordinate[] arcCoordinates,
			CustomCoordinate center, double radius, double e) {

		List<CustomCoordinate> generatedCoordinates = new ArrayList<CustomCoordinate>();

		// Add first Arc coordinate
		generatedCoordinates.add(arcCoordinates[0]);

		double angle = getAngleBetweenTwoPoints(arcCoordinates[0], arcCoordinates[1], radius);

		// Add coordinates up to next arc coordinates
		generatedCoordinates.addAll(generateLineStringCoordinates(arcCoordinates[0],
				arcCoordinates[1], center, radius, angle, e, GRID_SCALE));

		// Add second Arc coordinate
		// generatedCoordinates.add(arcCoordinates[1]);

		angle = getAngleBetweenTwoPoints(arcCoordinates[1], arcCoordinates[2], radius);

		// Add coordinates up to next arc coordinates
		generatedCoordinates.addAll(generateLineStringCoordinates(arcCoordinates[1],
				arcCoordinates[2], center, radius, angle, e, GRID_SCALE));

		System.out.println(generatedCoordinates.size());

		Coordinate[] generatedLineStringCoordinates = new Coordinate[generatedCoordinates.size()];
		int i = 0;
		for (CustomCoordinate cust : generatedCoordinates) {
			generatedLineStringCoordinates[i] = cust;
			i++;
		}

		LineString lineString = null;
		try {
			lineString = new GeometryFactory().createLineString(generatedLineStringCoordinates);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return lineString;
	}

	private static double getAngleBetweenTwoPoints(CustomCoordinate c1, CustomCoordinate c2,
			double radius) {
		double dx = c1.x - c2.x;
		double dy = c1.y - c2.y;
		double distance = Math.sqrt(dx * dx + dy * dy);

		double angle = Math
				.toDegrees(Math.acos(1 - (Math.pow(distance, 2) / (2 * Math.pow(radius, 2)))));

		return angle;
	}

	private static List<CustomCoordinate> generateLineStringCoordinates(CustomCoordinate current,
			CustomCoordinate nextArcCoordinate, CustomCoordinate center, double radius,
			double maxAngle, double e, double gridScale) {

		List<CustomCoordinate> tempList = new ArrayList<>();

		double deservedNeighbourAngle = 0;
		CustomCoordinate deservedNeighour = null;

		while (deservedNeighour == null) {
			List<CustomCoordinate> allNeighbours = getNeighbouringCoordinates(current, gridScale);

			for (CustomCoordinate c : allNeighbours) {
				double distance = Math
						.sqrt(Math.pow((c.x - center.x), 2) + Math.pow((c.y - center.y), 2));

				if (Math.abs(distance - radius) <= e) {

					deservedNeighbourAngle = getAngleBetweenTwoPoints(c, nextArcCoordinate, radius);

					if (deservedNeighbourAngle < maxAngle) {
						deservedNeighour = c;
						break;
					}
				}
			}
			// half the grid scale
			gridScale /= 2;
		}

		deservedNeighour.setQuadrant(
				extractQuadrant(deservedNeighour.x, deservedNeighour.y, center.x, center.y));

		System.out.println(deservedNeighour);

		tempList.add(deservedNeighour);

		if (!isArcCoordinateNeighbour(deservedNeighour, nextArcCoordinate, e)) {
			tempList.addAll(generateLineStringCoordinates(deservedNeighour, nextArcCoordinate,
					center, radius, deservedNeighbourAngle, e, GRID_SCALE));
		}

		return tempList;
	}

	private static boolean isArcCoordinateNeighbour(CustomCoordinate current,
			CustomCoordinate nextArcCoordinate, double e) {

		double distance = Math.sqrt(Math.pow((current.x - nextArcCoordinate.x), 2)
				+ Math.pow((current.y - nextArcCoordinate.y), 2));
		if (distance <= e)
			return true;

		return false;
	}

	private static List<CustomCoordinate> getNeighbouringCoordinates(CustomCoordinate current,
			double gridScale) {

		// need to check direction of increment decrement of x and y
		boolean xIncrement = true;
		boolean yIncrement = true;
		boolean xDecrement = true;
		boolean yDecrement = true;

		List<CustomCoordinate> neighbours = new ArrayList<CustomCoordinate>();

		if (xIncrement) {
			neighbours.add(new CustomCoordinate(current.x + gridScale, current.y));
			if (yDecrement) {
				neighbours.add(new CustomCoordinate(current.x + gridScale, current.y - gridScale));
			}
			if (yIncrement) {
				neighbours.add(new CustomCoordinate(current.x + gridScale, current.y + gridScale));
			}

		}
		if (xDecrement) {
			neighbours.add(new CustomCoordinate(current.x - gridScale, current.y));
			if (yDecrement) {
				neighbours.add(new CustomCoordinate(current.x - gridScale, current.y - gridScale));
			}
			if (yIncrement) {
				neighbours.add(new CustomCoordinate(current.x - gridScale, current.y + gridScale));
			}

		}

		if (yDecrement)
			neighbours.add(new CustomCoordinate(current.x, current.y - gridScale));

		if (yIncrement)
			neighbours.add(new CustomCoordinate(current.x, current.y + gridScale));

		return neighbours;
	}

}
