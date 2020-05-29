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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import org.locationtech.jts.geom.Coordinate;

import eu.esdihumboldt.util.geometry.interpolation.AbstractArcTest;
import eu.esdihumboldt.util.geometry.interpolation.InterpolationUtil;
import eu.esdihumboldt.util.geometry.interpolation.model.Angle;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Tests for Arc represented by three points.
 * 
 * @author Simon Templer
 */
@Features("Geometries")
@Stories("Arcs")
@SuppressWarnings("javadoc")
public class ArcByPointsImplTest extends AbstractArcTest {

	@Test
	public void testOrigin() throws IOException {
		ArcByPoints arc = new ArcByPointsImpl(new Coordinate(-1, 0), new Coordinate(0, 1),
				new Coordinate(1, 0));

		drawArcWithMarkers(arc);

		assertFalse(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByCenterPoint converted = arc.toArcByCenterPoint();
		assertEqualsCoord(new Coordinate(0, 0), converted.getCenterPoint());
		assertEquals(1.0, converted.getRadius(), 1e-10);
		assertEquals(Angle.fromDegrees(180), converted.getStartAngle());
		assertEquals(Angle.fromDegrees(0), converted.getEndAngle());
		assertEquals(Angle.fromDegrees(-180), converted.getAngleBetween());
		assertTrue(converted.isClockwise());
	}

	@Test
	public void testNotOrigin() throws IOException {
		ArcByPoints arc = new ArcByPointsImpl(new Coordinate(-2, 1), new Coordinate(-1, 2),
				new Coordinate(0, 1));

		drawArcWithMarkers(arc);

		assertFalse(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByCenterPoint converted = arc.toArcByCenterPoint();
		assertEqualsCoord(new Coordinate(-1, 1), converted.getCenterPoint());
		assertEquals(1.0, converted.getRadius(), 1e-10);
		assertEquals(Angle.fromDegrees(180), converted.getStartAngle());
		assertEquals(Angle.fromDegrees(0), converted.getEndAngle());
		assertEquals(Angle.fromDegrees(-180), converted.getAngleBetween());
		assertTrue(converted.isClockwise());
	}

	@Test
	public void testLargeArc() throws IOException {
		ArcByPoints arc = new ArcByPointsImpl(new Coordinate(1, 1),
				new Coordinate(0, -Math.sqrt(2.0)), new Coordinate(-1, 1));

		drawArcWithMarkers(arc);

		assertFalse(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByCenterPoint converted = arc.toArcByCenterPoint();
		assertEqualsCoord(new Coordinate(0, 0), converted.getCenterPoint());
		assertEquals(Math.sqrt(2.0), converted.getRadius(), 1e-10);
		assertEquals(Angle.fromDegrees(45), converted.getStartAngle());
		assertEquals(Angle.fromDegrees(135), converted.getEndAngle());
		assertEquals(Angle.fromDegrees(-270), converted.getAngleBetween());
		assertTrue(converted.isClockwise());
	}

	@Test
	public void testCCW() throws IOException {
		ArcByPoints arc = new ArcByPointsImpl(new Coordinate(1, 0), new Coordinate(0, 1),
				new Coordinate(-1, 0));

		drawArcWithMarkers(arc);

		assertFalse(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByCenterPoint converted = arc.toArcByCenterPoint();
		assertEqualsCoord(new Coordinate(0, 0), converted.getCenterPoint());
		assertEquals(1.0, converted.getRadius(), 1e-10);
		assertEquals(Angle.fromDegrees(0), converted.getStartAngle());
		assertEquals(Angle.fromDegrees(180), converted.getEndAngle());
		assertEquals(Angle.fromDegrees(180), converted.getAngleBetween());
		assertFalse(converted.isClockwise());
	}

	@Test
	public void testStraight() throws IOException {
		ArcByPoints arc = new ArcByPointsImpl(new Coordinate(-10, 1), new Coordinate(0, 1.0001),
				new Coordinate(10, 1));

		drawArcWithMarkers(arc);

		assertFalse(arc.isCircle());
		assertTrue(InterpolationUtil.isStraightLine(arc));

		ArcByCenterPoint converted = arc.toArcByCenterPoint();

		assertTrue(converted.isClockwise());
	}

	@Test
	public void testCircle() throws IOException {
		ArcByPoints arc = new ArcByPointsImpl(new Coordinate(-1, 0), new Coordinate(1, 0),
				new Coordinate(-1, 0));

		drawArcWithMarkers(arc);

		assertTrue(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByCenterPoint converted = arc.toArcByCenterPoint();
		assertEqualsCoord(new Coordinate(0, 0), converted.getCenterPoint());
		assertEquals(1.0, converted.getRadius(), 1e-10);
		assertEquals(Angle.fromDegrees(180), converted.getStartAngle());
		assertEquals(Angle.fromDegrees(180), converted.getEndAngle());
		assertEquals(Angle.fromDegrees(-360), converted.getAngleBetween());
		assertTrue(converted.isClockwise());
	}

}
