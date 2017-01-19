/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.geometry.handler.compositeGeometries;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.hale.common.instance.geometry.InterpolationHelper;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.InterpolationConfigurations;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.ReaderConfiguration;
import eu.esdihumboldt.util.geometry.interpolation.model.Angle;
import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;
import eu.esdihumboldt.util.geometry.interpolation.model.impl.ArcByCenterPointImpl;
import eu.esdihumboldt.util.geometry.interpolation.model.impl.ArcByPointsImpl;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for reading curve geometries
 * 
 * @author Patrick Lieb
 * @author Arun Varma
 * @author Simon Templer
 */
@Features("Geometries")
@Stories("GML")
public class CurveGeometryTest extends AbstractHandlerTest {

	private LineString lineStringReference;
	private Consumer<Geometry> lineStringChecker;
	private Consumer<Geometry> lineStringGridChecker;

	private final double maxPositionalError = InterpolationHelper.DEFAULT_MAX_POSITION_ERROR;
	private final ReaderConfiguration gridConfig = InterpolationConfigurations
			.grid(maxPositionalError, true);

	private final ArcByPoints arcByPoints = new ArcByPointsImpl(new Coordinate(8, 8),
			new Coordinate(12, 6.5), new Coordinate(16, 8));
	private final ArcByCenterPoint arcByCenter = new ArcByCenterPointImpl(new Coordinate(0, 0), 5.0,
			Angle.fromDegrees(30), Angle.fromDegrees(60), false);
	private Arc circleByPoints;
	private final Arc circleByCenter = new ArcByCenterPointImpl(new Coordinate(0, 0), 5.0,
			Angle.fromDegrees(0), Angle.fromDegrees(0), false);

	// XXX different segments need different count of coordinates
	// XXX missing Clothoid handler

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(0.01, 3.2),
				new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2) };
		lineStringReference = geomFactory.createLineString(coordinates);

		lineStringChecker = combine(noCoordinatePairs(), referenceChecker(lineStringReference));

		lineStringGridChecker = combine(
				referenceChecker(lineStringReference,
						InterpolationHelper.DEFAULT_MAX_POSITION_ERROR),
				gridConfig.geometryChecker());

		ArcByCenterPoint cbp = new ArcByPointsImpl(new Coordinate(0.01, 3.2),
				new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2)).toArcByCenterPoint();
		circleByPoints = new ArcByCenterPointImpl(cbp.getCenterPoint(), cbp.getRadius(),
				Angle.fromDegrees(0), Angle.fromDegrees(0), false);
	}

	/**
	 * Create a geometry checker for interpolated arcs.
	 * 
	 * @param arc the reference arc to check
	 * @param maxPositionalError the maximum positional error
	 * @return the geometry checker
	 */
	protected Consumer<Geometry> arcChecker(Arc arc, final double maxPositionalError) {
		return (geom) -> {
			// test interpolated geometry
			Coordinate[] coords = geom.getCoordinates();
			for (int i = 0; i < coords.length; i++) {
				Coordinate c = coords[i];

				// check if two coordinates are not the same
				if (i < coords.length - 1) {
					Coordinate c2 = coords[i + 1];

					assertNotEquals("Subsequent coordinates are equal", c, c2);
				}

				// check distance from center
				double distance = arc.toArcByCenterPoint().getCenterPoint().distance(c);
				double delta = Math.abs(distance - arc.toArcByCenterPoint().getRadius());
				assertTrue(delta <= maxPositionalError);

				if (!arc.isCircle()) {
					// TODO also check the angle?
				}
			}
		};
	}

	/**
	 * Test curve geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCurveGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/curve/sample-curve-gml3.xml").toURI());

		// eleven instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, lineStringChecker);

			// 2. segments with Arc defined through coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			// Arc handler added
			checkSingleGeometry(instance, arcChecker(arcByPoints, maxPositionalError));

			// 3. segments with ArcByBulge defined through coordinates
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not a accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 4. segments with ArcByCenterPoint defined through coordinates
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(arcByCenter, maxPositionalError));

			// 5. segments with ArcString defined through coordinates
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			// TODO more extensive check?
			checkSingleGeometry(instance, noCoordinatePairs());

			// 6. segments with ArcStringByBulge defined through coordinates
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 7. segments with Bezier defined through coordinates
			assertTrue("Seventh sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 8. segments with BSpline defined through coordinates
			assertTrue("Eigth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 9. segments with Circle defined through coordinates
			assertTrue("Nineth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(circleByPoints, maxPositionalError));

			// 10. segments with CircleByCenterPoint defined through coordinates
			assertTrue("Tenth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(circleByCenter, maxPositionalError));

			// 11. segments with CubicSpline defined through coordinates
			assertTrue("Eleventh sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test curve geometries read from a GML 3.1 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCurveGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/curve/sample-curve-gml31.xml").toURI());

		// twelve instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, lineStringChecker);

			// 2. segments with LineStringSegment defined through posList
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, lineStringChecker);

			// 3. segments with Arc defined through coordinates
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			// Arc handler added
			checkSingleGeometry(instance, arcChecker(arcByPoints, maxPositionalError));

			// 4. segments with ArcByBulge defined through coordinates
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 5. segments with ArcByCenterPoint defined through coordinates
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(arcByCenter, maxPositionalError));

			// 6. segments with ArcString defined through coordinates
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			// TODO more extensive check?
			checkSingleGeometry(instance, noCoordinatePairs());

			// 7. segments with ArcStringByBulge defined through coordinates
			assertTrue("Seventh sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 8. segments with Bezier defined through coordinates
			assertTrue("Eigth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 9. segments with BSpline defined through coordinates
			assertTrue("Nineth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 10. segments with Circle defined through coordinates
			assertTrue("Tenth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(circleByPoints, maxPositionalError));

			// 11. segments with CircleByCenterPoint defined through coordinates
			assertTrue("Eleventh sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(circleByCenter, maxPositionalError));

			// 12. segments with CubicSpline defined through coordinates
			assertTrue("Twelveth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 13. segments with Geodesic defined through posList
			assertTrue("Thirteenth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 14. segments with GeodesicString defined through posList
			assertTrue("Fourteenth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test curve geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCurveGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/curve/sample-curve-gml32.xml").toURI());

		// twelve instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, lineStringChecker);

			// 2. segments with LineStringSegment defined through posList
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, lineStringChecker);

			// 3. segments with Arc defined through coordinates
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			// Arc handler added
			checkSingleGeometry(instance, arcChecker(arcByPoints, maxPositionalError));

			// 4. segments with ArcByBulge defined through coordinates
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 5. segments with ArcByCenterPoint defined through coordinates
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(arcByCenter, maxPositionalError));

			// 6. segments with ArcString defined through coordinates
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			// TODO more extensive check?
			checkSingleGeometry(instance, noCoordinatePairs());

			// 7. segments with ArcStringByBulge defined through coordinates
			assertTrue("Seventh sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 8. segments with Bezier defined through coordinates
			assertTrue("Eigth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 9. segments with BSpline defined through coordinates
			assertTrue("Nineth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 10. segments with Circle defined through coordinates
			assertTrue("Tenth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(circleByPoints, maxPositionalError));

			// 11. segments with CircleByCenterPoint defined through coordinates
			assertTrue("Eleventh sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(circleByCenter, maxPositionalError));

			// 12. segments with CubicSpline defined through coordinates
			assertTrue("Twelveth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 13. segments with Geodesic defined through posList
			assertTrue("Thirteenth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);

			// 14. segments with GeodesicString defined through posList
			assertTrue("Fourteenth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringChecker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test curve geometries read from a GML 3.2 file. Geometry coordinates will
	 * be moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCurveGml32_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/curve/sample-curve-gml32.xml").toURI(), gridConfig);

		// twelve instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, lineStringGridChecker);

			// 2. segments with LineStringSegment defined through posList
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, lineStringGridChecker);

			// 3. segments with Arc defined through coordinates
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			// Arc handler added
			checkSingleGeometry(instance, arcChecker(arcByPoints, maxPositionalError));

			// 4. segments with ArcByBulge defined through coordinates
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringGridChecker);

			// 5. segments with ArcByCenterPoint defined through coordinates
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(arcByCenter, maxPositionalError));

			// 6. segments with ArcString defined through coordinates
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			// TODO more extensive check?
			checkSingleGeometry(instance, noCoordinatePairs());

			// 7. segments with ArcStringByBulge defined through coordinates
			assertTrue("Seventh sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringGridChecker);

			// 8. segments with Bezier defined through coordinates
			assertTrue("Eigth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringGridChecker);

			// 9. segments with BSpline defined through coordinates
			assertTrue("Nineth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringGridChecker);

			// 10. segments with Circle defined through coordinates
			assertTrue("Tenth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(circleByPoints, maxPositionalError));

			// 11. segments with CircleByCenterPoint defined through coordinates
			assertTrue("Eleventh sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, arcChecker(circleByCenter, maxPositionalError));

			// 12. segments with CubicSpline defined through coordinates
			assertTrue("Twelveth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringGridChecker);

			// 13. segments with Geodesic defined through posList
			assertTrue("Thirteenth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringGridChecker);

			// 14. segments with GeodesicString defined through posList
			assertTrue("Fourteenth sample feature missing", it.hasNext());
			instance = it.next();
			// FIXME this is not an accurate representation
			checkSingleGeometry(instance, lineStringGridChecker);
		} finally {
			it.close();
		}
	}

}
