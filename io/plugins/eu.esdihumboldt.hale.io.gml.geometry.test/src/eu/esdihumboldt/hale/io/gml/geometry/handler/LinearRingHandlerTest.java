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

import javax.xml.namespace.QName;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for reading linear ring geometries
 * 
 * @author Patrick Lieb, Arun Varma
 */
@Features("Geometries")
@Stories("GML")
public class LinearRingHandlerTest extends AbstractHandlerTest {

	private LinearRing reference;
	private LinearRing referenceOnGrid;

	// XXX no test for geometry properties

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(0.01, 3.2),
				new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2), new Coordinate(-3.33, -3.2),
				new Coordinate(0.01, 3.2) };
		reference = geomFactory.createLinearRing(coordinates);

		coordinates = new Coordinate[] { new Coordinate(0, 3.2), new Coordinate(3.3, 3.3),
				new Coordinate(0, -3.2), new Coordinate(-3.4, -3.2), new Coordinate(0, 3.2) };
		referenceOnGrid = geomFactory.createLinearRing(coordinates);

	}

	/**
	 * Test linear ring geometries read from a GML 2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLinearRingGml2() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/linearring/sample-linearring-gml2.xml").toURI());

		// two instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LinearRingProperty with LinearRing defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 2. LinearRingProperty with LinearRing defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linear ring geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLinearRingGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/linearring/sample-linearring-gml3.xml").toURI());

		// four instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LinearRingProperty with LinearRing defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 2. LinearRingProperty with LinearRing defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 3. LinearRingProperty with LinearRing defined through pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 4. LinearRingProperty with LinearRing defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linear ring geometries read from a GML 3.1 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLinearRingGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/linearring/sample-linearring-gml31.xml").toURI());

		// six instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LinearRingProperty with LinearRing defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 2. LinearRingProperty with LinearRing defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 3. LinearRingProperty with LinearRing defined through pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 4. LinearRingProperty with LinearRing defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 5. LinearRingProperty with LinearRing defined through
			// pointProperty
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 6. LinearRingProperty with LinearRing defined through posList
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linear ring geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLinearRingGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/linearring/sample-linearring-gml32.xml").toURI());

		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LinearRingProperty with LinearRing defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 2. LinearRingProperty with LinearRing defined through pos
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 3. LinearRingProperty with LinearRing defined through pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 4. LinearRingProperty with LinearRing defined through
			// pointProperty
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);

			// 5. LinearRingProperty with LinearRing defined through posList
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linear ring geometries read from a GML 2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLinearRingGml2_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/linearring/sample-linearring-gml2.xml").toURI(),
				false);

		// two instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LinearRingProperty with LinearRing defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 2. LinearRingProperty with LinearRing defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linear ring geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLinearRingGml3_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/linearring/sample-linearring-gml3.xml").toURI(),
				false);

		// four instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LinearRingProperty with LinearRing defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 2. LinearRingProperty with LinearRing defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 3. LinearRingProperty with LinearRing defined through pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 4. LinearRingProperty with LinearRing defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linear ring geometries read from a GML 3.1 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLinearRingGml31_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/linearring/sample-linearring-gml31.xml").toURI(),
				false);

		// six instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LinearRingProperty with LinearRing defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 2. LinearRingProperty with LinearRing defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 3. LinearRingProperty with LinearRing defined through pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 4. LinearRingProperty with LinearRing defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 5. LinearRingProperty with LinearRing defined through
			// pointProperty
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 6. LinearRingProperty with LinearRing defined through posList
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linear ring geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLinearRingGml32_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/linearring/sample-linearring-gml32.xml").toURI(),
				false);

		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LinearRingProperty with LinearRing defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 2. LinearRingProperty with LinearRing defined through pos
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 3. LinearRingProperty with LinearRing defined through pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 4. LinearRingProperty with LinearRing defined through
			// pointProperty
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);

			// 5. LinearRingProperty with LinearRing defined through posList
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLinearRingPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	private void checkLinearRingPropertyInstance(Instance instance) {
		checkLinearRingPropertyInstance(instance, true);
	}

	private void checkLinearRingPropertyInstance(Instance instance, boolean keepOriginal) {
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
		LinearRing linearring = ((GeometryProperty<LinearRing>) geomInstance.getValue())
				.getGeometry();
		assertTrue("Read geometry does not match the reference geometry",
				linearring.equalsExact(keepOriginal ? reference : referenceOnGrid));
	}
}
