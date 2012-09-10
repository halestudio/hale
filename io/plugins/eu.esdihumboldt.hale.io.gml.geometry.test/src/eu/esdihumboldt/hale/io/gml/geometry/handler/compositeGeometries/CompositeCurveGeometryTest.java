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

package eu.esdihumboldt.hale.io.gml.geometry.handler.compositeGeometries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Test for reading composite curve geometries
 * 
 * @author Patrick Lieb
 */
public class CompositeCurveGeometryTest extends AbstractHandlerTest {

	private MultiLineString reference;

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest#init()
	 */
	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(0.01, 3.2),
				new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2) };
		LineString linestring1 = geomFactory.createLineString(coordinates);

		LineString[] lines = new LineString[] { linestring1 };
		reference = geomFactory.createMultiLineString(lines);
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
			checkCompositeCurvePropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	private void checkCompositeCurvePropertyInstance(Instance instance) {
		Object[] geomVals = instance.getProperty(new QName(NS_TEST, "geometry"));
		assertNotNull(geomVals);
		assertEquals(1, geomVals.length);

		Object geom = geomVals[0];
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance);
	}

	private void checkGeomInstance(Instance geomInstance) {
		assertTrue(geomInstance.getValue() instanceof Collection<?>);
		for (Object instance : ((Collection<?>) geomInstance.getValue())) {
			assertTrue(instance instanceof GeometryProperty<?>);
			@SuppressWarnings("unchecked")
			MultiLineString multilinestring = ((GeometryProperty<MultiLineString>) instance)
					.getGeometry();
			assertTrue("Read geometry does not match the reference geometry",
					multilinestring.equalsExact(reference));
		}
	}

}
