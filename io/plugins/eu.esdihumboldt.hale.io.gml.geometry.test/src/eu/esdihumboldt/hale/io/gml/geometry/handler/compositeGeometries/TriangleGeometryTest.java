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
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Test for reading triangle geometries
 * 
 * @author Patrick Lieb, Arun Varma
 */
public class TriangleGeometryTest extends AbstractHandlerTest {

	private Polygon referencePolygon;
	private Polygon referencePolygonOnGrid;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(0.01, 3.2),
				new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2), new Coordinate(-3.33, -3.2),
				new Coordinate(0.01, 3.2) };

		LinearRing linearRing = geomFactory.createLinearRing(coordinates);
		referencePolygon = geomFactory.createPolygon(linearRing, null);

		// Grid test
		coordinates = new Coordinate[] { new Coordinate(0, 3.2), new Coordinate(3.3, 3.3),
				new Coordinate(0, -3.2), new Coordinate(-3.4, -3.2), new Coordinate(0, 3.2) };

		linearRing = geomFactory.createLinearRing(coordinates);
		referencePolygonOnGrid = geomFactory.createPolygon(linearRing, null);
	}

	/**
	 * Test triangle geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testTriangleGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/sample-triangle-gml32.xml").toURI());

		// two instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkTrianglePropertyPolygon(instance, true);

			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			instance = it.next();
			checkTrianglePropertyPolygon(instance, true);
		} finally {
			it.close();
		}
	}

	/**
	 * Test triangle geometries read from a GML 3.2 file. Geometry coordinates
	 * will be moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testTriangleGml32_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/sample-triangle-gml32.xml").toURI(), false);

		// two instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkTrianglePropertyPolygon(instance, false);

			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			instance = it.next();
			checkTrianglePropertyPolygon(instance, false);
		} finally {
			it.close();
		}
	}

	private void checkTrianglePropertyPolygon(Instance instance, boolean keepOriginal) {
		Object[] geomVals = instance.getProperty(new QName(NS_TEST, "geometry"));
		assertNotNull(geomVals);
		assertEquals(1, geomVals.length);

		Object geom = geomVals[0];
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		assertTrue(geomInstance.getValue() instanceof GeometryProperty<?>);
		@SuppressWarnings("unchecked")
		Polygon polygon = ((GeometryProperty<Polygon>) geomInstance.getValue()).getGeometry();
		assertTrue("Read geometry does not match the reference geometry",
				polygon.equalsExact(keepOriginal ? referencePolygon : referencePolygonOnGrid));
	}

}
