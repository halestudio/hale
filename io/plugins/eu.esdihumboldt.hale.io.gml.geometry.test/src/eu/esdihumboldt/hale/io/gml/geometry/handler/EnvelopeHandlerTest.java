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
import com.vividsolutions.jts.geom.MultiPoint;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Test for reading envelope geometries
 * 
 * @author Patrick Lieb
 */
public class EnvelopeHandlerTest extends AbstractHandlerTest {

	private MultiPoint reference;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] {
				new Coordinate(-39799.68820381, 273207.53980172),
				new Coordinate(-39841.185, 273182.863) };
		reference = geomFactory.createMultiPoint(coordinates);
	}

	/**
	 * Test envelope geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testEnvelopeGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/envelope/sample-envelope-gml3.xml").toURI());

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. EnvelopeProperty defined through pos
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkEnvelopePropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	private void checkEnvelopePropertyInstance(Instance instance) {
		Object[] geomVals = instance.getProperty(new QName(NS_TEST, "geometry"));
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
		MultiPoint multipoint = ((GeometryProperty<MultiPoint>) geomInstance.getValue())
				.getGeometry();
		assertTrue("Read geometry does not match the reference geometry",
				multipoint.equalsExact(reference));
	}

}
