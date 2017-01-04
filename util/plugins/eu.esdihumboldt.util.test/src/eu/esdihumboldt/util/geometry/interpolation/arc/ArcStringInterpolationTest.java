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

package eu.esdihumboldt.util.geometry.interpolation.arc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.util.geometry.interpolation.AbstractInterpolationTest;
import eu.esdihumboldt.util.geometry.interpolation.ArcStringInterpolation;
import eu.esdihumboldt.util.geometry.interpolation.Interpolation;

/**
 * Arc String interpolation test
 * 
 * @author Arun
 */

@RunWith(Parameterized.class)
public class ArcStringInterpolationTest extends AbstractInterpolationTest {

	private final int testIndex;
	private final Coordinate[] arcCoordinates;
	@SuppressWarnings("rawtypes")
	private final Class generatedGeometryType;
	private final boolean skipTest;
	private static final double e = 0.1;

	private static final boolean SKIP_TEST = false;

	private static final boolean DRAW_IMAGE = false;

	private static final boolean DEFAULT_KEEP_ORIGINAL = true;

	private static final List<Integer> nullTestIndex = Arrays.asList(8);

	/**
	 * Constructor for parameterized test
	 * 
	 * @param testIndex Index of test parameters
	 * @param coordinates input arc coordinates
	 * @param geometry type of output geometry
	 * @param skipTest if wants to skip test
	 */
	@SuppressWarnings("rawtypes")
	public ArcStringInterpolationTest(int testIndex, Coordinate[] coordinates, Class geometry,
			boolean skipTest) {
		this.testIndex = testIndex;
		this.arcCoordinates = coordinates;
		this.generatedGeometryType = geometry;
		this.skipTest = skipTest;
	}

	/**
	 * Before method to skip test
	 */
	@Before
	public void beforeMethod() {
		Assume.assumeFalse(this.skipTest);
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
						LineString.class, SKIP_TEST }, // 1 Arc
				{ 2, new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 16),
						new Coordinate(16, 8) }, //
						LineString.class, SKIP_TEST }, // 1 Arc
				{ 3, new Coordinate[] { new Coordinate(240, 280), new Coordinate(210, 150),
						new Coordinate(300, 100) }, //
						LineString.class, SKIP_TEST }, // 1 Arc
				{ 4, new Coordinate[] { new Coordinate(0.01, 3.2), new Coordinate(3.33, 3.33),
						new Coordinate(0.01, -3.2) }, //
						LineString.class, SKIP_TEST }, // 1 Arc
				{ 5, new Coordinate[] { new Coordinate(-8, 0), new Coordinate(0, 8),
						new Coordinate(8, 0), new Coordinate(0, -8), new Coordinate(-8, 0) }, //
						LineString.class, SKIP_TEST }, // 2 Arcs
				{ 6, new Coordinate[] { new Coordinate(-1, 0), new Coordinate(0, 1),
						new Coordinate(1, 0), new Coordinate(2, -1), new Coordinate(3, 0) }, //
						LineString.class, SKIP_TEST }, // 2 Arcs
				{ 7, new Coordinate[] { new Coordinate(-1, 0), new Coordinate(0, 1),
						new Coordinate(1, 0), new Coordinate(-0.5, 1.5), new Coordinate(-2, 0),
						new Coordinate(-1.5, -0.5), new Coordinate(-1, 0) }, //
						LineString.class, SKIP_TEST }, // 3 Arcs
				{ 8, new Coordinate[] { new Coordinate(-1, 0), new Coordinate(0, 1),
						new Coordinate(1, 0), new Coordinate(-0.5, 1.5), new Coordinate(-2, 0),
						new Coordinate(-1.5, -0.5) }, //
						LineString.class, SKIP_TEST } // not valid coordinates
														// length, sending 6
		});
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testInterpolation() throws IOException {
		System.out.println("-- Test-" + testIndex + " begin --");
		Interpolation<LineString> interpolation = new ArcStringInterpolation(this.arcCoordinates, e,
				DEFAULT_KEEP_ORIGINAL);
		Geometry interpolatedArc = interpolation.interpolateRawGeometry();

		if (!nullTestIndex.contains(testIndex)) {
			assertNotNull(interpolatedArc);
			assertEquals(interpolatedArc.getClass(), generatedGeometryType);

			checkNeighbourCoordinates(interpolatedArc);

			validateCoordinatesOnGrid(interpolatedArc, this.arcCoordinates.length, e,
					DEFAULT_KEEP_ORIGINAL);

			if (DRAW_IMAGE) {
				drawImage((LineString) interpolatedArc, arcCoordinates, testIndex);
			}
		}
		else {
			assertNull(interpolatedArc);
		}
	}

}