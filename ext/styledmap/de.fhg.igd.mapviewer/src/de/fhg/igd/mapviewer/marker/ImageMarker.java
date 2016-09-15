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
package de.fhg.igd.mapviewer.marker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;

import de.fhg.igd.mapviewer.marker.area.Area;
import de.fhg.igd.mapviewer.marker.area.BoxArea;
import de.fhg.igd.mapviewer.waypoints.SelectableWaypoint;

/**
 * An image marker
 * 
 * @param <T> the painter context type
 * @author Simon Templer
 */
public class ImageMarker<T extends SelectableWaypoint<T>> extends SimpleMarker<T> {

	private int maxHeight = 32;

	private final BufferedImage image;

	/**
	 * Creates an image marker
	 * 
	 * @param image the icon image
	 */
	public ImageMarker(final BufferedImage image) {
		this.image = image;
	}

	/**
	 * @see SimpleMarker#paintMarker(java.lang.Object)
	 */
	@Override
	protected Area paintMarker(T context) {
		int width = image.getWidth();
		int height = image.getHeight();

		if (height > maxHeight) {
			float shrink = (float) maxHeight / (float) height;
			height = maxHeight;
			width = (int) (width * shrink);
		}

		int x = -width / 2;
		int y = -height / 2;

		// create graphics for image plus one pixel border
		Graphics2D g = beginPainting(width + 2, height + 2, width / 2 + 1, height / 2 + 1);
		try {
			g.drawImage(image, x, y, x + width, y + height, 0, 0, image.getWidth(),
					image.getHeight(), null);

			// shadow
			if (!hasAlpha(image)) {
				g.setColor(new Color(0, 0, 0, 180));
				// g.setColor((selected)?(new Color(200, 0, 0, 180)):(new
				// Color(0, 0, 200, 180)));
				g.fillPolygon(
						new int[] { x + 3, x + width, x + width, x + width + 3, x + width + 3,
								x + 3 },
						new int[] { y + height, y + height, y + 3, y + 3, y + height + 3,
								y + height + 3 },
						6);
			}

			// selection
			if (context.isSelected()) {
				g.setColor(Color.RED);
				g.drawRect(x - 1, y - 1, width + 1, height + 1);
			}
		} finally {
			endPainting(g);
		}

		return new BoxArea(x, y, x + width, y + height);
	}

	/**
	 * @param maxHeight the maxHeight to set
	 */
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	/**
	 * Determines if an image has transparent pixels
	 * 
	 * @param image the {@link Image}
	 * @return if the given image has transparent pixels
	 */
	public static boolean hasAlpha(Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			// ignore
		}

		// Get the image's color model
		ColorModel cm = pg.getColorModel();
		return cm.hasAlpha();
	}

}
