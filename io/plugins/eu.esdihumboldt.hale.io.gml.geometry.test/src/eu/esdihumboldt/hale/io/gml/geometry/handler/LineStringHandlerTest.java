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

import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for reading line string geometries
 * 
 * @author Patrick Lieb, Arun Varma
 */
@Features("Geometries")
@Stories("GML")
public class LineStringHandlerTest extends AbstractHandlerTest {

	private LineString reference;
	private LineString referenceOnGrid;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] {
				new Coordinate(-39799.68820381, 273207.53980172),
				new Coordinate(-39841.185, 273182.863), new Coordinate(-39882.89, 273153.86) };
		reference = geomFactory.createLineString(coordinates);

		coordinates = new Coordinate[] { new Coordinate(-39799.7, 273207.5),
				new Coordinate(-39841.2, 273182.8), new Coordinate(-39882.9, 273153.9) };
		referenceOnGrid = geomFactory.createLineString(coordinates);

	}

	/**
	 * Test linestring geometries read from a GML 2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml2() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-linestring-gml2.xml").toURI());

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 2. LineStringProperty with LineString defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 3. LineStringProperty with LineString defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linestring geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-linestring-gml3.xml").toURI());

		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 2. LineStringProperty with LineString defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 3. GeometryProperty with LineString defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);

			// 4. LineStringProperty with LineString defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 5. LineStringProperty with LineString defined through pointRep
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linestring geometries read from a GML 3.1 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-linestring-gml31.xml").toURI());

		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 2. LineStringProperty with LineString defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 3. LineStringProperty with LineString defined through pos
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 4. LineStringProperty with LineString defined through pointRep
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 5. LineStringProperty with LineString defined through
			// pointProperty
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 6. LineStringProperty with LineString defined through posList
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linestring geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-linestring-gml32.xml").toURI());

		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 2. LineStringProperty with LineString defined through pos
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 3. LineStringProperty with LineString defined through pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 4. LineStringProperty with LineString defined through
			// pointProperty
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);

			// 5. LineStringProperty with LineString defined through posList
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linestring geometries read from a GML 2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml2_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-linestring-gml2.xml").toURI(),
				false);

		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 2. LineStringProperty with LineString defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 3. LineStringProperty with LineString defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linestring geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml3_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-linestring-gml3.xml").toURI(),
				false);

		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 2. LineStringProperty with LineString defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 3. GeometryProperty with LineString defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance, false);

			// 4. LineStringProperty with LineString defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 5. LineStringProperty with LineString defined through pointRep
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linestring geometries read from a GML 3.1 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml31_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-linestring-gml31.xml").toURI(),
				false);

		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 2. LineStringProperty with LineString defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 3. LineStringProperty with LineString defined through pos
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 4. LineStringProperty with LineString defined through pointRep
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 5. LineStringProperty with LineString defined through
			// pointProperty
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 6. LineStringProperty with LineString defined through posList
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	/**
	 * Test linestring geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml32_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-linestring-gml32.xml").toURI(),
				false);

		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 2. LineStringProperty with LineString defined through pos
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 3. LineStringProperty with LineString defined through pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 4. LineStringProperty with LineString defined through
			// pointProperty
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);

			// 5. LineStringProperty with LineString defined through posList
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	private void checkGeometryPropertyInstance(Instance instance) {
		checkGeometryPropertyInstance(instance, true);
	}

	private void checkGeometryPropertyInstance(Instance instance, boolean keepOriginal) {
		Collection<Object> geomVals = PropertyResolver.getValues(instance, "LineString", false);
		assertNotNull(geomVals);
		assertEquals(1, geomVals.size());

		Object geom = geomVals.iterator().next();
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance, keepOriginal);
	}

	private void checkLineStringPropertyInstance(Instance instance) {
		checkLineStringPropertyInstance(instance, true);
	}

	private void checkLineStringPropertyInstance(Instance instance, boolean keepOriginal) {
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
		LineString linestring = ((GeometryProperty<LineString>) geomInstance.getValue())
				.getGeometry();
		assertTrue("Read geometry does not match the reference geometry",
				linestring.equalsExact(keepOriginal ? reference : referenceOnGrid));
	}
}
