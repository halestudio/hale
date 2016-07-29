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

package de.fhg.igd.mapviewer.tip;

import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.JXMapViewer;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.MapPainter;

/**
 * Map tooltip
 * 
 * @author Simon Templer
 */
public abstract class AbstractMapTip implements MapTip {

	/**
	 * Paints the tip layer
	 */
	private class MapTipPainter implements MapPainter {

		@Override
		public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
			Rectangle viewPort = map.getViewportBounds();
			int x, y;
			synchronized (AbstractMapTip.this) {
				// decide if to paint
				boolean paint = wantsToPaint();

				if (paint) {
					if (pos != null) {
						try {
							Point2D p = map.getTileFactory().getTileProvider().getConverter()
									.geoToPixel(pos, map.getZoom());

							// determine tip point
							x = (int) p.getX() - viewPort.x;
							y = (int) p.getY() - viewPort.y;
						} catch (IllegalGeoPositionException e) {
							// ignore
							return;
						}
					}
					else {
						Point mousePos = MouseInfo.getPointerInfo().getLocation();
						Point mapPos = map.getLocationOnScreen();
						x = mousePos.x - mapPos.x;
						y = mousePos.y - mapPos.y;
					}
				}
				else {
					return;
				}
			}

			// determine paint position
			Point pos = position(x, y, tip.getHeight(), tip.getWidth(), viewPort.width,
					viewPort.height);

			// paint label
			g.translate(pos.x, pos.y);
			tip.paint(g);
			g.translate(-pos.x, -pos.y);
		}

		@Override
		public String getTipText(Point point) {
			return null;
		}

		@Override
		public void setMapKit(BasicMapKit mapKit) {
			// ignore
		}

		@Override
		public void dispose() {
			AbstractMapTip.this.dispose();
		}

	}

	private JXMapViewer map;

	private final JLabel tip = new JLabel();

	private GeoPosition pos = null;

	private MapTipPainter painter;

	/**
	 * @see MapTip#init(JXMapViewer)
	 */
	@Override
	public void init(JXMapViewer map) {
		this.map = map;

		configureTipLabel(tip);
	}

	/**
	 * Dispose the map tip
	 */
	protected void dispose() {
		// do nothing
	}

	/**
	 * Get the associated map
	 * 
	 * @return the associated map
	 */
	protected JXMapViewer getMap() {
		return map;
	}

	/**
	 * Configure the label used to paint the tip
	 * 
	 * @param tip the label used to paint the tip
	 */
	protected void configureTipLabel(JLabel tip) {
		Color foreground = PlatformUI.getWorkbench().getDisplay()
				.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
		Color background = PlatformUI.getWorkbench().getDisplay()
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
		java.awt.Color fgColor = new java.awt.Color(foreground.getRed(), foreground.getGreen(),
				foreground.getBlue());
		tip.setForeground(fgColor);
		tip.setBackground(new java.awt.Color(background.getRed(), background.getGreen(),
				background.getBlue()));

		tip.setOpaque(true);
		tip.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(fgColor, 1),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
	}

	/**
	 * Position the tip before painting. This implementation paints the tip
	 * above the position. Override to change that behavior.
	 * 
	 * @param x the x ordinate of the paint position
	 * @param y the y ordinate of the paint position
	 * @param tipHeight the tip height
	 * @param tipWidth the tip width
	 * @param viewWidth the viewport width
	 * @param viewHeight the viewport height
	 * 
	 * @return the paint position for the tip
	 */
	protected Point position(int x, int y, int tipHeight, int tipWidth, int viewWidth,
			int viewHeight) {
		y -= tip.getHeight(); // above cursor

		return new Point(x, y);
	}

	/**
	 * @see MapTip#getLastText()
	 */
	@Override
	public String getLastText() {
		return tip.getText();
	}

	/**
	 * @see MapTip#getPainter()
	 */
	@Override
	public MapPainter getPainter() {
		if (painter == null) {
			painter = new MapTipPainter();
		}

		return painter;
	}

	/**
	 * @see MapTip#wantsToPaint()
	 */
	@Override
	public boolean wantsToPaint() {
		synchronized (this) {
			// check tip text
			boolean paint = tip.getText() != null && !tip.getText().isEmpty();
			// check position
			paint = paint && (pos != null || useMousePos());

			return paint;
		}
	}

	/**
	 * Get if the mouse position shall be used as the tip position if no
	 * {@link GeoPosition} is specified
	 * 
	 * @return if the mouse position shall be used as tip position if no
	 *         position is given
	 */
	protected boolean useMousePos() {
		return false;
	}

	/**
	 * Set the tip text
	 * 
	 * @param text the tip text
	 * @param pos the tip position
	 */
	public void setTipText(String text, GeoPosition pos) {
		synchronized (this) {
			tip.setText(text);
			tip.setSize(tip.getPreferredSize());
			this.pos = pos;
		}

		if (map != null) {
			map.repaint();
		}
	}

	/**
	 * Clear the tip
	 */
	public void clearTip() {
		setTipText(null, null);
	}

}
