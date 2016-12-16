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

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
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
import eu.esdihumboldt.util.geometry.interpolation.ArcInterpolation;
import eu.esdihumboldt.util.geometry.interpolation.Interpolation;

/**
 * Test for the Interpolation of arc algorithm
 * 
 * @author Arun
 */
@RunWith(Parameterized.class)
public class ArcInterpolationTest extends AbstractInterpolationTest {

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
	public ArcInterpolationTest(int testIndex, Coordinate[] coordinates, Class geometry,
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
	 * Passing arc geometries
	 * 
	 * @return Collection of arc coordinates and type of generated geometry
	 */
	@SuppressWarnings("rawtypes")
	@Parameters
	public static Collection addCoordiantes() {
		return Arrays.asList(new Object[][] { //
				{ 0, new Coordinate[] { new Coordinate(577869.169, 5917253.678),
						new Coordinate(577871.772, 5917250.386),
						new Coordinate(577874.884, 5917253.202) }, //
						LineString.class, SKIP_TEST }, //
				{ 1, new Coordinate[] { new Coordinate(577738.2, 5917351.786),
						new Coordinate(577740.608, 5917347.876),
						new Coordinate(577745.185, 5917348.135) }, //
						LineString.class, SKIP_TEST }, //
				{ 2, new Coordinate[] { new Coordinate(240, 280), new Coordinate(210, 150),
						new Coordinate(300, 100) }, //
						LineString.class, SKIP_TEST }, //
				{ 3, new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 16),
						new Coordinate(16, 8) }, //
						LineString.class, SKIP_TEST }, //
				{ 4, new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 6.5),
						new Coordinate(16, 8) }, //
						LineString.class, SKIP_TEST }, //
				{ 5, new Coordinate[] { new Coordinate(3, 10.5), new Coordinate(4, 7.75),
						new Coordinate(8, 8) }, //
						LineString.class, SKIP_TEST }, //
				{ 6, new Coordinate[] { new Coordinate(353248.457, 5531386.407),
						new Coordinate(353249.438, 5531386.407),
						new Coordinate(353250.399, 5531386.217) }, //
						LineString.class, SKIP_TEST }, //
				{ 7, new Coordinate[] { new Coordinate(351141.396, 5532140.355),
						new Coordinate(351110.659, 5532137.542),
						new Coordinate(351080.17, 5532132.742) }, //
						LineString.class, SKIP_TEST }, //
				{ 8, new Coordinate[] { new Coordinate(350925.682, 5532108.264),
						new Coordinate(350848.556, 5532095.285),
						new Coordinate(350771.515, 5532081.814) }, //
						LineString.class, SKIP_TEST }, //
				{ 9, new Coordinate[] { new Coordinate(351080.17, 5532132.742),
						new Coordinate(351002.887, 5532120.75),
						new Coordinate(350925.682, 5532108.264) }, //
						LineString.class, SKIP_TEST }, //
				{ 10, new Coordinate[] { new Coordinate(0, 5), new Coordinate(-5, 0),
						new Coordinate(0, -5) }, //
						LineString.class, SKIP_TEST }, //
				{ 11, new Coordinate[] { new Coordinate(353297.973, 5531361.379),
						new Coordinate(353298.192, 5531360.429),
						new Coordinate(353298.503, 5531359.504) }, //
						LineString.class, SKIP_TEST }, //
				{ 12, new Coordinate[] { new Coordinate(563066.454, 5934020.581),
						new Coordinate(563061.303, 5934032.092),
						new Coordinate(563062.253, 5934019.517) }, //
						LineString.class, SKIP_TEST }, //
				{ 13, new Coordinate[] { new Coordinate(0.01, 3.2), new Coordinate(3.33, 3.33),
						new Coordinate(0.01, -3.2) }, //
						LineString.class, SKIP_TEST }, //
				{ 14, new Coordinate[] { new Coordinate(563196.992, 5935163.384),
						new Coordinate(563189.534, 5935166.129),
						new Coordinate(563182.076, 5935168.874) }, //
						LineString.class, SKIP_TEST }, //
				{ 15, new Coordinate[] { new Coordinate(563073.474, 5934958.319),
						new Coordinate(563091.634, 5934958.353),
						new Coordinate(563109.794, 5934958.387) }, //
						LineString.class, SKIP_TEST } //
		});
	}

	/**
	 * test algorithm
	 */
	@Test
	public void testInterpolation() {
		System.out.println("Test-" + testIndex);
		Interpolation<LineString> interpolation = new ArcInterpolation(this.arcCoordinates, e,
				DEFAULT_KEEP_ORIGINAL);
		Geometry interpolatedArc = interpolation.interpolateRawGeometry();

		assertNotNull(interpolatedArc);
		Assert.assertEquals(interpolatedArc.getClass(), generatedGeometryType);

		checkNeighbourCoordinates(interpolatedArc);

		validateCoordinatesOnGrid(interpolatedArc, this.arcCoordinates.length, e,
				DEFAULT_KEEP_ORIGINAL);

		if (DRAW_IMAGE)
			drawImage((LineString) interpolatedArc, arcCoordinates, testIndex);
	}
}
