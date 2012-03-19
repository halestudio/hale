/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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

/**
 * Test for reading linear ring geometries
 * 
 * @author Patrick Lieb
 */
public class LinearRingHandlerTest extends AbstractHandlerTest {

	private LinearRing reference;

	//XXX no test for geometry properties
	
	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] {
				new Coordinate(0.01, 3.2), new Coordinate(3.33, 3.33),
				new Coordinate(0.01, -3.2), new Coordinate(-3.33, -3.2),
				new Coordinate(0.01, 3.2) };
		reference = geomFactory.createLinearRing(coordinates);
	}

	/**
	 * Test linear ring geometries read from a GML 2 file
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testLinearRingGml2() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/sample-linearring-gml2.xml")
						.toURI());

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
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testLinearRingGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/sample-linearring-gml3.xml")
						.toURI());

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
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testLinearRingGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/sample-linearring-gml31.xml")
						.toURI());

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

			// 5. LinearRingProperty with LinearRing defined through pointProperty
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

//	/**
//	 * Test linear ring geometries read from a GML 3.2 file
//	 * 
//	 * @throws Exception
//	 *             if an error occurs
//	 */
//	@Test
//	public void testLinearRingGml32() throws Exception {
//		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
//				getClass().getResource("/data/geom-gml32.xsd").toURI(),
//				getClass().getResource("/data/sample-linearring-gml32.xml")
//						.toURI());
//
//		// five instances expected
//		ResourceIterator<Instance> it = instances.iterator();
//		try {
//			// 1. LinearRingProperty with LinearRing defined through coordinates
//			assertTrue("First sample feature missing", it.hasNext());
//			Instance instance = it.next();
//			checkLinearRingPropertyInstance(instance);
//
//			// 2. LinearRingProperty with LinearRing defined through pos
//			assertTrue("Second sample feature missing", it.hasNext());
//			instance = it.next();
//			checkLinearRingPropertyInstance(instance);
//
//			// 3. LinearRingProperty with LinearRing defined through pointRep
//			assertTrue("Third sample feature missing", it.hasNext());
//			instance = it.next();
//			checkLinearRingPropertyInstance(instance);
//
//			// 4. LinearRingProperty with LinearRing defined through pointProperty
//			assertTrue("Fourth sample feature missing", it.hasNext());
//			instance = it.next();
//			checkLinearRingPropertyInstance(instance);
//
//			// 5. LinearRingProperty with LinearRing defined through posList
//			assertTrue("Fifth sample feature missing", it.hasNext());
//			instance = it.next();
//			checkLinearRingPropertyInstance(instance);
//		} finally {
//			it.close();
//		}
//	}

	private void checkLinearRingPropertyInstance(Instance instance) {
		Object[] geomVals = instance
				.getProperty(new QName(NS_TEST, "geometry"));
		assertNotNull(geomVals);
		assertEquals(1, geomVals.length);

		Object geom = geomVals[0];
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance);
	}

	private void checkGeomInstance(Instance geomInstance) {
		assertTrue(geomInstance.getValue() instanceof GeometryProperty<?>);

		@SuppressWarnings("unchecked")
		LinearRing linearring = ((GeometryProperty<LinearRing>) geomInstance
				.getValue()).getGeometry();
		assertTrue("Read geometry does not match the reference geometry",
				linearring.equalsExact(reference));
	}
}
