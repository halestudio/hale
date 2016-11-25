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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

/**
 * Test for interpolation
 * 
 * @author Arun
 */
@RunWith(Parameterized.class)
public class InterpolationGridCellTest {

	private final double e;

	private final Coordinate testCoordinate;

	private final Coordinate expectedGridCoordinate;

	private final String gridCell;

	private final static boolean KEEP_ORIGINAL = false;

	/**
	 * Constructor with parameters
	 * 
	 * @param gridCell Grid cell position
	 * @param testCoordinate Test Coordinates
	 * @param exprectedGridCoordinate expected Grid Coordinates
	 * @param e max positional error
	 */
	public InterpolationGridCellTest(String gridCell, Coordinate testCoordinate,
			Coordinate exprectedGridCoordinate, double e) {
		this.gridCell = gridCell;
		this.e = e;
		this.testCoordinate = testCoordinate;
		this.expectedGridCoordinate = exprectedGridCoordinate;
	}

	/**
	 * Passing arc geometries
	 * 
	 * @return Collection of arc coordinates and type of generated geometry
	 */
	@SuppressWarnings("rawtypes")
	@Parameters
	public static Collection addCoordiantes() {
		return Arrays
				.asList(new Object[][] { //
						{ "Center", new Coordinate(1.4, 1.4), new Coordinate(1.5, 1.5), 0.5 }, // Center
						{ "UP Left Corner", new Coordinate(1.2, 1.1), new Coordinate(1, 1), 0.5 }, // up-left-corner
						{ "UP Right Corner", new Coordinate(3.8, 2.1), new Coordinate(4, 2), 0.5 }, // up-right-corner
						{ "Bottom Left Corner", new Coordinate(5.2, 3.9),
								new Coordinate(5,
										4),
								0.5 }, // bottom-left-corner
						{ "Bottom Right Corner", new Coordinate(4.9, 2.9), new Coordinate(5,
								3), 0.5 }, // bottom-right-corner
				{ "Same-Grid-cell", new Coordinate(4.9, 2.9), new Coordinate(4.9, 2.9), 0.1 }, // Same-Grid-cell
				{ "Bottom Right Corner", new Coordinate(0.098, 0.048), new Coordinate(0.10, 0.05),
						0.025 }, // bottom-right-corner
				{ "4 No cell Up left corner", new Coordinate(-5.17, 7), new Coordinate(-5.2, 7),
						0.1 }// bottom-right-corner
		});

	}

	/**
	 * Test Grid cell point
	 */
	@Test
	public void testGridCellPoint() {
		TestClass test = new TestClass(null, e, KEEP_ORIGINAL);
		Coordinate actualGridPoint = test.pointToGrid(this.testCoordinate);
		Assert.assertEquals("Test fail for " + this.gridCell
				+ " grid coordinate. Actual coordinate:(" + actualGridPoint
				+ ") Expected Coordinate:(" + this.expectedGridCoordinate + ")",
				this.expectedGridCoordinate, actualGridPoint);
	}

	/**
	 * Dummy test class for interpolation to test points on grid cell
	 * 
	 */
	private class TestClass extends Interpolation<LineString> {

		/**
		 * Constructor
		 * 
		 * @param coordinates Coordinates
		 * @param maxPositionalError maximum positional error
		 * @param keepOriginal keeps original points intact
		 */
		public TestClass(Coordinate[] coordinates, double maxPositionalError,
				boolean keepOriginal) {
			super(coordinates, maxPositionalError, keepOriginal);

		}

		@Override
		protected boolean validateRawCoordinates() {
			return true;
		}

		@Override
		protected LineString interpolatedGeometry() {
			return null;
		}

		@Override
		public Coordinate pointToGrid(Coordinate coordinate) {
			return super.pointToGrid(coordinate);
		}

	}

}
