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

import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Test for reading polyhedral surface geometries
 * 
 * @author Patrick Lieb
 */
public class PolyhedralSurfaceGeometryTest extends AbstractHandlerTest {

	private MultiPolygon reference;

	@Override
	public void init() {
		super.init();

		LinearRing shell = geomFactory.createLinearRing(new Coordinate[] {
				new Coordinate(0.01, 3.2), new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2),
				new Coordinate(-3.33, -3.2), new Coordinate(0.01, 3.2) });

		LinearRing[] holes = new LinearRing[2];
		LinearRing hole1 = geomFactory.createLinearRing(new Coordinate[] { new Coordinate(0, 1),
				new Coordinate(1, 1), new Coordinate(0, -1), new Coordinate(-1, -1),
				new Coordinate(0, 1) });
		LinearRing hole2 = geomFactory.createLinearRing(new Coordinate[] { new Coordinate(0, 2),
				new Coordinate(2, 2), new Coordinate(0, -2), new Coordinate(-2, -2),
				new Coordinate(0, 2) });
		holes[0] = hole1;
		holes[1] = hole2;

		reference = geomFactory.createMultiPolygon(new Polygon[] { geomFactory.createPolygon(shell,
				holes) });
	}

	/**
	 * Test polygon geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testPolygonGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-polyhedralsurface-gml32.xml").toURI());

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolyhedralSurfaceProperty with LinearRing defined through
			// coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkPolyhedralSurfacePropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	private void checkPolyhedralSurfacePropertyInstance(Instance instance) {
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
			MultiPolygon multipolygon = ((GeometryProperty<MultiPolygon>) instance).getGeometry();
			assertTrue("Read geometry does not match the reference geometry",
					multipolygon.equalsExact(reference));
		}
	}

}
