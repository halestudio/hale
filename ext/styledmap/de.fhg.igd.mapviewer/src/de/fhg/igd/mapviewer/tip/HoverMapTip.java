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

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.JXMapViewer;
import org.jdesktop.swingx.mapviewer.PixelConverter;

/**
 * Map tooltip
 * 
 * @author Simon Templer
 */
public abstract class HoverMapTip extends AbstractMapTip {

	private static final int HOVER_DELAY = 400;

	private final ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(2);

	private ScheduledFuture<?> hoverTimer = null;

	private ScheduledFuture<?> closeTimer = null;

	/**
	 * If the tip shall be hidden on mouse move
	 */
	private boolean hideOnMouseMove = true;

	/**
	 * Initialize the map tip with the given map
	 * 
	 * @param map the map
	 */
	@Override
	public void init(JXMapViewer map) {
		super.init(map);

		MouseAdapter mouse = new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseMoved(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (hoverTimer != null) {
					hoverTimer.cancel(true);
					// XXX on Windows this occurs when showing the tooltip -
					// hideToolTip();
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				hideTip();
			}

			@Override
			public void mouseMoved(final MouseEvent e) {
				// cancel old task
				if (hoverTimer != null) {
					hoverTimer.cancel(true);
				}

				if (hideOnMouseMove) {
					hideTip();
				}

				if (true) { // start hover timer
					// start new one
					hoverTimer = scheduleService.schedule(new Runnable() {

						@Override
						public void run() {
							// test if mouse was moved
							Point evt = new Point(e.getXOnScreen(), e.getYOnScreen());
							Point pos = MouseInfo.getPointerInfo().getLocation();
							if (evt.equals(pos)) {
								String text = getTipText(e.getX(), e.getY(),
										getMap().getTileFactory().getTileProvider().getConverter(),
										getMap().getZoom());

								if (text != null) {
									showTip(e, text);
								}
							}
						}
					}, HOVER_DELAY, TimeUnit.MILLISECONDS);
				}
			}
		};

		map.addMouseListener(mouse);
		map.addMouseMotionListener(mouse);
	}

	/**
	 * @see AbstractMapTip#wantsToPaint()
	 */
	@Override
	public boolean wantsToPaint() {
		synchronized (this) {
			return super.wantsToPaint() && closeTimer != null;
		}
	}

	/**
	 * Get the tip text for the given position
	 * 
	 * @param x the x viewport pixel ordinate
	 * @param y the y viewport pixel ordinate
	 * @param converter the converter
	 * @param zoom the zoom level
	 * @return the tip text, <code>null</code> for no tip
	 */
	protected abstract String getTipText(int x, int y, PixelConverter converter, int zoom);

	private void showTip(final MouseEvent e, final String text) {
		synchronized (HoverMapTip.this) {
			PixelConverter converter = getMap().getTileFactory().getTileProvider().getConverter();
			int zoom = getMap().getZoom();
			Rectangle viewPort = getMap().getViewportBounds();
			GeoPosition pos = converter
					.pixelToGeo(new Point(viewPort.x + e.getX(), viewPort.y + e.getY()), zoom);

			setTipText(text, pos);

			if (closeTimer != null) {
				closeTimer.cancel(true);
			}

			closeTimer = scheduleService.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					hideTip();
				}
			}, 5 * HOVER_DELAY, 1000, TimeUnit.MILLISECONDS);
		}
	}

	private void hideTip() {
		synchronized (HoverMapTip.this) {
			if (closeTimer != null) {
				closeTimer.cancel(true);
				closeTimer = null;
			}
			clearTip();
		}
	}

	/**
	 * @return if the tip shall be hidden on mouse move
	 */
	public boolean isHideOnMouseMove() {
		return hideOnMouseMove;
	}

	/**
	 * Set if the tip shall be hidden on mouse move
	 * 
	 * @param hideOnMouseMove if the tip shall be hidden on mouse move
	 */
	public void setHideOnMouseMove(boolean hideOnMouseMove) {
		this.hideOnMouseMove = hideOnMouseMove;
	}

}
