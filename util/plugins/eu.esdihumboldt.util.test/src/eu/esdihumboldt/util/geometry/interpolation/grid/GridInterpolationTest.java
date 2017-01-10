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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.util.geometry.interpolation.AbstractArcTest;
import eu.esdihumboldt.util.geometry.interpolation.model.Angle;
import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.impl.ArcByCenterPointImpl;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Tests for interpolation with {@link GridInterpolation}.
 * 
 * @author Simon Templer
 */
@Features("Geometries")
@Stories("Arcs")
@SuppressWarnings("javadoc")
public class GridInterpolationTest extends AbstractArcTest {

	@Test
	public void testHalfCircle() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), 1.0,
				Angle.fromDegrees(0), Angle.fromDegrees(180), true);

		gridInterpolationTest(arc, 0.1, false);
	}

	@Test
	public void testHalfCircleAllGrid() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0.5, 0.5), 0.95,
				Angle.fromDegrees(0), Angle.fromDegrees(180), true);

		gridInterpolationTest(arc, 0.1, true);
	}

	@Test
	public void testOffsetBig() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(2, 2), 15.0,
				Angle.fromDegrees(45), Angle.fromDegrees(135), true);

		gridInterpolationTest(arc, 0.1, false);
	}

	@Test
	public void testLarge() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), 50.0,
				Angle.fromDegrees(45), Angle.fromDegrees(135), true);

		gridInterpolationTest(arc, 0.1, false);
	}

	@Test
	public void testOffsetCircle() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(2, 2), 5.0,
				Angle.fromDegrees(0), Angle.fromDegrees(0), true);

		gridInterpolationTest(arc, 0.1, false);
	}

	@Test
	public void test90Deegrees() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), Math.sqrt(2.0),
				Angle.fromDegrees(45), Angle.fromDegrees(135), false);

		gridInterpolationTest(arc, 0.1, false);
	}

	// utility methods

	private LineString gridInterpolationTest(Arc arc, double maxPositionalError,
			boolean moveAllToGrid) throws IOException {
		GridInterpolation interpol = new GridInterpolation();
		Map<String, String> properties = new HashMap<>();
		if (moveAllToGrid) {
			properties.put(GridInterpolation.PARAMETER_MOVE_ALL_TO_GRID, "true");
		}
		interpol.configure(new GeometryFactory(), maxPositionalError, properties);

		LineString result = interpol.interpolateArc(arc);

		double gridSize = GridUtil.getGridSize(maxPositionalError);
		drawGridInterpolatedArc(arc, gridSize, result);

		// test interpolated geometry
		Coordinate[] coords = result.getCoordinates();
		for (int i = 0; i < coords.length; i++) {
			Coordinate c = coords[i];

			boolean checkGrid = moveAllToGrid || (!c.equals(arc.toArcByPoints().getStartPoint())
					&& !c.equals(arc.toArcByPoints().getMiddlePoint())
					&& !c.equals(arc.toArcByPoints().getEndPoint()));

			if (checkGrid) {
				// check if coordinate on grid
				GridUtilTest.checkOnGrid(c, gridSize);

				// check if two coordinates are not the same
				if (i < coords.length - 1) {
					Coordinate c2 = coords[i + 1];

					assertNotEquals("Subsequent coordinates are equal", c, c2);

					// better check is to compare difference in x and y based on
					// grid size
					boolean xDifferent = Math.abs(c2.x - c.x) > (gridSize / 2);
					boolean yDifferent = Math.abs(c2.y - c.y) > (gridSize / 2);
					assertTrue("Subsequent coordinates are equal", xDifferent || yDifferent);
				}
			}
		}

		return result;
	}

}
