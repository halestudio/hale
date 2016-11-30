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
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Test for reading ring geometries
 * 
 * @author Patrick Lieb, Arun Varma
 */
public class RingGeometryTest extends AbstractHandlerTest {

	private LinearRing reference;
	private LinearRing referenceOnGrid;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(0.01, 3.2),
				new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2), new Coordinate(0.01, 3.2) };
		LineString linestring = geomFactory.createLineString(coordinates);

		reference = geomFactory.createLinearRing(linestring.getCoordinates());

		// for grid test
		coordinates = new Coordinate[] { new Coordinate(0, 3.2), new Coordinate(3.3, 3.3),
				new Coordinate(0, -3.2), new Coordinate(0, 3.2) };
		linestring = geomFactory.createLineString(coordinates);

		referenceOnGrid = geomFactory.createLinearRing(linestring.getCoordinates());

	}

	/**
	 * Test ring geometries read from a GML 2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testRingGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/sample-ring-gml31.xml").toURI());

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// Ring with segments defined through LineStringSegment
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkRingPropertyInstance(instance, true);
		} finally {
			it.close();
		}
	}

	/**
	 * Test ring geometries read from a GML 2 file. Geometry coordinates will be
	 * moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testRingGml31_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/sample-ring-gml31.xml").toURI(), false);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// Ring with segments defined through LineStringSegment
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkRingPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	private void checkRingPropertyInstance(Instance instance, boolean keepOriginal) {
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
