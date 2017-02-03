/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.geometric.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.File;
import java.nio.file.Path;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.cst.functions.geometric.interiorpoint.InteriorPoint;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.util.svg.test.PaintSettings;
import eu.esdihumboldt.util.svg.test.SVGPainter;

/**
 * Tests checking if a calculated point is inside the original area.
 * 
 * Test polygons created using {@link "http://codepen.io/stempler/full/PPrjMW/"}
 * .
 * 
 * @author Simon Templer
 */
public class InteriorPointTest {

	private final GeometryFactory factory = new GeometryFactory();

	// create test images in temp folder
	private static final boolean GEN_IMAGES = false;

	private static final int MAX_SIZE = 1000;
	private static final int POINT_SIZE = 10;

	/**
	 * Do the interior point calculation for a polygon.
	 * 
	 * @param geometry the geometry
	 * @return the interior point
	 * @throws Exception if an error occurs
	 */
	protected Point calculatePoint(MultiPolygon geometry) throws Exception {
		GeometryProperty<?> prop = InteriorPoint.calculateInteriorPoint(geometry);
		return (Point) prop.getGeometry();
	}

	@SuppressWarnings("javadoc")
	protected void testPointWithin(Polygon geometry) throws Exception {
		testPointWithin(factory.createMultiPolygon(new Polygon[] { geometry }));
	}

	@SuppressWarnings("javadoc")
	protected void testPointWithin(MultiPolygon geometry) throws Exception {
		Point point = null;
		Exception storedException = null;
		try {
			point = calculatePoint(geometry);
		} catch (Exception e) {
			storedException = e;
		}

		if (GEN_IMAGES) {
			/*
			 * Stuff related to SVG commented out because of issues with
			 * dependencies in test product.
			 */

			PaintSettings settings = new PaintSettings(geometry.getEnvelopeInternal(), MAX_SIZE,
					POINT_SIZE);
			SVGPainter g = new SVGPainter(settings);
			Path file = File.createTempFile("pointwithin", ".svg").toPath();

			// draw polygon
			g.setColor(Color.BLACK);
			g.setStroke(2.0f);
			for (int i = 0; i < geometry.getNumGeometries(); i++) {
				Polygon polygon = (Polygon) geometry.getGeometryN(i);
				g.drawPolygon(polygon);
			}

			// draw centroid as reference
			Point centroid = geometry.getCentroid();
			g.setColor(Color.BLUE);
			g.drawPoint(centroid);

			// draw point
			if (point != null) {
				g.setColor(Color.RED);
				g.drawPoint(point);
			}

			g.writeToFile(file);
			System.out.println("Test graphic written to " + file);
		}

		if (storedException != null) {
			throw storedException;
		}

		assertNotNull(point);
		assertTrue("Point is not contained in the polygon", point.within(geometry));
	}

	/**
	 * Test with a simple triangular polygon.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testTriangle() throws Exception {
		LinearRing outer = factory.createLinearRing(new Coordinate[] {
				new Coordinate(49.87401, 8.65491), new Coordinate(49.87318, 8.65606),
				new Coordinate(49.87297, 8.6545), new Coordinate(49.87401, 8.65491) });
		Polygon geom = factory.createPolygon(outer);
		testPointWithin(geom);
	}

	/**
	 * Test with a triangular polygon with a hole in the middle.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testTriangleHole() throws Exception {
		LinearRing outer = factory.createLinearRing(new Coordinate[] {
				new Coordinate(49.87401, 8.65491), new Coordinate(49.87318, 8.65606),
				new Coordinate(49.87297, 8.6545), new Coordinate(49.87401, 8.65491) });
		LinearRing inner = factory.createLinearRing(new Coordinate[] {
				new Coordinate(49.87374, 8.65501), new Coordinate(49.87327, 8.65566),
				new Coordinate(49.87313, 8.65478), new Coordinate(49.87374, 8.65501) });
		Polygon geom = factory.createPolygon(outer, new LinearRing[] { inner });
		testPointWithin(geom);
	}

	/**
	 * Test with a polygon describing a curve.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCurve() throws Exception {
		LinearRing outer = factory
				.createLinearRing(
						new Coordinate[] { new Coordinate(49.87377, 8.65508),
								new Coordinate(49.87369, 8.65529), new Coordinate(49.87356,
										8.65544),
								new Coordinate(49.87349, 8.65549),
								new Coordinate(49.87327, 8.65566), new Coordinate(49.87325, 8.6556),
								new Coordinate(49.8734, 8.65548), new Coordinate(49.87351, 8.6554),
								new Coordinate(49.87357, 8.65532),
								new Coordinate(49.87365, 8.65518),
								new Coordinate(49.87371, 8.65497),
								new Coordinate(49.87376, 8.65444), new Coordinate(49.87385, 8.6545),
								new Coordinate(49.87377, 8.65508) });
		Polygon geom = factory.createPolygon(outer);
		testPointWithin(geom);
	}

	/**
	 * Test with a complex polygon describing a curve.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCurve2() throws Exception {
		LinearRing outer = factory.createLinearRing(new Coordinate[] {
				new Coordinate(49.87377, 8.65508), new Coordinate(49.87369, 8.65529),
				new Coordinate(49.87356, 8.65544), new Coordinate(49.87349, 8.65549),
				new Coordinate(49.87327, 8.65566), new Coordinate(49.87325, 8.6556),
				new Coordinate(49.8734, 8.65548), new Coordinate(49.87351, 8.6554),
				new Coordinate(49.87357, 8.65532), new Coordinate(49.87365, 8.65518),
				new Coordinate(49.87371, 8.65497), new Coordinate(49.87377, 8.65447),
				new Coordinate(49.87371, 8.65431), new Coordinate(49.87356, 8.65424),
				new Coordinate(49.87345, 8.65427), new Coordinate(49.87337, 8.65439),
				new Coordinate(49.87326, 8.65466), new Coordinate(49.87319, 8.65503),
				new Coordinate(49.87324, 8.65518), new Coordinate(49.87332, 8.65519),
				new Coordinate(49.87339, 8.65516), new Coordinate(49.87346, 8.65501),
				new Coordinate(49.87354, 8.65475), new Coordinate(49.87357, 8.65459),
				new Coordinate(49.8736, 8.65441), new Coordinate(49.87368, 8.65449),
				new Coordinate(49.87363, 8.65467), new Coordinate(49.87357, 8.65486),
				new Coordinate(49.87348, 8.65515), new Coordinate(49.87336, 8.6553),
				new Coordinate(49.87325, 8.65539), new Coordinate(49.87312, 8.65513),
				new Coordinate(49.87315, 8.65477), new Coordinate(49.87324, 8.65444),
				new Coordinate(49.87334, 8.65423), new Coordinate(49.87343, 8.65412),
				new Coordinate(49.87358, 8.6541), new Coordinate(49.87377, 8.65413),
				new Coordinate(49.87385, 8.6545), new Coordinate(49.87377, 8.65508) });
		Polygon geom = factory.createPolygon(outer);
		testPointWithin(geom);
	}

	/**
	 * Test with a complex polygon describing a rough building footprint.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFootprint() throws Exception {
		LinearRing outer = factory
				.createLinearRing(
						new Coordinate[] { new Coordinate(49.87367, 8.65714),
								new Coordinate(49.87362, 8.65741), new Coordinate(49.87365,
										8.65758),
								new Coordinate(49.87396, 8.65748),
								new Coordinate(49.87399, 8.65766),
								new Coordinate(49.87362, 8.65778),
								new Coordinate(49.87365, 8.65796), new Coordinate(49.874, 8.65785),
								new Coordinate(49.87409, 8.65856),
								new Coordinate(49.87403, 8.65858), new Coordinate(49.87402, 8.6585),
								new Coordinate(49.87365, 8.65863),
								new Coordinate(49.87362, 8.65874),
								new Coordinate(49.87369, 8.65873), new Coordinate(49.87372,
										8.65907),
								new Coordinate(49.87343, 8.65915),
								new Coordinate(49.87339, 8.65884),
								new Coordinate(49.87356, 8.65874),
								new Coordinate(49.87353, 8.65864),
								new Coordinate(49.87348, 8.65867),
								new Coordinate(49.87339, 8.65805), new Coordinate(49.8736, 8.65797),
								new Coordinate(49.87358, 8.6578),
								new Coordinate(49.87336, 8.65788), new Coordinate(49.87332,
										8.65768),
								new Coordinate(49.87356, 8.65758),
								new Coordinate(49.87354, 8.65745),
								new Coordinate(49.87347, 8.65744),
								new Coordinate(49.87338, 8.65723), new Coordinate(49.8734, 8.65701),
								new Coordinate(49.8736, 8.65698),
								new Coordinate(49.87367, 8.65714) });
		Polygon geom = factory.createPolygon(outer);
		testPointWithin(geom);
	}

	/**
	 * Test with a complex polygon describing a rough building footprint.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFootprint2() throws Exception {
		LinearRing outer = factory
				.createLinearRing(new Coordinate[] { new Coordinate(49.87517, 8.64862),
						new Coordinate(49.87491, 8.64873), new Coordinate(49.87497, 8.64913),
						new Coordinate(49.87521, 8.64904), new Coordinate(49.87525, 8.64924),
						new Coordinate(49.87424, 8.64958), new Coordinate(49.8742, 8.64937),
						new Coordinate(49.87446, 8.64929), new Coordinate(49.87441, 8.6489),
						new Coordinate(49.87414, 8.64897), new Coordinate(49.87411, 8.64877),
						new Coordinate(49.87514, 8.64844), new Coordinate(49.87517, 8.64862) });
		Polygon geom = factory.createPolygon(outer);
		testPointWithin(geom);
	}

	/**
	 * Test with a complex polygon describing a rough building footprint.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFootprint2Hole() throws Exception {
		LinearRing outer = factory
				.createLinearRing(new Coordinate[] { new Coordinate(49.87517, 8.64862),
						new Coordinate(49.87491, 8.64873), new Coordinate(49.87497, 8.64913),
						new Coordinate(49.87521, 8.64904), new Coordinate(49.87525, 8.64924),
						new Coordinate(49.87424, 8.64958), new Coordinate(49.8742, 8.64937),
						new Coordinate(49.87446, 8.64929), new Coordinate(49.87441, 8.6489),
						new Coordinate(49.87414, 8.64897), new Coordinate(49.87411, 8.64877),
						new Coordinate(49.87514, 8.64844), new Coordinate(49.87517, 8.64862) });
		LinearRing inner = factory
				.createLinearRing(new Coordinate[] { new Coordinate(49.87478, 8.6488),
						new Coordinate(49.87482, 8.64914), new Coordinate(49.8746, 8.64924),
						new Coordinate(49.87454, 8.64887), new Coordinate(49.87478, 8.6488) });
		Polygon geom = factory.createPolygon(outer, new LinearRing[] { inner });
		testPointWithin(geom);
	}

	/**
	 * Test with a complex polygon describing a rough building footprint.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testFootprint3() throws Exception {
		LinearRing outer = factory
				.createLinearRing(new Coordinate[] { new Coordinate(49.87494, 8.65267),
						new Coordinate(49.87488, 8.65277), new Coordinate(49.87461, 8.65286),
						new Coordinate(49.87451, 8.65274), new Coordinate(49.87449, 8.65256),
						new Coordinate(49.87457, 8.65225), new Coordinate(49.87483, 8.65217),
						new Coordinate(49.87494, 8.65229), new Coordinate(49.87488, 8.65249),
						new Coordinate(49.8748, 8.65242), new Coordinate(49.87471, 8.65244),
						new Coordinate(49.87466, 8.65252), new Coordinate(49.87468, 8.65265),
						new Coordinate(49.87479, 8.65261), new Coordinate(49.87494, 8.65267) });
		Polygon geom = factory.createPolygon(outer);
		testPointWithin(geom);
	}

	/**
	 * Test with a complex polygon describing streets and a square.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testStreetOuter() throws Exception {
		LinearRing outer = factory
				.createLinearRing(new Coordinate[] { new Coordinate(49.87274, 8.6547),
						new Coordinate(49.87275, 8.65473), new Coordinate(49.87269, 8.65477),
						new Coordinate(49.87261, 8.65486), new Coordinate(49.87257, 8.65489),
						new Coordinate(49.87247, 8.65506), new Coordinate(49.87241, 8.65525),
						new Coordinate(49.87234, 8.65536), new Coordinate(49.87241, 8.65536),
						new Coordinate(49.87247, 8.65539), new Coordinate(49.87248, 8.65542),
						new Coordinate(49.87282, 8.65532), new Coordinate(49.87281, 8.65536),
						new Coordinate(49.87248, 8.65546), new Coordinate(49.87245, 8.65557),
						new Coordinate(49.8724, 8.65561), new Coordinate(49.87235, 8.65562),
						new Coordinate(49.87228, 8.6556), new Coordinate(49.87223, 8.65558),
						new Coordinate(49.87218, 8.65554), new Coordinate(49.87206, 8.65558),
						new Coordinate(49.87205, 8.65556), new Coordinate(49.87219, 8.6555),
						new Coordinate(49.87229, 8.6554), new Coordinate(49.87235, 8.65529),
						new Coordinate(49.8724, 8.65521), new Coordinate(49.87244, 8.65509),
						new Coordinate(49.87252, 8.65494), new Coordinate(49.87258, 8.65483),
						new Coordinate(49.87267, 8.65475), new Coordinate(49.87274, 8.6547) });
		Polygon geom = factory.createPolygon(outer);
		testPointWithin(geom);
	}

	/**
	 * Test with a complex polygon describing streets and a roundabout.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testStreetHole() throws Exception {
		LinearRing outer = factory
				.createLinearRing(new Coordinate[] { new Coordinate(49.87274, 8.6547),
						new Coordinate(49.87275, 8.65473), new Coordinate(49.87269, 8.65477),
						new Coordinate(49.87261, 8.65486), new Coordinate(49.87257, 8.65489),
						new Coordinate(49.87247, 8.65506), new Coordinate(49.87241, 8.65525),
						new Coordinate(49.87234, 8.65536), new Coordinate(49.87241, 8.65536),
						new Coordinate(49.87247, 8.65539), new Coordinate(49.87248, 8.65542),
						new Coordinate(49.87282, 8.65532), new Coordinate(49.87281, 8.65536),
						new Coordinate(49.87248, 8.65546), new Coordinate(49.87245, 8.65557),
						new Coordinate(49.8724, 8.65561), new Coordinate(49.87235, 8.65562),
						new Coordinate(49.87228, 8.6556), new Coordinate(49.87223, 8.65558),
						new Coordinate(49.87218, 8.65554), new Coordinate(49.87206, 8.65558),
						new Coordinate(49.87205, 8.65556), new Coordinate(49.87219, 8.6555),
						new Coordinate(49.87229, 8.6554), new Coordinate(49.87235, 8.65529),
						new Coordinate(49.8724, 8.65521), new Coordinate(49.87244, 8.65509),
						new Coordinate(49.87252, 8.65494), new Coordinate(49.87258, 8.65483),
						new Coordinate(49.87267, 8.65475), new Coordinate(49.87274, 8.6547) });
		LinearRing inner = factory
				.createLinearRing(
						new Coordinate[] { new Coordinate(49.87246, 8.65545),
								new Coordinate(49.87245, 8.65551), new Coordinate(49.87244,
										8.65555),
								new Coordinate(49.87241, 8.65557), new Coordinate(49.87236, 8.6556),
								new Coordinate(49.87232, 8.6556), new Coordinate(49.87225, 8.65556),
								new Coordinate(49.8722, 8.65552), new Coordinate(49.87233, 8.65539),
								new Coordinate(49.87235, 8.65539),
								new Coordinate(49.87238, 8.65539),
								new Coordinate(49.87243, 8.65539),
								new Coordinate(49.87245, 8.65541),
								new Coordinate(49.87246, 8.65545) });
		Polygon geom = factory.createPolygon(outer, new LinearRing[] { inner });
		testPointWithin(geom);
	}

	/**
	 * Test with a polygon describing a rough half moon.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testHalfmoon() throws Exception {
		LinearRing outer = factory
				.createLinearRing(new Coordinate[] { new Coordinate(49.87359, 8.65558),
						new Coordinate(49.87307, 8.65649), new Coordinate(49.87239, 8.65812),
						new Coordinate(49.8724, 8.65586), new Coordinate(49.87294, 8.65431),
						new Coordinate(49.87402, 8.65378), new Coordinate(49.87541, 8.65358),
						new Coordinate(49.87671, 8.65437), new Coordinate(49.87751, 8.65659),
						new Coordinate(49.87683, 8.65548), new Coordinate(49.87583, 8.65489),
						new Coordinate(49.87448, 8.65505), new Coordinate(49.87359, 8.65558) });
		Polygon geom = factory.createPolygon(outer);
		testPointWithin(geom);
	}

	/**
	 * Test with a polygon describing a rough half moon.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testHalfmoonThin() throws Exception {
		LinearRing outer = factory
				.createLinearRing(new Coordinate[] { new Coordinate(49.87343, 8.65491),
						new Coordinate(49.87285, 8.65592), new Coordinate(49.87239, 8.65812),
						new Coordinate(49.8724, 8.65586), new Coordinate(49.87294, 8.65431),
						new Coordinate(49.87402, 8.65378), new Coordinate(49.87541, 8.65358),
						new Coordinate(49.87671, 8.65437), new Coordinate(49.87751, 8.65659),
						new Coordinate(49.87683, 8.65548), new Coordinate(49.87583, 8.65433),
						new Coordinate(49.87469, 8.65438), new Coordinate(49.87343, 8.65491) });
		Polygon geom = factory.createPolygon(outer);
		testPointWithin(geom);
	}

	/**
	 * Test with a multi-polygon.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMulti1() throws Exception {
		LinearRing outer1 = factory
				.createLinearRing(new Coordinate[] { new Coordinate(49.87585, 8.64984),
						new Coordinate(49.87597, 8.65059), new Coordinate(49.87557, 8.65071),
						new Coordinate(49.87545, 8.64993), new Coordinate(49.87585, 8.64984) });
		Polygon poly1 = factory.createPolygon(outer1);

		LinearRing outer2 = factory
				.createLinearRing(new Coordinate[] { new Coordinate(49.87599, 8.6507),
						new Coordinate(49.8761, 8.65147), new Coordinate(49.87568, 8.6516),
						new Coordinate(49.87558, 8.65081), new Coordinate(49.87599, 8.6507) });
		Polygon poly2 = factory.createPolygon(outer2);

		testPointWithin(factory.createMultiPolygon(new Polygon[] { poly1, poly2 }));
	}

	/**
	 * Test with a problem case from real data.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testProblemCase() throws Exception {
		LinearRing outer1 = factory
				.createLinearRing(new Coordinate[] { new Coordinate(466713.482, 5974979.283),
						new Coordinate(466737.125, 5974995.621),
						new Coordinate(466737.125, 5974995.621),
						new Coordinate(467230.558, 5975071.481),
						new Coordinate(467230.558, 5975071.481), new Coordinate(467309.28,
								5975083.867),
						new Coordinate(467309.28, 5975083.867),
						new Coordinate(466776.829, 5975009.807),
						new Coordinate(466776.829, 5975009.807),
						new Coordinate(466742.764, 5974999.507),
						new Coordinate(466713.482, 5974979.283) });
		Polygon poly1 = factory.createPolygon(outer1);

		testPointWithin(factory.createMultiPolygon(new Polygon[] { poly1 }));
	}

}
