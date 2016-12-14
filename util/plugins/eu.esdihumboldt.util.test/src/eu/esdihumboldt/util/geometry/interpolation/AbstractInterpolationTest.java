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
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * Abstract interpolation test
 * 
 * @author Arun
 */
public abstract class AbstractInterpolationTest {

	private int skipCount = 0;
	private final int MAX_SIZE = 200;

	/**
	 * validate neighbor coordinates must not same
	 * 
	 * @param geom geometry
	 */
	protected void checkNeighbourCoordinates(Geometry geom) {
		Coordinate[] coordinates = geom.getCoordinates();
		for (int i = 1; i < coordinates.length; i++) {
			assertNotEquals("should not match neighbour coordinates", coordinates[i],
					coordinates[i - 1]);
		}
	}

	/**
	 * print geometry coordinates
	 * 
	 * @param geom geometry
	 */
	protected void printCoordinates(Geometry geom) {
		System.out.println(geom.getCoordinates().length);
		System.out.println("");
		for (Coordinate coordinate : geom.getCoordinates())
			System.out.print("new Coordinate(" + coordinate.x + "," + coordinate.y + "), ");
		System.out.println("");
	}

	/**
	 * validate coordinate should be on grid
	 *
	 * @param generatedGeom geometry
	 * @param rawCoordinatesLength no of raw coordinates
	 * @param maxPosError max positional error
	 * @param keepOriginal keep original
	 *
	 */
	protected void validateCoordinatesOnGrid(Geometry generatedGeom, int rawCoordinatesLength,
			double maxPosError, boolean keepOriginal) {

		Coordinate[] coordinates = generatedGeom.getCoordinates();

		for (Coordinate testCoordinate : coordinates) {
			Coordinate actualGridPoint = Interpolation.pointToGrid(testCoordinate, maxPosError);

			assertTrue(checkOnGrid(testCoordinate, actualGridPoint, rawCoordinatesLength,
					keepOriginal));
		}
	}

	private boolean checkOnGrid(Coordinate expected, Coordinate actual, int noOfSkip,
			boolean keepOriginal) {

		boolean check = actual.equals(expected);
		if (!check) {
			if (keepOriginal)
				skipCount++;
		}
		return (skipCount <= noOfSkip);
	}

	/**
	 * Draw image
	 * 
	 * @param geometry interpolated LineString geometry
	 * @param originalCoordinates original coordinates
	 * @param imageIndex image index
	 */
	protected void drawImage(LineString geometry, Coordinate[] originalCoordinates,
			int imageIndex) {

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
		drawPoints(g, originalCoordinates, minX, minY, factor);

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
