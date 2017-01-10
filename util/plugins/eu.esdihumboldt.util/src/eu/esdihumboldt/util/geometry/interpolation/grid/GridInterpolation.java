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

import static eu.esdihumboldt.util.geometry.interpolation.InterpolationUtil.round;
import static eu.esdihumboldt.util.geometry.interpolation.grid.GridUtil.movePointToGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.util.geometry.interpolation.AbstractInterpolationAlgorithm;
import eu.esdihumboldt.util.geometry.interpolation.InterpolationUtil;
import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;

/**
 * Grid based interpolation algorithm.
 * 
 * @author Arun Verma
 * @author Simon Templer
 */
public class GridInterpolation extends AbstractInterpolationAlgorithm {

	private static final ALogger log = ALoggerFactory.getLogger(GridInterpolation.class);

	private final boolean moveAllToGrid = false;

	private double maxPositionalError;

	// XXX stateful variables

	private Coordinate nextArcCoordinate;

	private Coordinate lastGridCoordinateOfArc;

	private double nextCoordinateDistance;

	private double lastDeservedNeighbourAngle;

	@Override
	public void configure(GeometryFactory factory, double maxPositionalError,
			Map<String, String> properties) {
		super.configure(factory, maxPositionalError, properties);

		this.maxPositionalError = maxPositionalError;

		// FIXME handle setting for moving all coordinates to grid

		// FIXME
		this.nextCoordinateDistance = round(4 * maxPositionalError, 6);
	}

	@Override
	public Geometry interpolateArc(Arc arc) {
		if (InterpolationUtil.isStraightLine(arc)) {
			// this happens when slopes are close to equal

			ArcByPoints byPoints = arc.toArcByPoints();
			if (moveAllToGrid) {
				// TODO move to grid
				return createLineString(new Coordinate[] { pointToGrid(byPoints.getStartPoint()),
						pointToGrid(byPoints.getMiddlePoint()),
						pointToGrid(byPoints.getEndPoint()) }, arc);
			}
			else {
				// return points as-is
				return createLineString(new Coordinate[] { byPoints.getStartPoint(),
						byPoints.getMiddlePoint(), byPoints.getEndPoint() }, arc);
			}
		}

		return interpolateToLineString(arc);
	}

	private LineString interpolateToLineString(Arc arc) {
		ArcByPoints byPoints = arc.toArcByPoints();

		List<Coordinate> generatedCoordinates = new ArrayList<Coordinate>();

		// Add first Arc coordinate
		generatedCoordinates
				.add(moveAllToGrid ? movePointToGrid(byPoints.getStartPoint(), maxPositionalError)
						: byPoints.getStartPoint());

		// FIXME not sure if this handling is working correctly
		Coordinate[] arcCoordinates = new Coordinate[] { byPoints.getStartPoint(),
				byPoints.getMiddlePoint(), byPoints.getEndPoint() };

		int current;
		int next;
		for (current = 0; current < arcCoordinates.length - 1; current++) {
			next = current + 1;

			nextArcCoordinate = arcCoordinates[next];

			// Add coordinates up to next arc coordinates
			try {
				generatedCoordinates.addAll(generateLineStringCoordinates(arc,
						arcCoordinates[current], arcCoordinates[next]));
			} catch (Exception ex) {
				StringBuilder builder = new StringBuilder();
				for (Coordinate c : arcCoordinates) {
					builder.append(c.toString() + ",");
				}
				throw new RuntimeException(
						ex.toString() + " for " + (arc.isCircle() ? "circle" : "arc")
								+ " with coordinates " + builder.toString());
			}

			// Add next Arc coordinate
			if (moveAllToGrid) {
				if (!isDuplicateCoordinate(arcCoordinates[next])) {
					generatedCoordinates.add(lastGridCoordinateOfArc);
				}
			}
			else {
				if (lastGridCoordinateOfArc == null
						|| !arcCoordinates[next].equals(lastGridCoordinateOfArc)) {
					generatedCoordinates.add(arcCoordinates[next]);
				}
			}
		}

		// now, we have all coordinates of line string. So then just create it.
		LineString lineString = null;
		try {
			lineString = createLineString(
					generatedCoordinates.toArray(new Coordinate[generatedCoordinates.size()]), arc);
		} catch (Exception ex) {
			log.error("Error creating LineString from interpolated coordinates of arc", ex);
			if (!arc.isCircle()) {
				lineString = createLineString(arcCoordinates, arc);
			}
		}

		return lineString;
	}

	/**
	 * relocate relocate geometry coordinate to the nearest universal grid point
	 * 
	 * @param coordinate the geometry coordinates
	 * @return relocates grid coordinate
	 */
	protected Coordinate pointToGrid(Coordinate coordinate) {
		return movePointToGrid(coordinate, maxPositionalError);
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

		if (current.distance(nextArcCoordinate) <= maxPositionalError)
			return true;
		// || round(lastDeservedNeighbourAngle, 4) <= 0
		return false;
	}

	private List<Coordinate> generateLineStringCoordinates(Arc arc, Coordinate c1, Coordinate c2)
			throws Exception {
		ArcByCenterPoint byCenter = arc.toArcByCenterPoint();

		List<Coordinate> list = new ArrayList<>();

		// calculate angle between given two coordinates
		double angle = getAngleBetweenTwoPoints(arc, c1, c2, byCenter.getCenterPoint());
		Coordinate deservedNeighbour = c1;
		double lastDeservedNeighbourAngle = angle;

		while (!isNextArcCoordinateNeighbour(deservedNeighbour)) {
			deservedNeighbour = generateNextLineStringCoordinate(arc, deservedNeighbour,
					nextArcCoordinate, byCenter.getCenterPoint(), byCenter.getRadius(),
					lastDeservedNeighbourAngle, nextCoordinateDistance);
			if (!isDuplicateCoordinate(deservedNeighbour))
				list.add(lastGridCoordinateOfArc);

		}
		return list;
	}

	private Coordinate generateNextLineStringCoordinate(Arc arc, Coordinate current,
			Coordinate nextArcCoordinate, Coordinate center, double radius, double maxAngle,
			double nextPointDistance) {
		double deservedNeighbourAngle = 0;

		Coordinate mostDeservedNeighour = null;

		while (mostDeservedNeighour == null) {

			if (round(nextPointDistance, 6) == 0)
				throw new RuntimeException("Arc interpolation tends to infinite loop!!");

			// all possible neighbor
			List<Coordinate> allNeighbours = getNeighbouringCoordinates(current, nextPointDistance);

			// will find deserved neighbor
			for (Coordinate c : allNeighbours) {
				double distance = c.distance(center);

				if (Math.abs(distance - radius) <= maxPositionalError) {

					double neighbourAngle = getAngleBetweenTwoPoints(arc, c, nextArcCoordinate,
							center);
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

	private double getAngleBetweenTwoPoints(Arc arc, Coordinate c1, Coordinate c2,
			Coordinate center) {
		double c1Angle = Math.atan2(c1.y - center.y, c1.x - center.x);
		c1Angle = c1Angle * 360 / (2 * Math.PI);
		double c2Angle = Math.atan2(c2.y - center.y, c2.x - center.x);
		c2Angle = c2Angle * 360 / (2 * Math.PI);

		double angle = c2Angle - c1Angle;

		if (c1Angle > 0) {
			if (c2Angle > 0) {
				if (c2Angle > c1Angle) {
					if (arc.toArcByCenterPoint().isClockwise()) {
						angle = 360 - (c2Angle - c1Angle);
					}
				}
				else { // c2Angle < c1Angle
					if (arc.toArcByCenterPoint().isClockwise()) {
						angle = c1Angle - c2Angle;
					}
					else {
						angle = 360 - (c1Angle - c2Angle);
					}
				}
			}
			else { // c2Angle <0
				if (arc.toArcByCenterPoint().isClockwise()) {
					angle = c1Angle - c2Angle;
				}
				else {
					angle = 360 - (c1Angle - c2Angle);
				}
			}
		}
		else { // c1Angle <0
			if (c2Angle > 0) {
				if (arc.toArcByCenterPoint().isClockwise()) {
					angle = 360 - (c2Angle - c1Angle);
				}
				else {
					angle = c2Angle - c1Angle;
				}

			}
			else { // c2Angle <0
				if (c2Angle > c1Angle) {
					if (arc.toArcByCenterPoint().isClockwise()) {
						angle = 360 - (c2Angle - c1Angle);
					}
					else {
						angle = c2Angle - c1Angle;
					}
				}
				else { // c2Angle <= c1Angle
					if (arc.toArcByCenterPoint().isClockwise()) {
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

}
