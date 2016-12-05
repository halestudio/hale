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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.util.geometry.interpolation.util.DrawGeometry;

/**
 * Test for Circle Type
 * 
 * @author Arun
 */
@RunWith(Parameterized.class)
public class CircleInterpolationTest {

	private final int testIndex;
	private final Coordinate[] arcCoordinates;
	@SuppressWarnings("rawtypes")
	private final Class generatedGeometryType;
	private final boolean skipTest;
	private static final double e = 0.1;

	private static final boolean SKIP_TEST = false;

	private static final boolean DRAW_IMAGE = false;

	private static final boolean DEFAULT_KEEP_ORIGINAL = true;

	/**
	 * Constructor for parameterized test
	 * 
	 * @param testIndex Index of test parameters
	 * @param coordinates input arc coordinates
	 * @param geometry type of output geometry
	 * @param skipTest if wants to skip test
	 */
	@SuppressWarnings("rawtypes")
	public CircleInterpolationTest(int testIndex, Coordinate[] coordinates, Class geometry,
			boolean skipTest) {
		this.testIndex = testIndex;
		this.arcCoordinates = coordinates;
		this.generatedGeometryType = geometry;
		this.skipTest = skipTest;
	}

	/**
	 * Passing circle geometries
	 * 
	 * @return Collection of arc coordinates and type of generated geometry
	 */
	@SuppressWarnings("rawtypes")
	@Parameters
	public static Collection addCoordiantes() {
		return Arrays.asList(new Object[][] { //
				{ 0, new Coordinate[] { new Coordinate(569884.075, 5936816.054),
						new Coordinate(569883.230, 5936814.518),
						new Coordinate(569884.919, 5936814.518) }, //
						LineString.class, SKIP_TEST }, //
				{ 1, new Coordinate[] { new Coordinate(568420.259, 5936349.171),
						new Coordinate(568419.414, 5936347.635),
						new Coordinate(568421.103, 5936347.635) }, //
						LineString.class, SKIP_TEST }, //
				{ 2, new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 16),
						new Coordinate(16, 8) }, //
						LineString.class, SKIP_TEST }, //
				{ 3, new Coordinate[] { new Coordinate(240, 280), new Coordinate(210, 150),
						new Coordinate(300, 100) }, //
						LineString.class, SKIP_TEST }, //
				{ 4, new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 6.5),
						new Coordinate(16, 8) }, //
						LineString.class, SKIP_TEST }, //
				{ 5, new Coordinate[] { new Coordinate(3, 10.5), new Coordinate(4, 7.75),
						new Coordinate(8, 8) }, //
						LineString.class, SKIP_TEST }, //
				{ 6, new Coordinate[] { new Coordinate(569675.954, 5937944.689),
						new Coordinate(569675.109, 5937943.153),
						new Coordinate(569676.798, 5937943.153) }, //
						LineString.class, SKIP_TEST }, //
				{ 7, new Coordinate[] { new Coordinate(0.01, 3.2), new Coordinate(3.33, 3.33),
						new Coordinate(0.01, -3.2) }, //
						LineString.class, SKIP_TEST } //
		});
	}

//0.01,3.2 3.33,3.33 0.01,-3.2
	/**
	 * test algorithm
	 */
	@Test
	public void testInterpolation() {
		System.out.println("-- Test-" + testIndex + " begin --");
		if (skipTest) {
			System.out.println("-- -- Test is configured to skip");
			return;
		}
		Interpolation<LineString> interpolation = new CircleInterpolation(this.arcCoordinates, e,
				DEFAULT_KEEP_ORIGINAL);
		Geometry interpolatedArc = interpolation.interpolateRawGeometry();

		assertNotNull(interpolatedArc);
		Assert.assertEquals(interpolatedArc.getClass(), generatedGeometryType);

//		System.out.println(interpolatedArc.getCoordinates().length);
//		System.out.println("");
//		for (Coordinate coordinate : interpolatedArc.getCoordinates())
//			System.out.print("new Coordinate(" + coordinate.x + "," + coordinate.y + "), ");
//		System.out.println("");

		Coordinate[] coordinates = interpolatedArc.getCoordinates();
		for (int i = 1; i < coordinates.length; i++) {
			assertNotEquals("should not match neighbour coordinates", coordinates[i],
					coordinates[i - 1]);
		}
		if (DRAW_IMAGE)
			DrawGeometry.drawImage((LineString) interpolatedArc, arcCoordinates, testIndex);
	}
}
