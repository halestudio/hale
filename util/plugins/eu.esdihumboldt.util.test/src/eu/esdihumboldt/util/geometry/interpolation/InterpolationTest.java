/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.util.geometry.interpolation;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * Test for the Interpolation of arc algorithm
 * 
 * @author Arun
 */
@RunWith(Parameterized.class)
public class InterpolationTest {

	private final int testIndex;
	private final Coordinate[] arcCoordinates;
	@SuppressWarnings("rawtypes")
	private final Class generatedGeometryType;
	private final boolean skipTest;

	private static final int MAX_SIZE = 200;
	private static final double e = 0.025;

	private static final boolean SKIP_TEST = false;

	private static final boolean DRAW_IMAGE = false;

	/**
	 * Constructor for parameterized test
	 * 
	 * @param testIndex Index of test parameters
	 * @param coordinates input arc coordinates
	 * @param geometry type of output geometry
	 * @param skipTest if wants to skip test
	 */
	@SuppressWarnings("rawtypes")
	public InterpolationTest(int testIndex, Coordinate[] coordinates, Class geometry,
			boolean skipTest) {
		this.testIndex = testIndex;
		this.arcCoordinates = coordinates;
		this.generatedGeometryType = geometry;
		this.skipTest = skipTest;
	}

	/**
	 * Passing arc geometries
	 * 
	 * @return Collection of arc coordinates and type of generated geometry
	 */
	@SuppressWarnings("rawtypes")
	@Parameters
	public static Collection addCoordiantes() {
		return Arrays.asList(new Object[][] { //
				{ 0, new Coordinate[] { new Coordinate(577869.169, 5917253.678),
						new Coordinate(577871.772, 5917250.386),
						new Coordinate(577874.884, 5917253.202) }, //
						LineString.class, SKIP_TEST }, //
				{ 1, new Coordinate[] { new Coordinate(577738.2, 5917351.786),
						new Coordinate(577740.608, 5917347.876),
						new Coordinate(577745.185, 5917348.135) }, //
						LineString.class, SKIP_TEST }, //
				{ 2, new Coordinate[] { new Coordinate(240, 280), new Coordinate(210, 150),
						new Coordinate(300, 100) }, //
						LineString.class, SKIP_TEST }, //
				{ 3, new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 16),
						new Coordinate(16, 8) }, //
						LineString.class, SKIP_TEST }, //
				{ 4, new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 6.5),
						new Coordinate(16, 8) }, //
						LineString.class, SKIP_TEST }, //
				{ 5, new Coordinate[] { new Coordinate(3, 10.5), new Coordinate(4, 7.75),
						new Coordinate(8, 8) }, //
						LineString.class, SKIP_TEST }, //
				{ 6, new Coordinate[] { new Coordinate(353248.457, 5531386.407),
						new Coordinate(353249.438, 5531386.407),
						new Coordinate(353250.399, 5531386.217) }, //
						LineString.class, SKIP_TEST }, //
				{ 7, new Coordinate[] { new Coordinate(351141.396, 5532140.355),
						new Coordinate(351110.659, 5532137.542),
						new Coordinate(351080.17, 5532132.742) }, //
						LineString.class, SKIP_TEST }, //
				{ 8, new Coordinate[] { new Coordinate(350925.682, 5532108.264),
						new Coordinate(350848.556, 5532095.285),
						new Coordinate(350771.515, 5532081.814) }, //
						LineString.class, SKIP_TEST }, //
				{ 9, new Coordinate[] { new Coordinate(351080.17, 5532132.742),
						new Coordinate(351002.887, 5532120.75),
						new Coordinate(350925.682, 5532108.264) }, //
						LineString.class, SKIP_TEST }, //
				{ 10, new Coordinate[] { new Coordinate(0, 5), new Coordinate(-5, 0),
						new Coordinate(0, -5) }, //
						LineString.class, SKIP_TEST }, //
				{ 11, new Coordinate[] { new Coordinate(353297.973, 5531361.379),
						new Coordinate(353298.192, 5531360.429),
						new Coordinate(353298.503, 5531359.504) }, //
						LineString.class, SKIP_TEST } //

		});
	}

//353297.973 5531361.379 353298.192 5531360.429 353298.503 5531359.504
	/**
	 * test algorithm
	 */
	@Test
	public void testInterpolation() {
		System.out.println("-- Test-" + testIndex + " begin --");
		if (skipTest) {
			System.out.println("Test is configured to skip");
			return;
		}
		Interpolation<LineString> interpolation = new ArcInterpolation(this.arcCoordinates, e);
		Geometry interpolatedArc = interpolation.interpolateRawGeometry();

		assertNotNull(interpolatedArc);
		Assert.assertEquals(interpolatedArc.getClass(), generatedGeometryType);

		Coordinate[] coordinates = interpolatedArc.getCoordinates();
		for (int i = 1; i < coordinates.length; i++) {
			assertNotEquals("should not match neighbour coordinates", coordinates[i],
					coordinates[i - 1]);
		}
		// System.out.println(interpolatedArc.getCoordinates().length);
		// System.out.println(interpolatedArc);
		if (DRAW_IMAGE)
			createImage((LineString) interpolatedArc, arcCoordinates, testIndex);

	}

	private void createImage(LineString geometry, Coordinate[] arcCoordinates, int imageIndex) {

		Envelope envelope = geometry.getEnvelopeInternal();
		int height;
		int width;
		double factor;
		if (envelope.getHeight() > envelope.getWidth()) {
			height = MAX_SIZE;
			width = (int) Math.ceil(height * envelope.getWidth() / envelope.getHeight());
			factor = height / envelope.getHeight();
		}
		else {
			width = MAX_SIZE;
			height = (int) Math.ceil(width * envelope.getHeight() / envelope.getWidth());
			factor = width / envelope.getWidth();
		}
		double minX = envelope.getMinX();
		double minY = envelope.getMinY();

		// create Graphics
		BufferedImage bim = new BufferedImage(width + 100, height + 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bim.createGraphics();

		// draw LineString
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2.0f));
		drawLineString(g, geometry, minX, minY, factor);

		// draw arc Coordinates
		g.setColor(Color.RED);
		drawPoints(g, arcCoordinates, minX, minY, factor);

		// Dispose the Graphics2D object
		g.dispose();

		try {
			// Write the BufferedImage object to a file
			File image = File.createTempFile("test" + imageIndex + "-", ".png");
			ImageIO.write(bim, "PNG", image);
		} catch (IOException e) {
			//
		}
	}

	private void drawLineString(Graphics2D g, LineString geometry, double minX, double minY,
			double factor) {
		Coordinate[] coordinates = geometry.getCoordinates();
		List<java.awt.geom.Line2D> lines = createLineString(coordinates, minX, minY, factor);

		for (java.awt.geom.Line2D line : lines) {
			g.draw(line);
		}
	}

	private List<java.awt.geom.Line2D> createLineString(Coordinate[] coordinates, double minX,
			double minY, double factor) {
		List<java.awt.geom.Line2D> results = new ArrayList<>();
		for (int i = 0; i < coordinates.length - 1; i++) {
			results.add(new Line2D.Double((coordinates[i].x - minX) * factor,
					(coordinates[i].y - minY) * factor, (coordinates[i + 1].x - minX) * factor,
					(coordinates[i + 1].y - minY) * factor));
		}

		return results;
	}

	private void drawPoints(Graphics2D g, Coordinate[] coordinates, double minX, double minY,
			double factor) {
		for (int i = 0; i < coordinates.length; i++) {
			Line2D point = new Line2D.Double((coordinates[i].x - minX) * factor,
					(coordinates[i].y - minY) * factor, (coordinates[i].x - minX) * factor,
					(coordinates[i].y - minY) * factor);
			g.draw(point);
		}
	}

}
