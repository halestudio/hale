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

package eu.esdihumboldt.util.geometry.interpolation;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.io.IOException;
import java.util.function.Consumer;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;

import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcString;
import eu.esdihumboldt.util.svg.test.AbstractSVGPainterTest;
import eu.esdihumboldt.util.svg.test.PaintSettings;
import eu.esdihumboldt.util.svg.test.SVGPainter;

/**
 * Base class for Arc based tests.
 * 
 * @author Simon Templer
 */
public abstract class AbstractArcTest extends AbstractSVGPainterTest {

	/**
	 * Prepare a canvas to draw an arc, perform the given draw operation and
	 * save the drawing.
	 * 
	 * @param arc the arc to draw
	 * @param draw function that draws on the canvas
	 * @throws IOException if saving the drawing fails
	 */
	protected void withArcCanvas(Arc arc, Consumer<SVGPainter> draw) throws IOException {
		Envelope envelope = getArcEnvelope(arc);
		PaintSettings settings = new PaintSettings(envelope, 1000, 10);
		SVGPainter svg = new SVGPainter(settings);
		svg.setCanvasSize(1000, 1000);

		draw.accept(svg);

		saveDrawing("arc", svg);
	}

	private Envelope getArcEnvelope(Arc arc) {
		Envelope envelope = new Envelope(arc.toArcByCenterPoint().getCenterPoint());
		envelope.expandBy(arc.toArcByCenterPoint().getRadius());
		return envelope;
	}

	/**
	 * Prepare a canvas to draw an arc, perform the given draw operation and
	 * save the drawing.
	 * 
	 * @param arcs the arc string to draw
	 * @param draw function that draws on the canvas
	 * @throws IOException if saving the drawing fails
	 */
	protected void withArcStringCanvas(ArcString arcs, Consumer<SVGPainter> draw)
			throws IOException {
		Envelope envelope = new Envelope();

		for (Arc arc : arcs.getArcs()) {
			envelope.expandToInclude(getArcEnvelope(arc));
		}

		PaintSettings settings = new PaintSettings(envelope, 1000, 10);
		SVGPainter svg = new SVGPainter(settings);
		svg.setCanvasSize(1000, 1000);

		draw.accept(svg);

		saveDrawing("arc-string", svg);
	}

	/**
	 * Draw a name.
	 * 
	 * @param svg the SVG painter
	 * @param name the name
	 */
	protected void drawName(SVGPainter svg, String name) {
		if (name == null) {
			return;
		}

		svg.setColor(Color.DARK_GRAY);
//		LineMetrics fontMetrics = svg.getGraphics2D().getFontMetrics().getLineMetrics(name, svg.getGraphics2D());
		svg.getGraphics2D().setFont(svg.getGraphics2D().getFont()
				.deriveFont(40.0f)/* .deriveFont(Font.BOLD) */);
		svg.getGraphics2D().drawString(name, 30, 70);
	}

	/**
	 * Draw an arc with markers for the points defining the arc. Saves the
	 * resulting drawing.
	 * 
	 * @param arc the arc to draw
	 * @throws IOException if saving the drawing fails
	 */
	protected void drawArcWithMarkers(Arc arc) throws IOException {
		drawArcWithMarkers(arc, null);
	}

	/**
	 * Draw an arc with markers for the points defining the arc. Saves the
	 * resulting drawing.
	 * 
	 * @param arc the arc to draw
	 * @param name an optional name for the drawing
	 * @throws IOException if saving the drawing fails
	 */
	protected void drawArcWithMarkers(Arc arc, String name) throws IOException {
		withArcCanvas(arc, svg -> {
			String arcName = name;
			if (arcName == null) {
				arcName = arc.toString();

//				StackTraceElement[] trace = Thread.currentThread().getStackTrace();
//				if (trace.length >= 3) {
//					name = trace[2].getMethodName() + " - " + name;
//				}
			}

			drawName(svg, arcName);

			drawArcWithMarkers(svg, arc);
		});
	}

	/**
	 * Draw an interpolated arc with debug information. Saves the resulting
	 * drawing.
	 * 
	 * @param arc the arc to draw
	 * @param gridSize the grid size
	 * @param interpolated the interpolated geometry
	 * @throws IOException if saving the drawing fails
	 */
	protected void drawGridInterpolatedArc(Arc arc, double gridSize, LineString interpolated)
			throws IOException {
		withArcCanvas(arc, svg -> {
			drawGrid(svg, gridSize);

			drawName(svg, arc.toString());

			drawArcWithMarkers(svg, arc);

			if (interpolated != null) {
				svg.setColor(Color.BLACK);
				svg.setStroke(2.5f);
				svg.drawLineString(interpolated);
			}
		});
	}

	/**
	 * Draw an interpolated arc with debug information. Saves the resulting
	 * drawing.
	 * 
	 * @param arc the arc to draw
	 * @param interpolated the interpolated geometry
	 * @throws IOException if saving the drawing fails
	 */
	protected void drawInterpolatedArc(Arc arc, LineString interpolated) throws IOException {
		withArcCanvas(arc, svg -> {
			if (interpolated != null) {
				svg.setColor(Color.DARK_GRAY);
				for (Coordinate coord : interpolated.getCoordinates()) {
					svg.drawPoint(coord);
				}
			}

			drawName(svg, arc.toString());

			drawArcWithMarkers(svg, arc);

			if (interpolated != null) {
				svg.setColor(Color.BLACK);
				svg.setStroke(2.5f);
				svg.drawLineString(interpolated);
			}
		});
	}

	/**
	 * Draw an interpolated arc string with debug information. Saves the
	 * resulting drawing.
	 * 
	 * @param arcs the arc string to draw
	 * @param gridSize the grid size
	 * @param interpolated the interpolated geometry
	 * @throws IOException if saving the drawing fails
	 */
	protected void drawGridInterpolatedArcString(ArcString arcs, double gridSize,
			LineString interpolated) throws IOException {
		withArcStringCanvas(arcs, svg -> {
			drawGrid(svg, gridSize);

			for (Arc arc : arcs.getArcs()) {
				drawArcWithMarkers(svg, arc);
			}

			if (interpolated != null) {
				svg.setColor(Color.BLACK);
				svg.setStroke(2.5f);
				svg.drawLineString(interpolated);
			}
		});
	}

	/**
	 * Draw an interpolated arc string with debug information. Saves the
	 * resulting drawing.
	 * 
	 * @param arcs the arc string to draw
	 * @param interpolated the interpolated geometry
	 * @throws IOException if saving the drawing fails
	 */
	protected void drawInterpolatedArcString(ArcString arcs, LineString interpolated)
			throws IOException {
		withArcStringCanvas(arcs, svg -> {
			if (interpolated != null) {
				svg.setColor(Color.DARK_GRAY);
				for (Coordinate coord : interpolated.getCoordinates()) {
					svg.drawPoint(coord);
				}
			}

			for (Arc arc : arcs.getArcs()) {
				drawArcWithMarkers(svg, arc);
			}

			if (interpolated != null) {
				svg.setColor(Color.BLACK);
				svg.setStroke(2.5f);
				svg.drawLineString(interpolated);
			}
		});
	}

	/**
	 * Draw a grid with a specified grid size.
	 * 
	 * @param svg the SVG painter
	 * @param gridSize the grid size, i.e. the height/width of grid cells
	 */
	private void drawGrid(SVGPainter svg, double gridSize) {
		svg.setStroke(1.0f);
		svg.setColor(Color.GREEN.darker());

		double scaledStep = gridSize * svg.getSettings().getScaleFactor();

		double maxY = svg.getSettings().getMaxY();
		maxY = Math.floor(maxY / gridSize) * gridSize; // match to grid
		maxY = svg.getSettings().convertY(maxY);
		double capY = maxY + svg.getSettings().getCanvasSize().getHeight();

		double minX = svg.getSettings().getMinX();
		minX = Math.ceil(minX / gridSize) * gridSize; // match to grid
		minX = svg.getSettings().convertX(minX);
		double capX = minX + svg.getSettings().getCanvasSize().getWidth();

		double y = maxY;
		while (y <= capY) {
			svg.getGraphics2D().drawLine((int) Math.round(minX), (int) Math.round(y),
					(int) Math.round(capX), (int) Math.round(y));
			y = y + scaledStep;
		}

		double x = minX;
		while (x <= capX) {
			svg.getGraphics2D().drawLine((int) Math.round(x), (int) Math.round(maxY),
					(int) Math.round(x), (int) Math.round(capY));
			x = x + scaledStep;
		}
	}

	/**
	 * Draw an arc with markers for the points defining the arc.
	 * 
	 * @param painter the SVG painter
	 * @param arc the arc to draw
	 */
	protected void drawArcWithMarkers(SVGPainter painter, Arc arc) {
		painter.setColor(Color.DARK_GRAY);
		painter.setStroke(2.0f);
		drawArc(painter, arc);

		if (arc instanceof ArcByPoints) {
			painter.setColor(Color.GREEN);
		}
		else {
			painter.setColor(Color.BLUE);
		}
		ArcByPoints bp = arc.toArcByPoints();
		painter.drawPoint(bp.getStartPoint());
		painter.drawPoint(bp.getEndPoint());
		painter.setColor(Color.RED);
		painter.drawPoint(bp.getMiddlePoint());

		if (arc instanceof ArcByCenterPoint) {
			painter.setColor(Color.GREEN);
		}
		else {
			painter.setColor(Color.BLUE);
		}
		ArcByCenterPoint bc = arc.toArcByCenterPoint();
		painter.drawPoint(bc.getCenterPoint());
	}

	/**
	 * Draw an arc.
	 * 
	 * @param painter the SVG painter
	 * @param arc the arc to draw
	 */
	protected void drawArc(SVGPainter painter, Arc arc) {
		Arc2D arcShape = createArcShape(arc, painter.getSettings());
		painter.getGraphics2D().draw(arcShape);
	}

	/**
	 * Create an Arc AWT shape from a given arc.
	 * 
	 * @param arc the arc
	 * @param paintSettings the paint settings for coordinate conversion
	 * @return the arc shape
	 */
	protected Arc2D createArcShape(Arc arc, PaintSettings paintSettings) {
		ArcByCenterPoint a = arc.toArcByCenterPoint();

		// FIXME probably not the right position
		Coordinate center = paintSettings.convertPoint(a.getCenterPoint());

		double radius = a.getRadius() * paintSettings.getScaleFactor();
		double startAngle = a.getStartAngle().getDegrees();
		double angleExtent = a.getAngleBetween().getDegrees();
		Arc2D.Double arcShape = new Arc2D.Double();
		arcShape.setArcByCenter(center.x, center.y, radius, startAngle, angleExtent, Arc2D.OPEN);
		return arcShape;
	}

	/**
	 * Test coordinates being equal using a lax comparison for X and Y.
	 * 
	 * @param expected the expected coordinate
	 * @param other the coordinate to compare
	 */
	public void assertEqualsCoord(Coordinate expected, Coordinate other) {
		assertEquals(expected.x, other.x, 1e-3);
		assertEquals(expected.y, other.y, 1e-3);
	}

}
