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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;
import eu.esdihumboldt.util.svg.test.PaintSettings;
import eu.esdihumboldt.util.svg.test.SVGTestPainter;

/**
 * Test for reading surface geometries
 * 
 * @author Patrick Lieb, Arun Varma
 */
public class SurfaceGeometryTest extends AbstractHandlerTest {

	private static final boolean GEN_IMAGES = false;

	private Polygon reference;
	private Polygon referenceOnGrid;

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest#init()
	 */
	@Override
	public void init() {
		super.init();

		LinearRing shell = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(-122.44, 37.80),
						new Coordinate(-122.45, 37.80), new Coordinate(-122.45, 37.78),
						new Coordinate(-122.44, 37.78), new Coordinate(-122.44, 37.80) });

		LinearRing[] holes = new LinearRing[1];
		LinearRing hole1 = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(-122.24, 37.60),
						new Coordinate(-122.25, 37.60), new Coordinate(-122.25, 37.58),
						new Coordinate(-122.24, 37.58), new Coordinate(-122.24, 37.60) });
		holes[0] = hole1;

		reference = geomFactory.createPolygon(shell, holes);

		// grid
		shell = geomFactory.createLinearRing(new Coordinate[] { new Coordinate(-122.44, 37.80),
				new Coordinate(-122.46, 37.80), new Coordinate(-122.46, 37.78),
				new Coordinate(-122.44, 37.78), new Coordinate(-122.44, 37.80) });

		holes = new LinearRing[1];
		hole1 = geomFactory.createLinearRing(new Coordinate[] { new Coordinate(-122.24, 37.6),
				new Coordinate(-122.25, 37.59), new Coordinate(-122.25, 37.57),
				new Coordinate(-122.24, 37.58), new Coordinate(-122.24, 37.6) });
		holes[0] = hole1;

		referenceOnGrid = geomFactory.createPolygon(shell, holes);
	}

	/**
	 * Test surface geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/surface/sample-surface-gml3.xml").toURI());

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, reference);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometries read from a GML 3.1 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/surface/sample-surface-gml31.xml").toURI());

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, reference);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-surface-gml32.xml").toURI());

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, reference);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceArcsGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-surface-arcs.xml").toURI());

		// three instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			List<Geometry> geoms = new ArrayList<>();

			assertTrue("First sample feature missing", it.hasNext());
			Instance instance1 = it.next();
			geoms.addAll(checkSurfacePropertyInstance(instance1, x -> {
				/**/ }));

			assertTrue("Second sample feature missing", it.hasNext());
			Instance instance2 = it.next();
			geoms.addAll(checkSurfacePropertyInstance(instance2, x -> {
				/**/ }));

			assertTrue("Third sample feature missing", it.hasNext());
			Instance instance3 = it.next();
			geoms.addAll(checkSurfacePropertyInstance(instance3, x -> {
				/**/ }));

			assertEquals("Unexpected number of geometries", 3, geoms.size());

			Geometry geom1 = geoms.get(0);
			Geometry geom2 = geoms.get(1);
			Geometry geom3 = geoms.get(2);

			if (GEN_IMAGES) {
				Envelope envelope = new Envelope();
				envelope.expandToInclude(geom1.getEnvelopeInternal());
				envelope.expandToInclude(geom2.getEnvelopeInternal());
				envelope.expandToInclude(geom3.getEnvelopeInternal());
				PaintSettings settings = new PaintSettings(envelope, 1000, 10);
				SVGTestPainter svg = new SVGTestPainter(settings);

				svg.setColor(Color.BLACK);
				svg.drawGeometry(geom1);

				svg.setColor(Color.BLUE);
				svg.drawGeometry(geom2);

				svg.setColor(Color.RED);
				svg.drawGeometry(geom3);

				svg.writeAndOpenFile();
			}

			// interpolated geometries should not intersect (XXX verify)
			assertTrue("Geometries intersect", geom1.touches(geom2));
			assertTrue("Geometries intersect", geom2.touches(geom3));

			// TODO more checks?
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometry consisting of multiple patches read from a GML 3.2
	 * file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml32_patches() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-surface-gml32_patches.xml").toURI());

		LinearRing shell = geomFactory.createLinearRing(
				new Coordinate[] { new Coordinate(-4.5, 3), new Coordinate(0.5, 4.5),
						new Coordinate(5, 3), new Coordinate(8.5, 2), new Coordinate(3, -4.5),
						new Coordinate(1, 1), new Coordinate(-3, -1), new Coordinate(-4.5, 3) });
		Polygon composedPolygon = geomFactory.createPolygon(shell);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, composedPolygon);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometry consisting of multiple patches (only touching each
	 * other at one point) read from a GML 3.2 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml32_patches_touch() throws Exception {
		InstanceCollection instances = AbstractHandlerTest
				.loadXMLInstances(getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
						getClass()
								.getResource("/data/surface/sample-surface-gml32_patches_touch.xml")
								.toURI());

		Polygon polygon1 = geomFactory
				.createPolygon(new Coordinate[] { new Coordinate(-4.5, 3), new Coordinate(0.5, 4.5),
						new Coordinate(5, 3), new Coordinate(-3, -1), new Coordinate(-4.5, 3) });
		Polygon polygon2 = geomFactory
				.createPolygon(new Coordinate[] { new Coordinate(5, 3), new Coordinate(10, 5),
						new Coordinate(8.5, 2), new Coordinate(3, -4.5), new Coordinate(5, 3) });
		MultiPolygon composed = geomFactory
				.createMultiPolygon(new Polygon[] { polygon1, polygon2 });

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, composed);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometry consisting of multiple 3D patches read from a GML
	 * 3.2 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml32_patches_3d() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(), getClass()
						.getResource("/data/surface/sample-surface-gml32_patches_3d.xml").toURI());

		Polygon polygon1 = geomFactory.createPolygon(new Coordinate[] { new Coordinate(-4.5, 3, 0),
				new Coordinate(0.5, 4.5, 0), new Coordinate(5, 3, 1), new Coordinate(-3, -1, 1),
				new Coordinate(-4.5, 3, 0) });
		Polygon polygon2 = geomFactory.createPolygon(new Coordinate[] { new Coordinate(1, 1, 1),
				new Coordinate(5, 3, 1), new Coordinate(8.5, 2, 0), new Coordinate(3, -4.5, 0),
				new Coordinate(1, 1, 1) });
		MultiPolygon composed = geomFactory
				.createMultiPolygon(new Polygon[] { polygon1, polygon2 });

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, composed);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometry consisting of multiple 3D patches read from a GML
	 * 3.2 file. The patches cannot be combined to a single polygon.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml32_patches_3d_mismatch() throws Exception {
		InstanceCollection instances = AbstractHandlerTest
				.loadXMLInstances(getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
						getClass()
								.getResource(
										"/data/surface/sample-surface-gml32_patches_3d_mismatch.xml")
								.toURI());

		Polygon polygon1 = geomFactory.createPolygon(new Coordinate[] { new Coordinate(-4.5, 3, 0),
				new Coordinate(0.5, 4.5, 0), new Coordinate(5, 3, 1), new Coordinate(-3, -1, 0),
				new Coordinate(-4.5, 3, 0) });
		Polygon polygon2 = geomFactory.createPolygon(new Coordinate[] { new Coordinate(1, 1, 1),
				new Coordinate(5, 3, 1), new Coordinate(8.5, 2, 0), new Coordinate(3, -4.5, 0),
				new Coordinate(1, 1, 1) });
		MultiPolygon composed = geomFactory
				.createMultiPolygon(new Polygon[] { polygon1, polygon2 });

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, composed);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometry consisting of multiple patches (including holes)
	 * read from a GML 3.2 file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml32_patches_hole() throws Exception {
		InstanceCollection instances = AbstractHandlerTest
				.loadXMLInstances(getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
						getClass()
								.getResource("/data/surface/sample-surface-gml32_patches_hole.xml")
								.toURI());

		LinearRing shell = geomFactory.createLinearRing(
				new Coordinate[] { new Coordinate(-4.5, 3), new Coordinate(0.5, 4.5),
						new Coordinate(5, 3), new Coordinate(8.5, 2), new Coordinate(3, -4.5),
						new Coordinate(1, 1), new Coordinate(-3, -1), new Coordinate(-4.5, 3) });
		LinearRing[] holes = new LinearRing[] { geomFactory.createLinearRing(new Coordinate[] {
				new Coordinate(3, 0.5), new Coordinate(4, -2), new Coordinate(6.5, 1.5),
				new Coordinate(4.5, 2), new Coordinate(3, 0.5) }) };
		Polygon composedPolygon = geomFactory.createPolygon(shell, holes);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, composedPolygon);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometries read from a GML 3 file. Geometry coordinates will
	 * be moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml3_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/surface/sample-surface-gml3.xml").toURI(), false,
				0.01);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, referenceOnGrid);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometries read from a GML 3.1 file. Geometry coordinates
	 * will be moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml31_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/surface/sample-surface-gml31.xml").toURI(), false,
				0.01);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, referenceOnGrid);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometries read from a GML 3.2 file. Geometry coordinates
	 * will be moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSurfaceGml32_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-surface-gml32.xml").toURI(), false,
				0.01);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance, referenceOnGrid);
		} finally {
			it.close();
		}
	}

	private Collection<Geometry> checkSurfacePropertyInstance(Instance instance,
			Geometry referenceGeometry) {
		return checkSurfacePropertyInstance(instance,
				geometry -> assertTrue("Read geometry does not match the reference geometry",
						geometry.equalsExact(referenceGeometry)));
	}

	private Collection<Geometry> checkSurfacePropertyInstance(Instance instance,
			Consumer<Geometry> checker) {
		Object[] geomVals = instance.getProperty(new QName(NS_TEST, "geometry"));
		assertNotNull(geomVals);
		assertEquals(1, geomVals.length);

		Object geom = geomVals[0];
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		return checkGeomInstance(geomInstance, checker);
	}

	private Collection<Geometry> checkGeomInstance(Instance geomInstance,
			Consumer<Geometry> checker) {
		List<Geometry> geoms = new ArrayList<>();
		for (GeometryProperty<?> instance : getGeometries(geomInstance)) {
			Geometry geometry = instance.getGeometry();
			geoms.add(geometry);
			if (checker != null) {
				checker.accept(geometry);
			}
		}
		return geoms;
	}

}
