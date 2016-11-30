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
import com.vividsolutions.jts.geom.MultiPoint;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Test for reading envelope geometries
 * 
 * @author Patrick Lieb, Arun Varma
 */
public class EnvelopeHandlerTest extends AbstractHandlerTest {

	private MultiPoint reference;
	private MultiPoint referenceOnGrid;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] {
				new Coordinate(-39799.68820381, 273207.53980172),
				new Coordinate(-39841.185, 273182.863) };
		reference = geomFactory.createMultiPoint(coordinates);

		coordinates = new Coordinate[] { new Coordinate(-39799.7, 273207.5),
				new Coordinate(-39841.2, 273182.8) };
		referenceOnGrid = geomFactory.createMultiPoint(coordinates);

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
				getClass().getResource("/data/envelope/sample-envelope-gml3.xml").toURI(), true);

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. EnvelopeProperty defined through pos
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkEnvelopePropertyInstance(instance, true);
		} finally {
			it.close();
		}
	}

	/**
	 * Test envelope geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testEnvelopeGml3_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/envelope/sample-envelope-gml3.xml").toURI(), false);

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. EnvelopeProperty defined through pos
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkEnvelopePropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	private void checkEnvelopePropertyInstance(Instance instance, boolean keepOriginal) {
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
		MultiPoint multipoint = ((GeometryProperty<MultiPoint>) geomInstance.getValue())
				.getGeometry();
		assertTrue("Read geometry does not match the reference geometry",
				multipoint.equalsExact(keepOriginal ? reference : referenceOnGrid));
	}

}
