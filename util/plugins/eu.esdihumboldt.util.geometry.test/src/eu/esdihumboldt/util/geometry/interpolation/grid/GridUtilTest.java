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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Tests for grid utilities.
 * 
 * @author Simon Templer
 */
@Features("Geometries")
@Stories("Arcs")
@SuppressWarnings("javadoc")
public class GridUtilTest {

	@Test
	public void testOrigin() {
		Coordinate coord = new Coordinate(0, 0);

		Coordinate moved = testMoveToGrid(coord, 0.1);

		// point should be the same
		assertEqualsCoord(coord, moved);
	}

	@Test
	public void testQ1() {
		Coordinate coord = new Coordinate(0.1, 0.1);

		Coordinate moved = testMoveToGrid(coord, 0.1);

		assertTrue(moved.x > 0.1);
		assertTrue(moved.x < 0.2);
		assertTrue(moved.y > 0.1);
		assertTrue(moved.y < 0.2);
	}

	@Test
	public void testQ2() {
		Coordinate coord = new Coordinate(-0.1, 0.1);

		Coordinate moved = testMoveToGrid(coord, 0.1);

		assertTrue(moved.x < -0.1);
		assertTrue(moved.x > -0.2);
		assertTrue(moved.y > 0.1);
		assertTrue(moved.y < 0.2);
	}

	@Test
	public void testQ3() {
		Coordinate coord = new Coordinate(-0.1, -0.1);

		Coordinate moved = testMoveToGrid(coord, 0.1);

		assertTrue(moved.x < -0.1);
		assertTrue(moved.x > -0.2);
		assertTrue(moved.y < -0.1);
		assertTrue(moved.y > -0.2);
	}

	@Test
	public void testQ4() {
		Coordinate coord = new Coordinate(0.1, -0.1);

		Coordinate moved = testMoveToGrid(coord, 0.1);

		assertTrue(moved.x > 0.1);
		assertTrue(moved.x < 0.2);
		assertTrue(moved.y < -0.1);
		assertTrue(moved.y > -0.2);
	}

	@Test
	public void testCenter() {
		double gridSize = GridUtil.getGridSize(0.1);
		Coordinate coord = new Coordinate(gridSize / 2.0, gridSize / 2.0);

		Coordinate moved = testMoveToGrid(coord, 0.1);

		assertEquals(gridSize, moved.x, 1e-8);
		assertEquals(gridSize, moved.y, 1e-8);
	}

	@Test
	public void testSample1() {
		double gridSize = GridUtil.getGridSize(0.1);
		Coordinate coord = new Coordinate(gridSize * 4 + 0.05, gridSize * 3 + 0.05);

		Coordinate moved = testMoveToGrid(coord, 0.1);

		assertEquals(gridSize * 4, moved.x, 1e-8);
		assertEquals(gridSize * 3, moved.y, 1e-8);
	}

	// utility methods

	private Coordinate testMoveToGrid(Coordinate coord, double maxPositionalError) {
		double gridSize = GridUtil.getGridSize(maxPositionalError);

		Coordinate moved = GridUtil.movePointToGrid(coord, gridSize);

		// assure distance to original point is less than maxPositionalError
		double distance = coord.distance(moved);
		assertTrue(MessageFormat.format(
				"Distance ({0}) is greater than the maximum positional error ({1})", distance,
				maxPositionalError), distance <= maxPositionalError);

		// check if the ordinates are aligned with the grid
		checkOnGrid(moved, gridSize);

		return moved;
	}

	public static void checkOnGrid(Coordinate c, double gridSize) {
		checkOnGrid(c.x, gridSize);
		checkOnGrid(c.y, gridSize);
	}

	public static void checkOnGrid(double ord, double gridSize) {
		double fact = ord / gridSize;
		assertEquals("Ordinate does not align with the grid", Math.round(fact), fact, 1e-8);
	}

	/**
	 * Test coordinates being equal using a lax comparison for X and Y.
	 * 
	 * @param expected the expected coordinate
	 * @param other the coordinate to compare
	 */
	public void assertEqualsCoord(Coordinate expected, Coordinate other) {
		assertEquals(expected.x, other.x, 1e-3);
		assertEquals(expected.y, other.y, 1e-3);
	}

}
