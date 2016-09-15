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
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;

import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.MapToolRenderer;

/**
 * BoxRenderer
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class BoxRenderer implements MapToolRenderer {

	private Color backColor = new Color(255, 0, 0, 100);

	private Color borderColor = Color.RED;

	/**
	 * @see MapToolRenderer#paint(Graphics2D, List, Point2D, MapTool)
	 */
	@Override
	public void paint(final Graphics2D g, final List<Point2D> points, final Point2D mousePos,
			final MapTool tool) {
		if (points.size() < 1 || (points.size() < 2 && mousePos == null))
			return;

		// draw a box
		Point2D p1 = points.get(0);

		Point2D p2;
		if (points.size() > 1)
			p2 = points.get(1);
		else
			p2 = mousePos;

		int x, y, width, height;

		if (p1.getX() < p2.getX()) {
			x = (int) p1.getX();
			width = (int) p2.getX() - (int) p1.getX();
		}
		else {
			x = (int) p2.getX();
			width = (int) p1.getX() - (int) p2.getX();
		}

		if (p1.getY() < p2.getY()) {
			y = (int) p1.getY();
			height = (int) p2.getY() - (int) p1.getY();
		}
		else {
			y = (int) p2.getY();
			height = (int) p1.getY() - (int) p2.getY();
		}

		Rectangle box = new Rectangle(x, y, width, height);

		g.setColor(backColor);
		g.fill(box);
		g.setColor(borderColor);
		g.draw(box);
	}

	/**
	 * @see MapToolRenderer#repaintOnMouseMove()
	 */
	@Override
	public boolean repaintOnMouseMove() {
		return true;
	}

	/**
	 * @return the backColor
	 */
	public Color getBackColor() {
		return backColor;
	}

	/**
	 * @param backColor the backColor to set
	 */
	public void setBackColor(Color backColor) {
		this.backColor = backColor;
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

}
