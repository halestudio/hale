/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.util.svg.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Helper for painting (geometries) to an SVG image.
 * 
 * @author Simon Templer
 */
public class SVGPainter {

	private final PaintSettings settings;
	private final SVGGraphics2D g;

	/**
	 * Create a new painter.
	 * 
	 * @param settings the paint settings
	 */
	public SVGPainter(PaintSettings settings) {
		this.settings = settings;
		this.g = createSVGGraphics();

		if (settings.getCanvasSize() != null) {
			g.setSVGCanvasSize(settings.getCanvasSize());
		}
	}

	/**
	 * Set the canvas size for the SVG image.
	 * 
	 * @param width the width
	 * @param height the height
	 */
	public void setCanvasSize(int width, int height) {
		g.setSVGCanvasSize(new Dimension(width, height));
	}

	private SVGGraphics2D createSVGGraphics() {
		// Get a DOMImplementation.
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);

		// Create an instance of the SVG Generator.
		return new SVGGraphics2D(document);
	}

	/**
	 * Write the graphics to a SVG file.
	 * 
	 * @param file the file to write to
	 * @throws IOException if an error occurs writing the file
	 */
	public void writeToFile(Path file) throws IOException {
		// Finally, stream out SVG to the standard output using
		// UTF-8 encoding.
		boolean useCSS = true; // we want to use CSS style attributes
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			g.stream(writer, useCSS);
		}
	}

	/**
	 * Write the SVG to a string.
	 * 
	 * @return the SVG as string
	 * @throws IOException if creating the string representation fails
	 */
	public String writeToString() throws IOException {
		boolean useCSS = true; // we want to use CSS style attributes
		StringWriter writer = new StringWriter();
		g.stream(writer, useCSS);
		return writer.toString();
	}

	/**
	 * Draw a geometry.
	 * 
	 * @param geometry the geometry to draw
	 */
	public void drawGeometry(Geometry geometry) {
		if (geometry == null || geometry.isEmpty()) {
			return;
		}
		if (geometry.getNumGeometries() > 1) {
			for (int i = 0; i < geometry.getNumGeometries(); i++) {
				drawGeometry(geometry.getGeometryN(i));
			}
		}
		else {
			if (geometry instanceof Polygon) {
				drawPolygon((Polygon) geometry);
			}
			else if (geometry instanceof LineString) {
				drawLineString((LineString) geometry);
			}
			else if (geometry instanceof Point) {
				drawPoint((Point) geometry);
			}
			else {
				throw new IllegalArgumentException(
						"Cannot draw geometry of type " + geometry.getClass().getName());
			}
		}
	}

	/**
	 * Draw a line string.
	 * 
	 * @param geometry the line string geometry
	 */
	public void drawLineString(LineString geometry) {
		Coordinate[] coords = geometry.getCoordinates();
		if (coords.length >= 2) {
			for (int i = 0; i < coords.length - 1; i++) {
				g.drawLine( //
						settings.convertX(coords[i].x), //
						settings.convertY(coords[i].y), //
						settings.convertX(coords[i + 1].x), //
						settings.convertY(coords[i + 1].y));
			}
		}
	}

	/**
	 * Draw a line between two points.
	 * 
	 * @param p1 the first point
	 * @param p2 the second point
	 */
	public void drawLine(Coordinate p1, Coordinate p2) {
		g.drawLine( //
				settings.convertX(p1.x), //
				settings.convertY(p1.y), //
				settings.convertX(p2.x), //
				settings.convertY(p2.y));
	}

	/**
	 * Draw a polygon.
	 * 
	 * @param geometry the polygon geometry
	 */
	public void drawPolygon(Polygon geometry) {
		// exterior
		Coordinate[] coordinates = geometry.getExteriorRing().getCoordinates();
		java.awt.Polygon outerPolygon = createPolygon(coordinates);

		if (geometry.getNumInteriorRing() > 0) {
			// polygon has interior geometries

			java.awt.geom.Area drawArea = new java.awt.geom.Area(outerPolygon);

			// interior
			for (int i = 0; i < geometry.getNumInteriorRing(); i++) {
				LineString interior = geometry.getInteriorRingN(i);
				java.awt.Polygon innerPolygon = createPolygon(interior.getCoordinates());
				drawArea.subtract(new java.awt.geom.Area(innerPolygon));
			}

			g.draw(drawArea);
		}
		else {
			// polygon has no interior
			// use polygon instead of Area for painting, as painting small
			// Areas sometimes produces strange results (some are not
			// visible)
			g.draw(outerPolygon);
		}
	}

	private java.awt.Polygon createPolygon(Coordinate[] coordinates) {
		java.awt.Polygon result = new java.awt.Polygon();
		for (Coordinate coord : coordinates) {
			result.addPoint(settings.convertX(coord.x), settings.convertY(coord.y));
		}
		return result;
	}

	/**
	 * Get the internal graphics object for direct interaction.
	 * 
	 * @return the internal graphics object
	 */
	public Graphics2D getGraphics2D() {
		return g;
	}

	/**
	 * Draw a point.
	 * 
	 * @param point the point geometry
	 */
	public void drawPoint(Point point) {
		drawPoint(point.getCoordinate());
	}

	/**
	 * Draw a point.
	 * 
	 * @param coord the point coordinates
	 */
	public void drawPoint(Coordinate coord) {
		g.fillOval(settings.convertX(coord.x) - settings.getPointSize() / 2, //
				settings.convertY(coord.y) - settings.getPointSize() / 2, //
				settings.getPointSize(), //
				settings.getPointSize());
	}

	/**
	 * Set the drawing color. Convenience method that delegates the call to the
	 * internal graphics object.
	 * 
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		g.setColor(color);
	}

	/**
	 * Set the stroke width.
	 * 
	 * @param width the stroke width to set
	 */
	public void setStroke(float width) {
		g.setStroke(new BasicStroke(width));
	}

	/**
	 * @return the settings
	 */
	public PaintSettings getSettings() {
		return settings;
	}

}
