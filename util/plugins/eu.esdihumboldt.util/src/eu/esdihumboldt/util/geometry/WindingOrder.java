/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.util.geometry;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Winding order changer for Geometry objects.
 * 
 * @author Arun
 */
public class WindingOrder {

	private static final GeometryFactory factory = new GeometryFactory();

	/**
	 * To determine axis of given CRS is flip or not
	 * 
	 * @param crs the CRS
	 * @return true, if CRS is flip, else false
	 */
	public static boolean isCRSFlip(CoordinateReferenceSystem crs) {
		// get number of dimensions and it should be two
		if (crs.getCoordinateSystem().getDimension() == 2) {
			AxisDirection axis1 = crs.getCoordinateSystem().getAxis(0).getDirection();
			AxisDirection axis2 = crs.getCoordinateSystem().getAxis(1).getDirection();

			// check if direction is flip?
			if (axis1.equals(AxisDirection.EAST) || axis1.equals(AxisDirection.WEST)) {
				if (axis1.equals(AxisDirection.EAST) && (axis2.equals(AxisDirection.SOUTH)
						|| axis2.equals(AxisDirection.DOWN))) {
					return true;
				}
				else if (axis1.equals(AxisDirection.WEST)
						&& (axis2.equals(AxisDirection.NORTH) || axis2.equals(AxisDirection.UP))) {
					return true;
				}
			}
			else { // Check if order of axis flip?
				if ((axis1.equals(AxisDirection.SOUTH) || axis1.equals(AxisDirection.DOWN))
						&& axis2.equals(AxisDirection.WEST)) {
					return true;
				}
				else if ((axis1.equals(AxisDirection.NORTH) || axis1.equals(AxisDirection.UP))
						&& axis2.equals(AxisDirection.EAST)) {
					return true;
				}
			}
		}
		// if not any case return false above.
		return false;
	}

	/**
	 * Identifying order of Geometry object CounterClockwise or not?
	 * 
	 * @param geometry Geometry object to identify order.
	 * @return boolean value, true, if order is counterclockwise, else false if
	 *         order is clockwise.
	 */
	public static boolean isCounterClockwise(Geometry geometry) {
		if (geometry == null)
			return false;

		// Get coordinates of geometry
		Coordinate[] coordinates = geometry.getCoordinates();

		// Get order of geometry using algorithm
		boolean orientation = false;
		orientation = CGAlgorithms.isCCW(coordinates);
		return orientation;
	}

	/**
	 * Unify Winding order of Geometry object( including Polygon, Multipolygon,
	 * LinearRing) as CounterClockwise or Clockwise.
	 * 
	 * @param geometry Geometry object for unifying.
	 * @param counterClockWise true, if unify geometry counterClockwise else
	 *            false.
	 * @param crs Coordinate Reference System
	 * @return Geometry unified object.
	 */
	public static Geometry unifyWindingOrder(Geometry geometry, boolean counterClockWise,
			CoordinateReferenceSystem crs) {

		if (geometry == null) {
			return null;
		}
		// Getting isCRS flip? if yes, then reverse the winding order of input
		if (crs != null && isCRSFlip(crs))
			counterClockWise = !counterClockWise;

		if (geometry instanceof MultiPolygon) {
			return unifyWindingOrderForMultiPolygon((MultiPolygon) geometry, counterClockWise);
		}
		else if (geometry instanceof Polygon) {
			return unifyWindingOrderForPolyGon((Polygon) geometry, counterClockWise);
		}
		else if (geometry instanceof LinearRing) {
			return unifyWindingOrderForLinearRing((LinearRing) geometry, counterClockWise);
		}
		else if (GeometryCollection.class.equals(geometry.getClass())) {
			return unifyWindingOrderForGeometryCollection((GeometryCollection) geometry,
					counterClockWise);
		}
		else
			return geometry;
	}

	/**
	 * Unify order for LinearRing as CounterClockwise or Clockwise.
	 * 
	 * @param linearRing LinearRing object for unifying
	 * @param counterClockWise boolean value. true, if want geometry as counter
	 *            clock wise, else false.
	 * @return LinearRing unified object.
	 */
	public static LinearRing unifyWindingOrderForLinearRing(LinearRing linearRing,
			boolean counterClockWise) {

		// Checking and reversing geometry
		if (isCounterClockwise(linearRing) == counterClockWise)
			return linearRing;
		else
			return (LinearRing) linearRing.reverse();
	}

	/**
	 * Unify order of the polygon as counterClockwise or not including all its
	 * holes.
	 * 
	 * @param poly Polygon object for unifying
	 * @param counterClockWise boolean value. true, if want shell of Polygon as
	 *            counter clock wise and holes as clockwise, else false.
	 * @return Polygon unified object.
	 */
	public static Polygon unifyWindingOrderForPolyGon(Polygon poly, boolean counterClockWise) {

		// Checking and reversing Shell
		LinearRing shell = unifyWindingOrderForLinearRing((LinearRing) poly.getExteriorRing(),
				counterClockWise);

		Polygon revPoly;

		// Checking and reversing Holes
		if (poly.getNumInteriorRing() > 0) {
			LinearRing holes[] = new LinearRing[poly.getNumInteriorRing()];
			for (int i = 0; i < poly.getNumInteriorRing(); i++) {
				holes[i] = unifyWindingOrderForLinearRing((LinearRing) poly.getInteriorRingN(i),
						!counterClockWise);
			}

			// Create New Polygon using unified shell and holes both.
			revPoly = factory.createPolygon(shell, holes);
		}
		else
			// Create New Polygon using unified shell only
			revPoly = factory.createPolygon(shell);

		return revPoly;
	}

	/**
	 * Unify order of the Multipolygon including all polygons and holes in it.
	 * 
	 * @param multiPoly Multipolygon object for unifying it.
	 * @param counterClockWise boolean value. true, if want shell of
	 *            multipolygon as counter clock wise and holes as clockwise,
	 *            else false.
	 * @return Multipolygon unified Object
	 */
	public static MultiPolygon unifyWindingOrderForMultiPolygon(MultiPolygon multiPoly,
			boolean counterClockWise) {

		// get no of polygons in MultiPolygon
		int noOfPolygons = multiPoly.getNumGeometries();

		Polygon[] revGeoms = new Polygon[noOfPolygons];
		// Unify each inner polygon one by one
		for (int i = 0; i < noOfPolygons; i++) {
			revGeoms[i] = unifyWindingOrderForPolyGon((Polygon) multiPoly.getGeometryN(i),
					counterClockWise);
		}
		// new multipolygon of unified polygons
		return factory.createMultiPolygon(revGeoms);
	}

	/**
	 * Unify order of the GeometryCollection including all Geometries in it.
	 * 
	 * @param geoCollection GeometryCollection object for unifying.
	 * @param counterClockWise boolean value. true, if want all geometry object
	 *            * as counter clock wise, else false.
	 * @return GeometryCollection unified Object
	 */
	public static GeometryCollection unifyWindingOrderForGeometryCollection(
			GeometryCollection geoCollection, boolean counterClockWise) {

		// get no of geometries in GeometryCollection
		int noOfGeoms = geoCollection.getNumGeometries();

		Geometry[] revGeoms = new Geometry[noOfGeoms];
		// Unify each geometry one by one
		for (int i = 0; i < noOfGeoms; i++) {
			revGeoms[i] = unifyWindingOrder(geoCollection.getGeometryN(i), counterClockWise, null);
		}
		// new geolmetry collection object of unified geometry objects.
		return factory.createGeometryCollection(revGeoms);
	}

}
