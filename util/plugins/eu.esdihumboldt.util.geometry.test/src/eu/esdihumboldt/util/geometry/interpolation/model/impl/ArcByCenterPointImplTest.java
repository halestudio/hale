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

import com.vividsolutions.jts.geom.Coordinate;

import eu.esdihumboldt.util.geometry.interpolation.AbstractArcTest;
import eu.esdihumboldt.util.geometry.interpolation.InterpolationUtil;
import eu.esdihumboldt.util.geometry.interpolation.model.Angle;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Tests for Arc represented by a center point.
 * 
 * @author Simon Templer
 */
@Features("Geometries")
@Stories("Arcs")
@SuppressWarnings("javadoc")
public class ArcByCenterPointImplTest extends AbstractArcTest {

	@Test
	public void testCW1() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), 1.0,
				Angle.fromDegrees(0), Angle.fromDegrees(180), true);

		drawArcWithMarkers(arc);

		assertEquals(-180.0, arc.getAngleBetween().getDegrees(), 1e-10);

		assertFalse(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByPoints converted = arc.toArcByPoints();
		assertEqualsCoord(new Coordinate(1, 0), converted.getStartPoint());
		assertEqualsCoord(new Coordinate(0, -1), converted.getMiddlePoint());
		assertEqualsCoord(new Coordinate(-1, 0), converted.getEndPoint());
	}

	@Test
	public void testCW2() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), Math.sqrt(2.0),
				Angle.fromDegrees(45), Angle.fromDegrees(135), true);

		drawArcWithMarkers(arc);

		assertEquals(-270.0, arc.getAngleBetween().getDegrees(), 1e-10);

		assertFalse(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByPoints converted = arc.toArcByPoints();
		assertEqualsCoord(new Coordinate(1, 1), converted.getStartPoint());
		assertEqualsCoord(new Coordinate(0, -Math.sqrt(2.0)), converted.getMiddlePoint());
		assertEqualsCoord(new Coordinate(-1, 1), converted.getEndPoint());
	}

	@Test
	public void testCW3() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(1, 1), Math.sqrt(2.0),
				Angle.fromDegrees(135), Angle.fromDegrees(45), true);

		drawArcWithMarkers(arc);

		assertEquals(-90.0, arc.getAngleBetween().getDegrees(), 1e-10);

		assertFalse(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByPoints converted = arc.toArcByPoints();
		assertEqualsCoord(new Coordinate(0, 2), converted.getStartPoint());
		assertEqualsCoord(new Coordinate(1, 1 + Math.sqrt(2.0)), converted.getMiddlePoint());
		assertEqualsCoord(new Coordinate(2, 2), converted.getEndPoint());
	}

	@Test
	public void testCCW1() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), 1.0,
				Angle.fromDegrees(0), Angle.fromDegrees(180), false);

		drawArcWithMarkers(arc);

		assertEquals(180.0, arc.getAngleBetween().getDegrees(), 1e-10);

		assertFalse(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByPoints converted = arc.toArcByPoints();
		assertEqualsCoord(new Coordinate(1, 0), converted.getStartPoint());
		assertEqualsCoord(new Coordinate(0, 1), converted.getMiddlePoint());
		assertEqualsCoord(new Coordinate(-1, 0), converted.getEndPoint());
	}

	@Test
	public void testCCW2() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), Math.sqrt(2.0),
				Angle.fromDegrees(45), Angle.fromDegrees(135), false);

		drawArcWithMarkers(arc);

		assertEquals(90.0, arc.getAngleBetween().getDegrees(), 1e-10);

		assertFalse(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByPoints converted = arc.toArcByPoints();
		assertEqualsCoord(new Coordinate(1, 1), converted.getStartPoint());
		assertEqualsCoord(new Coordinate(0, Math.sqrt(2.0)), converted.getMiddlePoint());
		assertEqualsCoord(new Coordinate(-1, 1), converted.getEndPoint());
	}

	@Test
	public void testCircle1() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), 1.0,
				Angle.fromDegrees(0), Angle.fromDegrees(0), false);

		drawArcWithMarkers(arc);

		assertEquals(360.0, arc.getAngleBetween().getDegrees(), 1e-10);

		assertTrue(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByPoints converted = arc.toArcByPoints();
		assertEqualsCoord(new Coordinate(1, 0), converted.getStartPoint());
		assertEqualsCoord(new Coordinate(-1, 0), converted.getMiddlePoint());
		assertEqualsCoord(new Coordinate(1, 0), converted.getEndPoint());
	}

	@Test
	public void testCircle2() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), 1.0,
				Angle.fromDegrees(0), Angle.fromDegrees(360), false);

		drawArcWithMarkers(arc);

		assertEquals(360.0, arc.getAngleBetween().getDegrees(), 1e-10);

		assertTrue(arc.isCircle());
		assertFalse(InterpolationUtil.isStraightLine(arc));

		ArcByPoints converted = arc.toArcByPoints();
		assertEqualsCoord(new Coordinate(1, 0), converted.getStartPoint());
		assertEqualsCoord(new Coordinate(-1, 0), converted.getMiddlePoint());
		assertEqualsCoord(new Coordinate(1, 0), converted.getEndPoint());
	}

}
