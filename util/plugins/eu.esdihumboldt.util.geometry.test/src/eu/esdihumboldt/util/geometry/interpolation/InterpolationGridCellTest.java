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

import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for interpolation
 * 
 * @author Arun
 */
@Features("Geometries")
@Stories("Arcs")
@RunWith(Parameterized.class)
public class InterpolationGridCellTest {

	private final double e;

	private final Coordinate testCoordinate;

	private final Coordinate expectedGridCoordinate;

	private final String gridCell;

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
						{ "Bottom Left Corner", new Coordinate(5.2, 3.9), new Coordinate(5,
								4), 0.5 }, // bottom-left-corner
				{ "Bottom Right Corner", new Coordinate(4.9, 2.9), new Coordinate(5, 3), 0.5 }, // bottom-right-corner
				{ "Same-Grid-cell", new Coordinate(4.9, 2.9), new Coordinate(4.9, 2.9), 0.1 }, // Same-Grid-cell
				{ "Bottom Right Corner", new Coordinate(0.098, 0.048), new Coordinate(0.10, 0.05),
						0.025 }, // bottom-right-corner
				{ "4 No cell Up left corner", new Coordinate(-5.17, 7), new Coordinate(-5.2, 7),
						0.1 }, // bottom-right-corner
				{ "Same-Grid-cell", new Coordinate(4, 2), new Coordinate(4.0, 2.0), 0.1 }, // Same-Grid-cell
				{ "test11", new Coordinate(-122.44, 37.80), new Coordinate(-122.44, 37.80), 0.01 }, //
				{ "test12", new Coordinate(-122.45, 37.80), new Coordinate(-122.46, 37.80), 0.01 }, //
				{ "test13", new Coordinate(-122.45, 37.78), new Coordinate(-122.46, 37.78), 0.01 }, //
				{ "test14", new Coordinate(-122.24, 37.60), new Coordinate(-122.24, 37.60), 0.01 }, //
				{ "test14", new Coordinate(-5.0, -0.16), new Coordinate(-5.0, -0.12), 0.040 } //
		});

	}

	/**
	 * Test Grid cell point
	 */
	@Test
	public void testGridCellPoint() {

		Coordinate actualGridPoint = Interpolation.pointToGrid(this.testCoordinate, this.e);
		Assert.assertEquals(
				"Test fail for " + this.gridCell + ". Test coordinate:(" + testCoordinate + ") ",
				this.expectedGridCoordinate, actualGridPoint);
	}

}
