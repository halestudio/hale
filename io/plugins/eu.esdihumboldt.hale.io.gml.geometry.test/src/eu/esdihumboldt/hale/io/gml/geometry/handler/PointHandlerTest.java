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

package eu.esdihumboldt.hale.io.gml.geometry.handler;

import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;

import org.junit.Test;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

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
 * Tests for reading point geometries.
 * 
 * @author Simon Templer
 * @author Arun Varma
 */
@Features("Geometries")
@Stories("GML")
public class PointHandlerTest extends AbstractHandlerTest {

	/**
	 * The reference point geometry for checking if a point that was read is
	 * correct
	 */
	private Point reference;

	private final ReaderConfiguration gridConfig = InterpolationConfigurations.ALL_TO_GRID_DEFAULT;
	private Consumer<Geometry> gridChecker;

	@Override
	public void init() {
		super.init();

		// create the reference point geometry
		reference = geomFactory.createPoint(new Coordinate(127906.229, 489141.472));

		gridChecker = combine(
				referenceChecker(reference, InterpolationHelper.DEFAULT_MAX_POSITION_ERROR),
				gridConfig.geometryChecker());
	}

	/**
	 * Test point geometries read from a GML 2 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testPointGml2() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml2.xml").toURI());

		Consumer<Geometry> checker = referenceChecker(reference);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, checker);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);

			// 3. GeometryProperty with Point defined through coordinates
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test point geometries read from a GML 3 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testPointGml3() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml3.xml").toURI());

		Consumer<Geometry> checker = referenceChecker(reference);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, checker);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);

			// 3. GeometryProperty with Point defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);

			// 4. PointProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);

			// 5. GeometryProperty with Point defined through pos
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test point geometries read from a GML 3.1 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testPointGml31() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml31.xml").toURI());

		Consumer<Geometry> checker = referenceChecker(reference);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, checker);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);

			// 3. GeometryProperty with Point defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);

			// 4. PointProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);

			// 5. GeometryProperty with Point defined through pos
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test point geometries read from a GML 3.2 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testPointGml32() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml32.xml").toURI());

		Consumer<Geometry> checker = referenceChecker(reference);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, checker);

			// 2. GeometryProperty with Point defined through coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);

			// 3. PointProperty with Point defined through pos
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);

			// 4. GeometryProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test point geometries read from a GML 2 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testPointGml2_Grid() throws Exception {

		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml2.xml").toURI(), gridConfig);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 3. GeometryProperty with Point defined through coordinates
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test point geometries read from a GML 3 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testPointGml3_Grid() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml3.xml").toURI(), gridConfig);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 3. GeometryProperty with Point defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 4. PointProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 5. GeometryProperty with Point defined through pos
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test point geometries read from a GML 3.1 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testPointGml31_Grid() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml31.xml").toURI(), gridConfig);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 3. GeometryProperty with Point defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 4. PointProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 5. GeometryProperty with Point defined through pos
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test point geometries read from a GML 3.2 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testPointGml32_Grid() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml32.xml").toURI(), gridConfig);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 2. GeometryProperty with Point defined through coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 3. PointProperty with Point defined through pos
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 4. GeometryProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);
		} finally {
			it.close();
		}
	}

}
