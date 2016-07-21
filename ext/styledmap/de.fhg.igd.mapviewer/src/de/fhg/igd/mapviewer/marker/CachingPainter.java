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

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

/**
 * A painter caching its result in a {@link BufferedImage}
 * 
 * @param <T> the painter context type
 * @author Simon Templer
 */
public abstract class CachingPainter<T> {

	private transient SoftReference<BufferedImage> cachedImage;
	private int originX = 0;
	private int originY = 0;

	private boolean dirty = false;
	private boolean antialiasing = true;

	private static Graphics2D graphicsDummy = null;

	/**
	 * Paint on a graphics device
	 * 
	 * @param g the graphics device
	 * @param context the painting context
	 * @param x the x coordinate of position to paint at
	 * @param y the y coordinate of position to paint at
	 */
	public void paint(Graphics2D g, T context, int x, int y) {
		if (dirty || cachedImage == null || cachedImage.get() == null) {
			// paint on image
			doPaint(context);
		}

		BufferedImage img = cachedImage.get();

		if (img != null) {
			configureGraphics(g);
			g.drawImage(img, x - originX, y - originY, null);
		}

		/*
		 * test g.setColor(Color.WHITE); g.drawRect(-2, -2, 4, 4);
		 */
	}

	/**
	 * Do the painting. Implementations of this method must call
	 * {@link #beginPainting(int, int, int, int)} to get a graphics object and
	 * {@link #endPainting(Graphics2D)} to finish painting.
	 * 
	 * @param context the painting context
	 */
	protected abstract void doPaint(T context);

	/**
	 * Start the painting by creating a graphics object to paint on
	 * 
	 * @param width the desired width
	 * @param height the desired height
	 * @param originX the translation of the origin in x-direction
	 * @param originY the translation of the origin in y-direction
	 * @return the graphics device to use for painting
	 */
	protected Graphics2D beginPainting(int width, int height, int originX, int originY) {
		this.originX = originX;
		this.originY = originY;

		BufferedImage img = (cachedImage == null) ? (null) : (cachedImage.get());
		clearCache();
		boolean clear = true;

		if (img == null || img.getWidth() != width || img.getHeight() != height) {
			img = GraphicsUtilities.createCompatibleTranslucentImage(width, height);
			cachedImage = new SoftReference<BufferedImage>(img);
			clear = false;
		}

		Graphics2D gfx = img.createGraphics();

		gfx.setClip(0, 0, width, height);

		if (clear) {
			Composite composite = gfx.getComposite();
			gfx.setComposite(AlphaComposite.Clear);
			gfx.fillRect(0, 0, width, height);
			gfx.setComposite(composite);
		}

		gfx.translate(originX, originY);
		configureGraphics(gfx);

		return gfx;
	}

	/**
	 * Finishes the painting, must be called always after calling
	 * {@link #beginPainting(int, int, int, int)} when painting is done.
	 * 
	 * @param gfx the graphics object
	 */
	protected void endPainting(Graphics2D gfx) {
		gfx.dispose();

		dirty = false;
	}

	/**
	 * Mark the painter as dirty, it will be repainted on the next call to
	 * {@link #paint(Graphics2D, Object, int, int)}
	 */
	public void markDirty() {
		if (!dirty) {
			clearCache();
			dirty = true;
		}
	}

	/**
	 * @param antialiasing the anti-aliasing to set
	 */
	public void setAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
	}

	/**
	 * Clears the cached image
	 */
	protected void clearCache() {
		if (cachedImage != null) {
			BufferedImage img = cachedImage.get();
			if (img != null) {
				img.flush();
			}
		}
	}

	/**
	 * Configure the given graphics
	 * 
	 * @param gfx the graphics device
	 */
	protected void configureGraphics(Graphics2D gfx) {
		if (antialiasing) {
			gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		else {
			gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	/**
	 * Get the dummy graphics device
	 * 
	 * @return the dummy graphics device
	 */
	protected Graphics2D getGraphicsDummy() {
		if (graphicsDummy == null) {
			BufferedImage image = GraphicsUtilities.createCompatibleTranslucentImage(32, 32);
			graphicsDummy = image.createGraphics();
		}

		configureGraphics(graphicsDummy);

		return graphicsDummy;
	}

}
