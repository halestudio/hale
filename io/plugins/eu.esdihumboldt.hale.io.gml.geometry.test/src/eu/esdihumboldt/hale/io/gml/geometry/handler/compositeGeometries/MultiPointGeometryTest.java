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
import com.vividsolutions.jts.geom.MultiPoint;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Test for reading multi point geometries
 * 
 * @author Patrick Lieb
 */
public class MultiPointGeometryTest extends AbstractHandlerTest {

	private MultiPoint reference;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] {
				new Coordinate(-39799.68820381, 273207.53980172),
				new Coordinate(-39841.185, 273182.863), new Coordinate(-39882.89, 273153.86) };

		reference = geomFactory.createMultiPoint(coordinates);
	}

	/**
	 * Test multi point geometries read from a GML 2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultiPointGml2() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/point/sample-multipoint-gml2.xml").toURI());

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. MultiPointProperty with MultiPoint defined through pointMember
			// - coord
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkMultiPointPropertyInstance(instance);

			// 2. MultiPointProperty with MultiPoint defined through pointMember
			// - coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiPointPropertyInstance(instance);

			// 2. MultiPointProperty with MultiPoint defined through
			// pointMembers - coordinates
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiPointPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test multi point geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultiPointGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/point/sample-multipoint-gml3.xml").toURI());

		// four instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. MultiPointProperty with MultiPoint defined through pointMember
			// - coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkMultiPointPropertyInstance(instance);

			// 2. MultiPointProperty with MultiPoint defined through
			// pointMembers
			// - coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiPointPropertyInstance(instance);

			// 3. MultiPointProperty with MultiPoint defined through
			// pointMembers - coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiPointPropertyInstance(instance);

			// 4. MultiPointProperty with MultiPoint defined through
			// pointMembers - pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiPointPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test multi point geometries read from a GML 31 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultiPointGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/point/sample-multipoint-gml31.xml").toURI());

		// four instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. MultiPointProperty with MultiPoint defined through pointMember
			// - coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkMultiPointPropertyInstance(instance);

			// 2. MultiPointProperty with MultiPoint defined through pointMember
			// - coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiPointPropertyInstance(instance);

			// 3. MultiPointProperty with MultiPoint defined through
			// pointMembers - coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiPointPropertyInstance(instance);

			// 4. MultiPointProperty with MultiPoint defined through
			// pointMembers - pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiPointPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test multi point geometries read from a GML 32 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultiPointGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/point/sample-multipoint-gml32.xml").toURI());

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. MultiPointProperty with MultiPoint defined through pointMember
			// - coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkMultiPointPropertyInstance(instance);

			// 2. MultiPointProperty with MultiPoint defined through pointMember
			// - coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiPointPropertyInstance(instance);

			// 3. MultiPointProperty with MultiPoint defined through
			// pointMembers - pos
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiPointPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	private void checkMultiPointPropertyInstance(Instance instance) {
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
			MultiPoint multipoint = ((GeometryProperty<MultiPoint>) instance).getGeometry();
			assertTrue("Read geometry does not match the reference geometry",
					multipoint.equalsExact(reference));
		}
	}

}
