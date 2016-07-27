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
package de.fhg.igd.mapviewer;

import java.awt.Rectangle;

/**
 * Abstract overlay painter that knows of its {@link BasicMapKit}
 * 
 * @author Simon Templer
 */
public abstract class MapKitTileOverlayPainter extends AbstractTileOverlayPainter {

	private BasicMapKit mapKit;

	/**
	 * @see AbstractTileOverlayPainter#AbstractTileOverlayPainter(int)
	 */
	public MapKitTileOverlayPainter(int numberOfThreads) {
		super(numberOfThreads);
	}

	/**
	 * Set the map kit
	 * 
	 * @param mapKit the map kit
	 */
	public void setMapKit(BasicMapKit mapKit) {
		this.mapKit = mapKit;
	}

	/**
	 * Get the map kit
	 * 
	 * @return the map kit
	 */
	public BasicMapKit getMapKit() {
		return mapKit;
	}

	/**
	 * @see AbstractTileOverlayPainter#isCurrentZoom(int)
	 */
	@Override
	protected boolean isCurrentZoom(int zoom) {
		if (mapKit != null) {
			return zoom == mapKit.getMainMap().getZoom();
		}

		return false;
	}

	@Override
	protected Rectangle getCurrentViewBounds(int zoom) {
		if (mapKit != null) {
			if (mapKit.getMainMap().getZoom() == zoom) {
				return mapKit.getMainMap().getViewportBounds();
			}
		}

		return null;
	}

	/**
	 * @see AbstractTileOverlayPainter#repaint()
	 */
	@Override
	protected void repaint() {
		if (mapKit != null) {
			mapKit.refresh();
		}
	}
}
