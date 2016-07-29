/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.tools.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;

import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.MapToolRenderer;

/**
 * PolygonRenderer
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class LineRenderer implements MapToolRenderer {

	private Color borderColor = Color.RED;

	private boolean drawMousePos = true;

	/**
	 * @see MapToolRenderer#paint(Graphics2D, List, Point2D, MapTool)
	 */
	@Override
	public void paint(Graphics2D g, List<Point2D> points, Point2D mousePos, MapTool tool) {
		if (drawMousePos && mousePos != null)
			points.add(mousePos);

		int count = points.size();

		if (count > 0) {
			int[] x = new int[count];
			int[] y = new int[count];

			int index = 0;
			for (Point2D point : points) {
				x[index] = (int) point.getX();
				y[index] = (int) point.getY();

				index++;
			}

			g.setColor(borderColor);
			g.drawPolyline(x, y, count);
		}
	}

	/**
	 * @see MapToolRenderer#repaintOnMouseMove()
	 */
	@Override
	public boolean repaintOnMouseMove() {
		return drawMousePos;
	}

	/**
	 * @return the borderColor
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor the borderColor to set
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * @param drawMousePos the drawMousePos to set
	 */
	public void setDrawMousePos(boolean drawMousePos) {
		this.drawMousePos = drawMousePos;
	}

}
