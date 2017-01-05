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
import com.vividsolutions.jts.geom.MultiLineString;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for reading multi line string geometries
 * 
 * @author Patrick Lieb, Arun Varma
 */
@Features("Geometries")
@Stories("GML")
public class MultiLineStringGeometryTest extends AbstractHandlerTest {

	private MultiLineString reference;
	private MultiLineString referenceOnGrid;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] {
				new Coordinate(-39799.68820381, 273207.53980172),
				new Coordinate(-39841.185, 273182.863), new Coordinate(-39882.89, 273153.86) };
		LineString linestring1 = geomFactory.createLineString(coordinates);

		coordinates = new Coordinate[] { new Coordinate(-39799.8, 273207.7),
				new Coordinate(-39841.3, 273182.95), new Coordinate(-39882.99, 273153.99) };
		LineString linestring2 = geomFactory.createLineString(coordinates);

		LineString[] lines = new LineString[] { linestring1, linestring2 };
		reference = geomFactory.createMultiLineString(lines);

		// Grid
		coordinates = new Coordinate[] { new Coordinate(-39799.7, 273207.5),
				new Coordinate(-39841.2, 273182.8), new Coordinate(-39882.9, 273153.9) };
		linestring1 = geomFactory.createLineString(coordinates);

		coordinates = new Coordinate[] { new Coordinate(-39799.8, 273207.8),
				new Coordinate(-39841.3, 273182.9), new Coordinate(-39883.0, 273154.0) };
		linestring2 = geomFactory.createLineString(coordinates);

		lines = new LineString[] { linestring1, linestring2 };
		referenceOnGrid = geomFactory.createMultiLineString(lines);
	}

	/**
	 * Test multi line string geometries read from a GML 2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultiLineStringGml2() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-multilinestring-gml2.xml").toURI());

		// two instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. MultiLineStringProperty with MultiLineString defined through
			// coord
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkMultiLineStringPropertyInstance(instance);

			// 2. MultiLineStringProperty with MultiLineString defined through
			// coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test multi line string geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultiLineStringGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-multilinestring-gml3.xml").toURI());

		// four instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. MultiLineStringProperty with MultiLineString defined through
			// coord
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkMultiLineStringPropertyInstance(instance);

			// 2. MultiLineStringProperty with MultiLineString defined through
			// coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance);

			// 3. MultiLineStringProperty with MultiLineString defined through
			// pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance);

			// 4. MultiLineStringProperty with MultiLineString defined through
			// pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test multi line string geometries read from a GML 2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultiLineStringGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(), getClass()
						.getResource("/data/linestring/sample-multilinestring-gml31.xml").toURI());

		// six instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. MultiLineStringProperty with MultiLineString defined through
			// coord
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkMultiLineStringPropertyInstance(instance);

			// 2. MultiLineStringProperty with MultiLineString defined through
			// coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance);

			// 3. MultiLineStringProperty with MultiLineString defined through
			// pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance);

			// 4. MultiLineStringProperty with MultiLineString defined through
			// pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance);

			// 5. MultiLineStringProperty with MultiLineString defined through
			// pointProperty
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance);

			// 6. MultiLineStringProperty with MultiLineString defined through
			// posList
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	/**
	 * Test multi line string geometries read from a GML 2 file. Geometry
	 * coordinates will be moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultiLineStringGml2_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml2.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-multilinestring-gml2.xml").toURI(),
				false);

		// two instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. MultiLineStringProperty with MultiLineString defined through
			// coord
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);

			// 2. MultiLineStringProperty with MultiLineString defined through
			// coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	/**
	 * Test multi line string geometries read from a GML 3 file. Geometry
	 * coordinates will be moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultiLineStringGml3_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-multilinestring-gml3.xml").toURI(),
				false);

		// four instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. MultiLineStringProperty with MultiLineString defined through
			// coord
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);

			// 2. MultiLineStringProperty with MultiLineString defined through
			// coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);

			// 3. MultiLineStringProperty with MultiLineString defined through
			// pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);

			// 4. MultiLineStringProperty with MultiLineString defined through
			// pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	/**
	 * Test multi line string geometries read from a GML 2 file. Geometry
	 * coordinates will be moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultiLineStringGml31_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/linestring/sample-multilinestring-gml31.xml").toURI(),
				false);

		// six instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. MultiLineStringProperty with MultiLineString defined through
			// coord
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);

			// 2. MultiLineStringProperty with MultiLineString defined through
			// coordinates
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);

			// 3. MultiLineStringProperty with MultiLineString defined through
			// pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);

			// 4. MultiLineStringProperty with MultiLineString defined through
			// pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);

			// 5. MultiLineStringProperty with MultiLineString defined through
			// pointProperty
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);

			// 6. MultiLineStringProperty with MultiLineString defined through
			// posList
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			checkMultiLineStringPropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	private void checkMultiLineStringPropertyInstance(Instance instance) {
		checkMultiLineStringPropertyInstance(instance, true);
	}

	private void checkMultiLineStringPropertyInstance(Instance instance, boolean keepOriginal) {
		Object[] geomVals = instance.getProperty(new QName(NS_TEST, "geometry"));
		assertNotNull(geomVals);
		assertEquals(1, geomVals.length);

		Object geom = geomVals[0];
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance, keepOriginal);
	}

	private void checkGeomInstance(Instance geomInstance, boolean keepOriginal) {
		for (GeometryProperty<?> instance : getGeometries(geomInstance)) {
			@SuppressWarnings("unchecked")
			MultiLineString multilinestring = ((GeometryProperty<MultiLineString>) instance)
					.getGeometry();
			assertTrue("Read geometry does not match the reference geometry",
					multilinestring.equalsExact(keepOriginal ? reference : referenceOnGrid));
		}
	}
}
