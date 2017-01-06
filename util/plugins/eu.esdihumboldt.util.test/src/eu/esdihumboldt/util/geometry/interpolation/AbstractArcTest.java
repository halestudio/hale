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

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.io.IOException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import eu.esdihumboldt.util.geometry.interpolation.model.Arc;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByCenterPoint;
import eu.esdihumboldt.util.geometry.interpolation.model.ArcByPoints;
import eu.esdihumboldt.util.svg.test.AbstractSVGPainterTest;
import eu.esdihumboldt.util.svg.test.PaintSettings;
import eu.esdihumboldt.util.svg.test.SVGPainter;

/**
 * Base class for Arc based tests.
 * 
 * @author Simon Templer
 */
public class AbstractArcTest extends AbstractSVGPainterTest {

	/**
	 * Draw an arc with markers for the points defining the arc. Saves the
	 * resulting drawing.
	 * 
	 * @param arc the arc to draw
	 * @throws IOException if saving the drawing fails
	 */
	protected void drawArcWithMarkers(Arc arc) throws IOException {
		Envelope envelope = new Envelope(arc.toArcByCenterPoint().getCenterPoint());
		envelope.expandBy(arc.toArcByCenterPoint().getRadius());
		PaintSettings settings = new PaintSettings(envelope, 1000, 10);
		SVGPainter svg = new SVGPainter(settings);
		svg.setCanvasSize(1000, 1000);

		drawArcWithMarkers(svg, arc);

		saveDrawing("arc", svg);
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
		Coordinate upperLeft = paintSettings.convertPoint(new Coordinate(
				a.getCenterPoint().x - a.getRadius(), a.getCenterPoint().y - a.getRadius()));

		double upperLeftRectX = upperLeft.x;
		double upperLeftRectY = upperLeft.y;
		double circleWidth = a.getRadius() * 2 * paintSettings.getScaleFactor();
		double circleHeight = circleWidth;
		// add 180 degrees because Y axis is flipped in Graphics2D
		double startAngle = a.getStartAngle().getDegrees() + 180.0;
		double angleExtent = a.getAngleBetween().getDegrees();
		return new Arc2D.Double(upperLeftRectX, upperLeftRectY, circleWidth, circleHeight,
				startAngle, angleExtent, Arc2D.OPEN);
	}

}
