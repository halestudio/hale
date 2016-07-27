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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;

import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.MapToolRenderer;

/**
 * XPointRenderer
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class XPointRenderer implements MapToolRenderer {

	private Color color = Color.RED;
	private int xSize = 10;
	private int lineStrength = 3;

	/**
	 * @see MapToolRenderer#paint(Graphics2D, List, Point2D, MapTool)
	 */
	@Override
	public void paint(Graphics2D g, List<Point2D> points, Point2D mousePos, MapTool tool) {
		if (!points.isEmpty()) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate((int) points.get(0).getX(), (int) points.get(0).getY());

			g2.setColor(color);
			g2.setStroke(
					new BasicStroke(lineStrength, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.drawLine(-xSize / 2, -xSize / 2, xSize / 2, xSize / 2);
			g2.drawLine(-xSize / 2, xSize / 2, xSize / 2, -xSize / 2);
		}
	}

	/**
	 * @see MapToolRenderer#repaintOnMouseMove()
	 */
	@Override
	public boolean repaintOnMouseMove() {
		return false;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the xSize
	 */
	public int getxSize() {
		return xSize;
	}

	/**
	 * @param xSize the xSize to set
	 */
	public void setxSize(int xSize) {
		this.xSize = xSize;
	}

	/**
	 * @return the lineStrength
	 */
	public int getLineStrength() {
		return lineStrength;
	}

	/**
	 * @param lineStrength the lineStrength to set
	 */
	public void setLineStrength(int lineStrength) {
		this.lineStrength = lineStrength;
	}

}
