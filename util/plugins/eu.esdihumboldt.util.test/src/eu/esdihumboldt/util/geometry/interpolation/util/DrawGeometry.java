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

package eu.esdihumboldt.util.geometry.interpolation.util;

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
import com.vividsolutions.jts.geom.LineString;

/**
 * Draw interpolated geometry
 * 
 * @author Arun
 */
public class DrawGeometry {

	private static final int MAX_SIZE = 200;

	/**
	 * Draw image
	 * 
	 * @param geometry interpolated LineString geometry
	 * @param originalCoordinates original coordinates
	 * @param imageIndex image index
	 */
	public static void drawImage(LineString geometry, Coordinate[] originalCoordinates,
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

	private static void drawLineString(Graphics2D g, LineString geometry, double minX, double minY,
			double factor) {
		Coordinate[] coordinates = geometry.getCoordinates();
		List<java.awt.geom.Line2D> lines = createLineString(coordinates, minX, minY, factor);

		for (java.awt.geom.Line2D line : lines) {
			g.draw(line);
		}
	}

	private static List<java.awt.geom.Line2D> createLineString(Coordinate[] coordinates,
			double minX, double minY, double factor) {
		List<java.awt.geom.Line2D> results = new ArrayList<>();
		for (int i = 0; i < coordinates.length - 1; i++) {
			results.add(new Line2D.Double((coordinates[i].x - minX) * factor,
					(coordinates[i].y - minY) * factor, (coordinates[i + 1].x - minX) * factor,
					(coordinates[i + 1].y - minY) * factor));
		}

		return results;
	}

	private static void drawPoints(Graphics2D g, Coordinate[] coordinates, double minX, double minY,
			double factor) {
		for (int i = 0; i < coordinates.length; i++) {
			Line2D point = new Line2D.Double((coordinates[i].x - minX) * factor,
					(coordinates[i].y - minY) * factor, (coordinates[i].x - minX) * factor,
					(coordinates[i].y - minY) * factor);
			g.draw(point);
		}
	}

}
