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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
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
 * @author Patrick Lieb, Arun Varma
 */
public class CompositeCurveGeometryTest extends AbstractHandlerTest {

	private LineString reference;
	private LineString referenceOnGrid;

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest#init()
	 */
	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(0.01, 3.2),
				new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2) };
		reference = geomFactory.createLineString(coordinates);

		// Grid
		coordinates = new Coordinate[] { new Coordinate(0, 3.2), new Coordinate(3.3, 3.3),
				new Coordinate(0, -3.2) };
		referenceOnGrid = geomFactory.createLineString(coordinates);
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
				getClass().getResource("/data/curve/sample-compositecurve-gml32.xml").toURI(),
				true);

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkCompositeCurvePropertyInstance(instance, reference);
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
								.toURI(),
						true);

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
			checkCompositeCurvePropertyInstance(instance, combined);
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
								.toURI(),
						true);

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
			checkCompositeCurvePropertyInstance(instance, separate);
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
				false);

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkCompositeCurvePropertyInstance(instance, referenceOnGrid);
		} finally {
			it.close();
		}
	}

	private void checkCompositeCurvePropertyInstance(Instance instance,
			Geometry referenceGeometry) {
		Object[] geomVals = instance.getProperty(new QName(NS_TEST, "geometry"));
		assertNotNull(geomVals);
		assertEquals(1, geomVals.length);

		Object geom = geomVals[0];
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance, referenceGeometry);
	}

	private void checkGeomInstance(Instance geomInstance, Geometry referenceGeometry) {
		for (GeometryProperty<?> instance : getGeometries(geomInstance)) {
			Geometry geom = instance.getGeometry();
			assertTrue("Read geometry does not match the reference geometry",
					geom.equalsExact(referenceGeometry));
		}
	}

}
