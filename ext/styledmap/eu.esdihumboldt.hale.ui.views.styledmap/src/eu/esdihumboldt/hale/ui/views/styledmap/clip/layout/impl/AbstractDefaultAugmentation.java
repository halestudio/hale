/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import org.jdesktop.swingx.mapviewer.JXMapViewer;

import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.LayoutAugmentation;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterProxy;

/**
 * Base class for layout augmentations.
 * 
 * @author Simon Templer
 */
public abstract class AbstractDefaultAugmentation implements LayoutAugmentation {

	/**
	 * Default margin in pixels
	 */
	public static final int DEFAULT_MARGIN = 5;

	/**
	 * @see LayoutAugmentation#paint(Graphics2D, JXMapViewer, List, int, int)
	 */
	@Override
	public final void paint(Graphics2D g, JXMapViewer map, List<PainterProxy> painters, int width,
			int height) {
		Font orgFont = g.getFont();
		g.setFont(getFont(orgFont));

		doPaint(g, map, painters, width, height);

		g.setFont(orgFont);
	}

	/**
	 * Paint the layout augmentation.
	 * 
	 * @param g the graphics to paint on
	 * @param map the corresponding map viewer
	 * @param painters the list of layouted painters
	 * @param width the width of the paint area
	 * @param height the height of the paint area
	 */
	protected abstract void doPaint(Graphics2D g, JXMapViewer map, List<PainterProxy> painters,
			int width, int height);

	/**
	 * Get the font to use for augmentation text.
	 * 
	 * @param originalFont the original font applied to the graphics
	 * @return the font to use for the augmentation
	 */
	protected Font getFont(Font originalFont) {
		return originalFont.deriveFont(Font.BOLD);
	}

	/**
	 * Draw a text.
	 * 
	 * @param g the graphics context
	 * @param text the text to draw
	 * @param x the x coordinate where the text should be rendered
	 * @param y the y coordinate where the text should be rendered
	 */
	protected void drawText(Graphics2D g, String text, int x, int y) {
		if (text != null && !text.isEmpty()) {
			g.setColor(Color.WHITE);
			g.drawString(text, x - 1, y - 1);
			g.drawString(text, x - 1, y + 1);
			g.drawString(text, x + 1, y - 1);
			g.drawString(text, x + 1, y + 1);

			g.setColor(Color.BLACK);
			g.drawString(text, x, y);
		}
	}

	/**
	 * Draw a split line.
	 * 
	 * @param g the graphics context
	 * @param x1 the x coordinate of the line start point
	 * @param y1 the y coordinate of the line start point
	 * @param x2 the x coordinate of the line end point
	 * @param y2 the y coordinate of the line end point
	 */
	protected void drawSplitLine(Graphics2D g, int x1, int y1, int x2, int y2) {
		g.setColor(Color.WHITE);
		g.setStroke(new BasicStroke(3));
		g.drawLine(x1, y1, x2, y2);
	}

}
