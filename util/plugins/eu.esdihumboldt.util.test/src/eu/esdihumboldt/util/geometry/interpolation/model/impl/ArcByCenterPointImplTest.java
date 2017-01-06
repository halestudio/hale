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

import java.io.IOException;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

import eu.esdihumboldt.util.geometry.interpolation.AbstractArcTest;
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
	public void testToPointsRepresentation1() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), 1.0,
				Angle.fromDegrees(0), Angle.fromDegrees(180), false);

		drawArcWithMarkers(arc);

		ArcByPoints converted = arc.toArcByPoints();
		assertEquals(new Coordinate(1, 0), converted.getStartPoint());
		assertEquals(new Coordinate(0, -1), converted.getMiddlePoint());
		assertEquals(new Coordinate(-1, 0), converted.getEndPoint());
	}

	@Test
	public void testToPointsRepresentation2() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), 1.0,
				Angle.fromDegrees(0), Angle.fromDegrees(180), true);

		drawArcWithMarkers(arc);

		ArcByPoints converted = arc.toArcByPoints();
		assertEquals(new Coordinate(1, 0), converted.getStartPoint());
		assertEquals(new Coordinate(0, 1), converted.getMiddlePoint());
		assertEquals(new Coordinate(-1, 0), converted.getEndPoint());
	}

}
