/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.geometry.handler.compositeGeometries;

import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import eu.esdihumboldt.hale.common.instance.geometry.InterpolationHelper;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.InterpolationConfigurations;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.ReaderConfiguration;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for reading composite curve geometries
 * 
 * @author Patrick Lieb
 * @author Arun Varma
 * @author Simon Templer
 */
@Features("Geometries")
@Stories("GML")
public class CompositeCurveGeometryTest extends AbstractHandlerTest {

	private LineString reference;
	private final ReaderConfiguration gridConfig = InterpolationConfigurations.ALL_TO_GRID_DEFAULT;
	private Consumer<Geometry> checker;
	private Consumer<Geometry> gridChecker;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(0.01, 3.2),
				new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2) };
		reference = geomFactory.createLineString(coordinates);

		checker = referenceChecker(reference);

		gridChecker = combine(
				referenceChecker(reference, InterpolationHelper.DEFAULT_MAX_POSITION_ERROR),
				gridConfig.geometryChecker());
	}

	/**
	 * Test composite curve geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCompositeCurveGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/curve/sample-compositecurve-gml32.xml").toURI());

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, checker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test composite curve geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCompositeCurveGml32_combined() throws Exception {
		InstanceCollection instances = AbstractHandlerTest
				.loadXMLInstances(getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
						getClass()
								.getResource("/data/curve/sample-compositecurve-gml32_combined.xml")
								.toURI());

		LineString combined = geomFactory.createLineString(
				new Coordinate[] { new Coordinate(0, 0), new Coordinate(1, 1), new Coordinate(2, 2),
						new Coordinate(3, 1), new Coordinate(4, 0), new Coordinate(5, -1),
						new Coordinate(6, 0), new Coordinate(7, 2), new Coordinate(8, 4) });

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, referenceChecker(combined));
		} finally {
			it.close();
		}
	}

	/**
	 * Test composite curve geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCompositeCurveGml32_mismatch() throws Exception {
		InstanceCollection instances = AbstractHandlerTest
				.loadXMLInstances(getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
						getClass()
								.getResource("/data/curve/sample-compositecurve-gml32_mismatch.xml")
								.toURI());

		LineString ls1 = geomFactory.createLineString(new Coordinate[] { new Coordinate(0, 0),
				new Coordinate(1, 1), new Coordinate(2, 2) });
		LineString ls2 = geomFactory.createLineString(new Coordinate[] { new Coordinate(2, 3),
				new Coordinate(3, 1), new Coordinate(4, 0) });
		LineString ls3 = geomFactory
				.createLineString(new Coordinate[] { new Coordinate(4, 0), new Coordinate(5, -1),
						new Coordinate(6, 0), new Coordinate(7, 2), new Coordinate(8, 4) });

		MultiLineString separate = geomFactory
				.createMultiLineString(new LineString[] { ls1, ls2, ls3 });

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, referenceChecker(separate));
		} finally {
			it.close();
		}
	}

	/**
	 * Test composite curve geometries read from a GML 3.2 file. Geometry
	 * coordinates will be moved to universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCompositeCurveGml32_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/curve/sample-compositecurve-gml32.xml").toURI(),
				gridConfig);

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, gridChecker);
		} finally {
			it.close();
		}
	}

}
