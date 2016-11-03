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

package eu.esdihumboldt.util.geometry;

import static org.junit.Assert.assertNotNull;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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

import eu.esdihumboldt.util.geometry.interpolation.Interpolation;

/**
 * Test for the Interpolation of arc algorithm
 * 
 * @author Arun
 */
@RunWith(Parameterized.class)
public class InterpolationTest {

	private final Coordinate[] arcCoordinates;
	@SuppressWarnings("rawtypes")
	private final Class generatedGeometryType;

	private static final int MAX_SIZE = 200;

	private static final boolean DRAW_IMAGE = false;

	/**
	 * Constructor for parameterized test
	 * 
	 * @param coordinates input arc coordinates
	 * @param geometry type of output geometry
	 */
	@SuppressWarnings("rawtypes")
	public InterpolationTest(Coordinate[] coordinates, Class geometry) {
		this.arcCoordinates = coordinates;
		this.generatedGeometryType = geometry;
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
				{ new Coordinate[] { new Coordinate(577869.169, 5917253.678),
						new Coordinate(577871.772, 5917250.386),
						new Coordinate(577874.884, 5917253.202) }, //
						LineString.class }, //
				{ new Coordinate[] { new Coordinate(577738.2, 5917351.786),
						new Coordinate(577740.608, 5917347.876),
						new Coordinate(577745.185, 5917348.135) }, //
						LineString.class }, //
				{ new Coordinate[] { new Coordinate(240, 280), new Coordinate(210, 150),
						new Coordinate(300, 100) }, //
						LineString.class }, //
				{ new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 16),
						new Coordinate(16, 8) }, //
						LineString.class }, //
				{ new Coordinate[] { new Coordinate(8, 8), new Coordinate(12, 6.5),
						new Coordinate(16, 8) }, //
						LineString.class }, //
				{ new Coordinate[] { new Coordinate(3, 10.5), new Coordinate(4, 7.75),
						new Coordinate(8, 8) }, //
						LineString.class } //
		});
	}

	/**
	 * test algorithm
	 */
	@Test
	public void testInterpolation() {
		System.out.println("-- Test begin --");
		Geometry generatedGeometry = Interpolation.interpolateArc(arcCoordinates, 0.025);

		Assert.assertEquals(generatedGeometry.getClass(), generatedGeometryType);

		System.out.println(generatedGeometry.getCoordinates().length);
		System.out.println(generatedGeometry);
		assertNotNull(generatedGeometry);

		if (DRAW_IMAGE)
			createImage((LineString) generatedGeometry, arcCoordinates);

	}

	private void createImage(LineString geometry, Coordinate[] arcCoordinates) {

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
			Path file = File.createTempFile("LineStringWithArc", ".png").toPath();
			ImageIO.write(bim, "PNG", file.toFile());
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
