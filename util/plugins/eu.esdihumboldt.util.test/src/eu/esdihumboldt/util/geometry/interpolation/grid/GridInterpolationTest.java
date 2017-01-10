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

		gridInterpolationTest(arc, 0.1);
	}

	@Test
	public void test90Deegrees() throws IOException {
		ArcByCenterPoint arc = new ArcByCenterPointImpl(new Coordinate(0, 0), Math.sqrt(2.0),
				Angle.fromDegrees(45), Angle.fromDegrees(135), false);

		gridInterpolationTest(arc, 0.1);
	}

	// utility methods

	private void gridInterpolationTest(Arc arc, double maxPositionalError) throws IOException {
		GridInterpolation interpol = new GridInterpolation();
		Map<String, String> properties = new HashMap<>();
		interpol.configure(new GeometryFactory(), maxPositionalError, properties);

		LineString result = interpol.interpolateArc(arc);

		drawGridInterpolatedArc(arc, GridUtil.getGridSize(maxPositionalError), result);

		// TODO test interpolated geometry
	}

}
