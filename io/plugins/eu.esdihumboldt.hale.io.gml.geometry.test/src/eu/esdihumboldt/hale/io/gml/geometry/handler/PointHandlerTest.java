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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Tests for reading point geometries.
 * 
 * @author Simon Templer, Arun Varma
 */
public class PointHandlerTest extends AbstractHandlerTest {

	/**
	 * The reference point geometry for checking if a point that was read is
	 * correct
	 */
	private Point reference;
	private Point referenceOnGrid;

	@Override
	public void init() {
		super.init();

		// create the reference point geometry
		reference = geomFactory.createPoint(new Coordinate(127906.229, 489141.472));
		referenceOnGrid = geomFactory.createPoint(new Coordinate(127906.3, 489141.5));
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

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkPointPropertyInstance(instance);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance);

			// 3. GeometryProperty with Point defined through coordinates
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);
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

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkPointPropertyInstance(instance);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance);

			// 3. GeometryProperty with Point defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);

			// 4. PointProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance);

			// 5. GeometryProperty with Point defined through pos
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);
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

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkPointPropertyInstance(instance);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance);

			// 3. GeometryProperty with Point defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);

			// 4. PointProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance);

			// 5. GeometryProperty with Point defined through pos
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);
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

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkPointPropertyInstance(instance);

			// 2. GeometryProperty with Point defined through coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);

			// 3. PointProperty with Point defined through pos
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance);

			// 4. GeometryProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);
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

		boolean keepOriginal = false;

		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml2.xml").toURI(), keepOriginal);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkPointPropertyInstance(instance, keepOriginal);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance, keepOriginal);

			// 3. GeometryProperty with Point defined through coordinates
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance, keepOriginal);
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
		boolean keepOriginal = false;
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml3.xml").toURI(), keepOriginal);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkPointPropertyInstance(instance, keepOriginal);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance, keepOriginal);

			// 3. GeometryProperty with Point defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance, keepOriginal);

			// 4. PointProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance, keepOriginal);

			// 5. GeometryProperty with Point defined through pos
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance, keepOriginal);
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
		boolean keepOriginal = false;
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml31.xml").toURI(), keepOriginal);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkPointPropertyInstance(instance, keepOriginal);

			// 2. PointProperty with Point defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance, keepOriginal);

			// 3. GeometryProperty with Point defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance, keepOriginal);

			// 4. PointProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance, keepOriginal);

			// 5. GeometryProperty with Point defined through pos
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance, keepOriginal);
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
		boolean keepOriginal = false;
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/point/sample-point-gml32.xml").toURI(), keepOriginal);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. PointProperty with Point defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkPointPropertyInstance(instance, keepOriginal);

			// 2. GeometryProperty with Point defined through coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance, keepOriginal);

			// 3. PointProperty with Point defined through pos
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkPointPropertyInstance(instance, keepOriginal);

			// 4. GeometryProperty with Point defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance, keepOriginal);
		} finally {
			it.close();
		}
	}

	private void checkGeometryPropertyInstance(Instance instance) {
		checkGeometryPropertyInstance(instance, true);
	}

	private void checkGeometryPropertyInstance(Instance instance, boolean keepOriginal) {
		Collection<Object> geomVals = PropertyResolver.getValues(instance, "Point", false);
		assertNotNull(geomVals);
		assertEquals(1, geomVals.size());

		Object geom = geomVals.iterator().next();
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance, keepOriginal);
	}

	private void checkPointPropertyInstance(Instance instance) {
		checkPointPropertyInstance(instance, true);
	}

	private void checkPointPropertyInstance(Instance instance, boolean keepOriginal) {
		Object[] geomVals = instance.getProperty(new QName(NS_TEST, "geometry"));
		assertNotNull(geomVals);
		assertEquals(1, geomVals.length);

		Object geom = geomVals[0];
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance, keepOriginal);
	}

	private void checkGeomInstance(Instance geomInstance, boolean keepOriginal) {
		assertTrue(geomInstance.getValue() instanceof GeometryProperty<?>);

		@SuppressWarnings("unchecked")
		Point point = ((GeometryProperty<Point>) geomInstance.getValue()).getGeometry();
		assertTrue("Read geometry does not match the reference geometry",
				point.equalsExact(keepOriginal ? reference : referenceOnGrid));

		// TODO check CRS!
	}

}
