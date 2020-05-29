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
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.InterpolationConfigurations;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.ReaderConfiguration;
import eu.esdihumboldt.util.svg.test.PaintSettings;
import eu.esdihumboldt.util.svg.test.SVGPainter;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for reading surface geometries
 * 
 * @author Patrick Lieb
 * @author Arun Varma
 * @author Simon Templer
 */
@Features("Geometries")
@Stories("GML")
public class SurfaceGeometryTest extends AbstractHandlerTest {

	private Polygon reference;
	/*
	 * Max positional error must be smaller than the default as we have
	 * geographic coordinates.
	 * 
	 * With a larger value we also end up with double coordinates (which makes
	 * them harder to compare to the reference).
	 */
	private static final double maxPositionalError = 0.002;
	private final ReaderConfiguration gridConfig = InterpolationConfigurations
			.grid(maxPositionalError, true);
	private Consumer<Geometry> checker;
	private Consumer<Geometry> gridChecker;

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

		checker = combine(noCoordinatePairs(), referenceChecker(reference));

		gridChecker = combine(noCoordinatePairs(), referenceChecker(reference, maxPositionalError),
				gridConfig.geometryChecker());
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
			checkSingleGeometry(instance, checker);
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
			checkSingleGeometry(instance, checker);
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
			checkSingleGeometry(instance, checker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Stories("Arcs")
	@Test
	public void testSurfaceArcsGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-surface-arcs.xml").toURI(),
				InterpolationConfigurations.segment(maxPositionalError));

		// three instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			List<GeometryProperty<?>> geoms = new ArrayList<>();

			assertTrue("First sample feature missing", it.hasNext());
			Instance instance1 = it.next();
			geoms.addAll(checkSingleGeometry(instance1, null));

			assertTrue("Second sample feature missing", it.hasNext());
			Instance instance2 = it.next();
			geoms.addAll(checkSingleGeometry(instance2, null));

			assertTrue("Third sample feature missing", it.hasNext());
			Instance instance3 = it.next();
			geoms.addAll(checkSingleGeometry(instance3, null));

			assertEquals("Unexpected number of geometries", 3, geoms.size());

			Geometry geom1 = geoms.get(0).getGeometry();
			Geometry geom2 = geoms.get(1).getGeometry();
			Geometry geom3 = geoms.get(2).getGeometry();

			Envelope envelope = new Envelope();
			envelope.expandToInclude(geom1.getEnvelopeInternal());
			envelope.expandToInclude(geom2.getEnvelopeInternal());
			envelope.expandToInclude(geom3.getEnvelopeInternal());
			PaintSettings settings = new PaintSettings(envelope, 1000, 10);
			SVGPainter svg = new SVGPainter(settings);

			svg.setColor(Color.BLACK);
			svg.drawGeometry(geom1);

			svg.setColor(Color.BLUE);
			svg.drawGeometry(geom2);

			svg.setColor(Color.RED);
			svg.drawGeometry(geom3);

			saveDrawing(svg);

			// ensure that polygons could be created
			assertTrue(Polygon.class.isAssignableFrom(geom1.getClass()));
			assertTrue(Polygon.class.isAssignableFrom(geom2.getClass()));
			assertTrue(Polygon.class.isAssignableFrom(geom3.getClass()));

			// XXX how to test?
			// intersection area cannot be computed
//			double interArea1 = geom1.intersection(geom2).getArea();
//			double interArea2 = geom2.intersection(geom3).getArea();
		} finally {
			it.close();
		}
	}

	/**
	 * Test surface geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Stories("Arcs")
	@Test
	public void testSurfaceArcsGml32_grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-surface-arcs.xml").toURI(),
				gridConfig);

		// three instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			List<GeometryProperty<?>> geoms = new ArrayList<>();

			assertTrue("First sample feature missing", it.hasNext());
			Instance instance1 = it.next();
			geoms.addAll(checkSingleGeometry(instance1, null));

			assertTrue("Second sample feature missing", it.hasNext());
			Instance instance2 = it.next();
			geoms.addAll(checkSingleGeometry(instance2, null));

			assertTrue("Third sample feature missing", it.hasNext());
			Instance instance3 = it.next();
			geoms.addAll(checkSingleGeometry(instance3, null));

			assertEquals("Unexpected number of geometries", 3, geoms.size());

			Geometry geom1 = geoms.get(0).getGeometry();
			Geometry geom2 = geoms.get(1).getGeometry();
			Geometry geom3 = geoms.get(2).getGeometry();

			Envelope envelope = new Envelope();
			envelope.expandToInclude(geom1.getEnvelopeInternal());
			envelope.expandToInclude(geom2.getEnvelopeInternal());
			envelope.expandToInclude(geom3.getEnvelopeInternal());
			PaintSettings settings = new PaintSettings(envelope, 1000, 10);
			SVGPainter svg = new SVGPainter(settings);

			svg.setColor(Color.BLACK);
			svg.drawGeometry(geom1);

			svg.setColor(Color.BLUE);
			svg.drawGeometry(geom2);

			svg.setColor(Color.RED);
			svg.drawGeometry(geom3);

			saveDrawing(svg);

			// ensure that polygons could be created
			assertTrue(Polygon.class.isAssignableFrom(geom1.getClass()));
			assertTrue(Polygon.class.isAssignableFrom(geom2.getClass()));
			assertTrue(Polygon.class.isAssignableFrom(geom3.getClass()));

			// XXX how to test?
//			assertTrue("Geometries intersect", geom1.touches(geom2));
//			assertTrue("Geometries intersect", geom2.touches(geom3));
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
			checkSingleGeometry(instance, referenceChecker(composedPolygon));
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
			checkSingleGeometry(instance, referenceChecker(composed));
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
			checkSingleGeometry(instance, referenceChecker(composed));
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
			checkSingleGeometry(instance, referenceChecker(composed));
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
			checkSingleGeometry(instance, referenceChecker(composedPolygon));
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
				getClass().getResource("/data/surface/sample-surface-gml3.xml").toURI(),
				gridConfig);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, gridChecker);
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
				getClass().getResource("/data/surface/sample-surface-gml31.xml").toURI(),
				gridConfig);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, gridChecker);
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
				getClass().getResource("/data/surface/sample-surface-gml32.xml").toURI(),
				gridConfig);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// PolygonPatch with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, gridChecker);
		} finally {
			it.close();
		}
	}

}
