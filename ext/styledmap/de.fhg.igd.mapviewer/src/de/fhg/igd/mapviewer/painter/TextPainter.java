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

package de.fhg.igd.mapviewer.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.jdesktop.swingx.mapviewer.JXMapViewer;
import org.jdesktop.swingx.painter.AbstractPainter;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.MapPainter;

/**
 * Paints a text
 * 
 * @author Simon Templer
 */
public abstract class TextPainter extends AbstractPainter<JXMapViewer>implements MapPainter {

	private BasicMapKit mapKit;

	/**
	 * Create a text painter
	 * 
	 * @param cacheable if the painter is cacheable, i.e. if the text stays the
	 *            same
	 */
	public TextPainter(boolean cacheable) {
		super(cacheable);

		setAntialiasing(true);
	}

	@Override
	public void setMapKit(BasicMapKit mapKit) {
		this.mapKit = mapKit;
	}

	/**
	 * Get the map kit the painter is associated with
	 * 
	 * @return the map kit or <code>null</code>
	 */
	public BasicMapKit getMapKit() {
		return mapKit;
	}

	@Override
	public String getTipText(Point point) {
		return null;
	}

	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * Get the border color to use for the text border.
	 * 
	 * @return the border color for the text border, <code>null</code> for no
	 *         border
	 */
	protected Color getBorderColor() {
		return null;
	}

	@Override
	protected void doPaint(Graphics2D g, JXMapViewer object, int width, int height) {
		String text = getText();
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

		drawText(g, text, bounds, width, height);
	}

	/**
	 * Draw the text. The default implementation draws the text in the bottom
	 * left corner.
	 * 
	 * @param g the graphics device to draw an
	 * @param text the text to draw
	 * @param bounds the bounds of the text to draw
	 * @param width the draw surface width
	 * @param height the draw surface height
	 * 
	 * @see Graphics2D#drawString(String, int, int)
	 */
	protected void drawText(Graphics2D g, String text, Rectangle2D bounds, int width, int height) {
		drawBorderedString(g, text, 5, (int) bounds.getHeight() + 5);
	}

	/**
	 * Draw a string with a border defined through {@link #getBorderColor()}.
	 * 
	 * @param g the graphics context
	 * @param text the text to draw
	 * @param x the x position
	 * @param y the y position
	 * @see #getBorderColor()
	 */
	protected void drawBorderedString(Graphics2D g, String text, int x, int y) {
		// draw border
		Color borderColor = getBorderColor();
		if (borderColor != null) {
			Color orgColor = g.getColor();
			g.setColor(borderColor);
			g.drawString(text, x - 1, y - 1);
			g.drawString(text, x - 1, y + 1);
			g.drawString(text, x + 1, y - 1);
			g.drawString(text, x + 1, y + 1);
			g.setColor(orgColor);
		}

		g.drawString(text, x, y);
	}

	/**
	 * Get the text to paint
	 * 
	 * @return the text to paint
	 */
	protected abstract String getText();

}
