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

package eu.esdihumboldt.cst.functions.geometric.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.cst.functions.geometric.GeometryHelperFunctions;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Tests for geometry helper functions
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public class GeometryHelperFunctionsTest {

	private final GeometryFactory factory = new GeometryFactory();

	private final Random random = new Random();

	// Tests

	@Test
	public void testFindPolygons() {
		List<Geometry> geoms = new ArrayList<>();
		geoms.add(createPolygon());
		geoms.add(createPoint());
		geoms.add(createMultiLineString());
		geoms.add(createPolygon());
		geoms.add(createMultiPolygon());
		geoms.add(createLineString());
		geoms.add(createMultiPoint());

		Collection<GeometryProperty<?>> list = GeometryHelperFunctions._findPolygons(geoms);
		assertEquals(3, list.size());
	}

	@Test
	public void testFindLines() {
		List<Geometry> geoms = new ArrayList<>();
		geoms.add(createPolygon());
		geoms.add(createPoint());
		geoms.add(createMultiLineString());
		geoms.add(createPolygon());
		geoms.add(createMultiPolygon());
		geoms.add(createLineString());
		geoms.add(createMultiPoint());

		Collection<GeometryProperty<?>> list = GeometryHelperFunctions._findLines(geoms);
		assertEquals(2, list.size());
	}

	@Test
	public void testFindPoints() {
		List<Geometry> geoms = new ArrayList<>();
		geoms.add(createPolygon());
		geoms.add(createPoint());
		geoms.add(createMultiLineString());
		geoms.add(createPoint());
		geoms.add(createPolygon());
		geoms.add(createMultiPolygon());
		geoms.add(createLineString());
		geoms.add(createMultiPoint());
		geoms.add(createPoint());

		Collection<GeometryProperty<?>> list = GeometryHelperFunctions._findPoints(geoms);
		assertEquals(4, list.size());
	}

	// Helpers

	protected Coordinate createCoordinate() {
		return new Coordinate(random.nextDouble(), random.nextDouble());
	}

	protected Coordinate[] createCoordinates(boolean startIsEnd) {
		int num = random.nextInt(6) + 4;
		Coordinate[] result = new Coordinate[num];
		for (int i = 0; i < num; i++) {
			if (i == num - 1 && startIsEnd) {
				result[i] = result[0];
			}
			else {
				result[i] = createCoordinate();
			}
		}
		return result;
	}

	protected Polygon createPolygon() {
		return factory.createPolygon(createCoordinates(true));
	}

	protected MultiPolygon createMultiPolygon() {
		int num = random.nextInt(3) + 1;
		Polygon[] polygons = new Polygon[num];
		for (int i = 0; i < num; i++) {
			polygons[i] = createPolygon();
		}
		return factory.createMultiPolygon(polygons);
	}

	protected LineString createLineString() {
		return factory.createLineString(createCoordinates(false));
	}

	protected MultiLineString createMultiLineString() {
		int num = random.nextInt(3) + 1;
		LineString[] lines = new LineString[num];
		for (int i = 0; i < num; i++) {
			lines[i] = createLineString();
		}
		return factory.createMultiLineString(lines);
	}

	protected Point createPoint() {
		return factory.createPoint(createCoordinate());
	}

	protected MultiPoint createMultiPoint() {
		int num = random.nextInt(3) + 1;
		Point[] point = new Point[num];
		for (int i = 0; i < num; i++) {
			point[i] = createPoint();
		}
		return factory.createMultiPoint(point);
	}

}
