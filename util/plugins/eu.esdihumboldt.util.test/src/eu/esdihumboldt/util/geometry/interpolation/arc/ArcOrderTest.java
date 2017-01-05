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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.vividsolutions.jts.geom.Coordinate;

import eu.esdihumboldt.util.geometry.interpolation.ArcInterpolation;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for arc order
 * 
 * @author Arun
 */
@Features("Geometries")
@Stories("Arcs")
@RunWith(Parameterized.class)
public class ArcOrderTest {

	private final Coordinate[] coordinates;
	private final boolean expectedOrder;

	/**
	 * Constructor
	 * 
	 * @param coordinates arc coordinates
	 * @param expectedOrder expected order of arc
	 */
	public ArcOrderTest(Coordinate[] coordinates, boolean expectedOrder) {
		this.coordinates = coordinates;
		this.expectedOrder = expectedOrder;
	}

	/**
	 * provide input parameters to test
	 * 
	 * @return List of paramters
	 */
	@SuppressWarnings("rawtypes")
	@Parameters
	public static Collection provideCoordinates() {
		return Arrays.asList(new Object[][] { //
				{ new Coordinate[] { new Coordinate(577869.169, 5917253.678),
						new Coordinate(577871.772, 5917250.386),
						new Coordinate(577874.884, 5917253.202) }, //
						false //
				}, //
				{ new Coordinate[] { new Coordinate(577738.2, 5917351.786),
						new Coordinate(577740.608, 5917347.876),
						new Coordinate(577745.185, 5917348.135) }, //
						false //
				}, //
				{ new Coordinate[] { new Coordinate(240, 280), new Coordinate(210, 150),
						new Coordinate(300, 100) }, //
						false //
				}, //
				{ new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 16),
						new Coordinate(16, 8) }, //
						true //
				}, //
				{ new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 6.5),
						new Coordinate(16, 8) }, //
						false //
				}, //
				{ new Coordinate[] { new Coordinate(3, 10.5), new Coordinate(4, 7.75),
						new Coordinate(8, 8) }, //
						false //
				}, //
				{ new Coordinate[] { new Coordinate(353248.457, 5531386.407),
						new Coordinate(353249.438, 5531386.407),
						new Coordinate(353250.399, 5531386.217) }, //
						true //
				}, //
				{ new Coordinate[] { new Coordinate(0, 5), new Coordinate(-5, 0),
						new Coordinate(0, -5) }, //
						false //
				}, //
				{ new Coordinate[] { new Coordinate(353297.973, 5531361.379),
						new Coordinate(353298.192, 5531360.429),
						new Coordinate(353298.503, 5531359.504) }, //
						false //
				}, //
				{ new Coordinate[] { new Coordinate(563066.454, 5934020.581),
						new Coordinate(563061.303, 5934032.092),
						new Coordinate(563062.253, 5934019.517) }, //
						false //
				} //
		});//
	}

	/**
	 * Test
	 */
	@Test
	public void testOrder() {
		boolean isClockWise = ArcInterpolation.getOrderOfArc(this.coordinates);
		assertTrue(this.expectedOrder == isClockWise);
	}

}
