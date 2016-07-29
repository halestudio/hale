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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.jdesktop.swingx.mapviewer.TileOverlayPainter;
import org.jdesktop.swingx.mapviewer.TileProvider;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectProcedure;

/**
 * Abstract tile overlay painter base class.
 * 
 * @author Simon Templer
 */
public abstract class AbstractTileOverlayPainter implements TileOverlayPainter {

	/**
	 * Delegates tile painting
	 */
	private class TilePaintDelegate {

		private final int x;
		private final int y;
		private final int tilePosX;
		private final int tilePosY;
		private final int tileWidth;
		private final int tileHeight;
		private final PixelConverter converter;
		private final int zoom;

		/**
		 * Creates a tile paint delegate
		 * 
		 * @param x the tile x number
		 * @param y the tile y number
		 * @param tilePosX the tile x position in pixel
		 * @param tilePosY the tile y position in pixel
		 * @param tileWidth the tile width
		 * @param tileHeight the tile height
		 * @param converter the pixel converter
		 * @param zoom the zoom level
		 */
		public TilePaintDelegate(int x, int y, int tilePosX, int tilePosY, int tileWidth,
				int tileHeight, PixelConverter converter, int zoom) {
			this.x = x;
			this.y = y;
			this.tilePosX = tilePosX;
			this.tilePosY = tilePosY;
			this.tileWidth = tileWidth;
			this.tileHeight = tileHeight;
			this.converter = converter;
			this.zoom = zoom;
		}

		/**
		 * Paint the tile
		 */
		public void paintTile() {
			BufferedImage img = repaintTile(tilePosX, tilePosY, tileWidth, tileHeight, converter,
					zoom);
			if (img == null) {
				cacheTile(x, y, zoom, emptyImage);
			}
			else {
				cacheTile(x, y, zoom, img);
			}

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					repaint();
				}
			});
		}

	}

	/**
	 * Refresher
	 */
	public class DefaultRefresher implements Refresher {

		// private TIntObjectHashMap<TIntHashSet> tileMap = new
		// TIntObjectHashMap<TIntHashSet>();
		private final TIntObjectHashMap<TIntObjectHashMap<TIntHashSet>> tileMap = new TIntObjectHashMap<TIntObjectHashMap<TIntHashSet>>();

		private final TileProvider tiles;

		private final int minZoom;
		private final int maxZoom;

		private final int tileWidth;
		private final int tileHeight;

		private final boolean removeCached;

		private boolean valid = true;

		private BufferedImageOp imageOp;

		/**
		 * Constructor
		 * 
		 * @param tiles the tile provider
		 * @param removeCached if the cached tiles shall be removed
		 */
		public DefaultRefresher(TileProvider tiles, boolean removeCached) {
			this.tiles = tiles;
			this.removeCached = removeCached;

			if (tiles != null) {
				minZoom = tiles.getMinimumZoom();
				maxZoom = tiles.getTotalMapZoom();

				tileWidth = tiles.getTileWidth(minZoom);
				tileHeight = tiles.getTileHeight(minZoom);
			}
			else {
				minZoom = maxZoom = tileWidth = tileHeight = 0;
			}
		}

		@Override
		public void setImageOp(BufferedImageOp imageOp) {
			this.imageOp = imageOp;
		}

		@Override
		public void addPosition(GeoPosition pos) {
			if (tiles == null)
				return;

			try {
				Point2D point = tiles.getConverter().geoToPixel(pos, minZoom);

				int x = (int) point.getX();
				int y = (int) point.getY();

				for (int zoom = minZoom; zoom <= maxZoom; zoom++) {
					addOverlappingTiles(zoom, x, y);

					x = x / 2;
					y = y / 2;
				}
			} catch (IllegalGeoPositionException e) {
				log.trace("Error adding position to refresher", e); //$NON-NLS-1$
			}
		}

		/**
		 * Add tiles for the given pixel coordinates, also regarding a possible
		 * overlap
		 * 
		 * @param zoom the zoom level
		 * @param x the pixel x ordinate
		 * @param y the pixel y ordinate
		 */
		private void addOverlappingTiles(int zoom, int x, int y) {
			int overlap = getMaxOverlap();

			addTileByPixel(zoom, x + overlap, y + overlap);
			addTileByPixel(zoom, x - overlap, y + overlap);
			addTileByPixel(zoom, x - overlap, y - overlap);
			addTileByPixel(zoom, x + overlap, y - overlap);
		}

		@Override
		public void addArea(GeoPosition topLeft, GeoPosition bottomRight) {
			if (tiles == null)
				return;

			// for each zoom level
			for (int zoom = minZoom; zoom <= maxZoom; zoom++) {
				// add tiles that represent the area
				try {
					int overlap = getMaxOverlap();

					Point2D p1 = tiles.getConverter().geoToPixel(topLeft, zoom);
					Point2D p2 = tiles.getConverter().geoToPixel(bottomRight, zoom);

					int firstTileX = (int) Math.min(p1.getX() - overlap, p2.getX() - overlap)
							/ tileWidth;
					int firstTileY = (int) Math.min(p1.getY() - overlap, p2.getY() - overlap)
							/ tileHeight;
					int lastTileX = (int) Math.max(p1.getX() + overlap, p2.getX() + overlap)
							/ tileWidth;
					int lastTileY = (int) Math.max(p1.getY() + overlap, p2.getY() + overlap)
							/ tileHeight;

					// add all identified tiles
					for (int tileX = firstTileX; tileX <= lastTileX; tileX++) {
						for (int tileY = firstTileY; tileY <= lastTileY; tileY++) {
							addTile(zoom, tileX, tileY);
						}
					}
				} catch (IllegalGeoPositionException e) {
					log.trace("Error adding area to refresher");
				}
			}
		}

		private void addTileByPixel(int zoom, int pixelX, int pixelY) {
			int tileX = pixelX / tileWidth;
			int tileY = pixelY / tileHeight;

			addTile(zoom, tileX, tileY);
		}

		private void addTile(int zoom, int tileX, int tileY) {
			TIntObjectHashMap<TIntHashSet> zoomMap = tileMap.get(zoom);
			if (zoomMap == null) {
				zoomMap = new TIntObjectHashMap<TIntHashSet>();
				tileMap.put(zoom, zoomMap);
			}

			TIntHashSet ySet = zoomMap.get(tileX);
			if (ySet == null) {
				ySet = new TIntHashSet();
				zoomMap.put(tileX, ySet);
			}

			ySet.add(tileY);
		}

		@Override
		public void execute() {
			if (tiles == null)
				return;

			if (valid && tiles == AbstractTileOverlayPainter.this.tiles) {
				// execution only possible once
				valid = false;
				synchronized (refreshers) {
					refreshers.remove(this);
				}

				if (tileMap.isEmpty()) {
					return;
				}

				synchronized (cache) {
					// for each zoom level
					for (int zoom = minZoom; zoom <= tiles.getTotalMapZoom(); zoom++) {
						final TIntObjectHashMap<TIntObjectHashMap<SoftReference<BufferedImage>>> zoomCache = cache
								.get(zoom);
						final int fZoom = zoom;
						final Rectangle viewBounds = getCurrentViewBounds(zoom);

						if (zoomCache != null) {
							// if there are any cached tiles at the given zoom
							// level

							TIntObjectHashMap<TIntHashSet> zoomMap = tileMap.get(zoom);
							if (zoomMap != null) {

								// process tile map
								zoomMap.forEachEntry(new TIntObjectProcedure<TIntHashSet>() {

									@Override
									public boolean execute(int x, final TIntHashSet ySet) {
										final TIntObjectHashMap<SoftReference<BufferedImage>> xCache = zoomCache
												.get(x);

										if (xCache != null) {
											TIntIterator it = ySet.iterator();
											while (it.hasNext()) {
												int y = it.next();

												if (removeCached || !isCurrentZoom(fZoom)
														|| !insideView(viewBounds, x, y)) {
													// remove cached tile
													xCache.remove(y);
												}
												else {
													/*
													 * Only schedule repaint
													 * where the cache is not
													 * removed and at current
													 * zoom level.
													 */

													// only schedule repaint for
													// tiles that actually have
													// a cached image
													SoftReference<BufferedImage> imgRef = xCache
															.get(y);
													if (imgRef != null && imgRef.get() != null) {
														if (imageOp != null) {
															// apply image
															// operation to
															// invalidated image
															BufferedImage img = imgRef.get();
															if (img != null) {
																img = imageOp.filter(img, null);
																xCache.put(y,
																		new SoftReference<BufferedImage>(
																				img));
															}
														}

														int tilePosX = tileWidth * x;
														int tilePosY = tileHeight * y;
														TilePaintDelegate paint = new TilePaintDelegate(
																x, y, tilePosX, tilePosY, tileWidth,
																tileHeight, tiles.getConverter(),
																fZoom);
														scheduleTileRepaint(paint);
													}
												}
											}
										}

										return true;
									}

								});

							}
						}
					}
				}
				repaint();
			}
		}

		/**
		 * Determines if a tile is visible in the given view bounds.
		 * 
		 * @param viewBounds the view bounds, may be <code>null</code> if
		 *            unknown
		 * @param x the tile x ordinate
		 * @param y the tile y ordinate
		 * @return if the tile is visible in the view bounds
		 */
		protected boolean insideView(Rectangle viewBounds, int x, int y) {
			if (viewBounds == null) {
				return false;
			}

			if (x * tileWidth > viewBounds.x + viewBounds.width) {
				// tile lies to the right of the view
				return false;
			}
			if ((x + 1) * tileWidth < viewBounds.x) {
				// tile lies to the left of the view
				return false;
			}
			if (y * tileHeight > viewBounds.y + viewBounds.height) {
				// tile lies below the view
				return false;
			}
			if ((y + 1) * tileHeight < viewBounds.y) {
				// tile lies above the view
				return false;
			}

			return true;
		}

		/**
		 * Invalidate the refresher
		 */
		protected final void invalidate() {
			valid = false;
		}

	}

	private static final Log log = LogFactory.getLog(AbstractTileOverlayPainter.class);

	/**
	 * Default priority
	 */
	public static final int DEF_PRIORITY = 5;

	private final BufferedImage emptyImage = GraphicsUtilities.createCompatibleTranslucentImage(1,
			1);

	private final BufferedImage loadingImage;

	private final TIntObjectHashMap<TIntObjectHashMap<TIntObjectHashMap<SoftReference<BufferedImage>>>> cache = new TIntObjectHashMap<TIntObjectHashMap<TIntObjectHashMap<SoftReference<BufferedImage>>>>();

	private final Set<DefaultRefresher> refreshers = new HashSet<DefaultRefresher>();

	private TileProvider tiles;

	private boolean antialiasing = true;

	private int priority = DEF_PRIORITY;

	/**
	 * The executor for tile painting
	 */
	private final ExecutorService executor;

	private final Deque<TilePaintDelegate> repaintQueue = new LinkedList<TilePaintDelegate>();

	/**
	 * Constructor
	 * 
	 * @param numberOfThreads the number of worker threads to use for tile
	 *            painting
	 */
	public AbstractTileOverlayPainter(int numberOfThreads) {
		try {
			loadingImage = ImageIO
					.read(AbstractTileOverlayPainter.class.getResource("images/loading.png"));
		} catch (IOException e) {
			throw new IllegalStateException("Could not load loading image");
		}

		if (numberOfThreads > 1) {
			executor = Executors.newFixedThreadPool(4);
		}
		else {
			executor = Executors.newSingleThreadExecutor();
		}
	}

	/**
	 * @param antialiasing if antialiasing should be used for the painter
	 */
	public void setAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
	}

	/**
	 * @see TileOverlayPainter#setTileProvider(org.jdesktop.swingx.mapviewer.TileProvider)
	 */
	@Override
	public void setTileProvider(TileProvider tiles) {
		this.tiles = tiles;

		clearAll();
	}

	/**
	 * Clear all tiles and other temporary objects
	 */
	private void clearAll() {
		synchronized (refreshers) {
			// invalidate refreshers (important for tile provider change)
			for (DefaultRefresher refresher : refreshers) {
				refresher.invalidate();
			}

			refreshers.clear();
		}

		synchronized (cache) {
			cache.clear();
		}

		synchronized (repaintQueue) {
			repaintQueue.clear();
		}
	}

	/**
	 * Refresh all tiles
	 */
	public void refreshAll() {
		clearAll();
		repaint();
	}

	/**
	 * Prepare a refresh
	 * 
	 * @return the refresher
	 */
	public Refresher prepareRefresh() {
		return prepareRefresh(true);
	}

	/**
	 * Get if the given zoom level is the current zoom level
	 * 
	 * @param zoom the zoom level
	 * @return if it is the current zoom level
	 */
	protected abstract boolean isCurrentZoom(int zoom);

	/**
	 * Get the bounds of the current view of the map.
	 * 
	 * @param zoom the current assumed zoom level
	 * @return the view bounds (in world pixel coordinates) or <code>null</code>
	 *         if it is not known or the zoom level does not match
	 */
	protected abstract Rectangle getCurrentViewBounds(int zoom);

	/**
	 * Prepare a refresh
	 * 
	 * @param removeCached if the changed cached tiles shall be removed
	 * @return the refresher
	 */
	public Refresher prepareRefresh(boolean removeCached) {
		DefaultRefresher defaultRefresher = new DefaultRefresher(tiles, removeCached);
		synchronized (refreshers) {
			refreshers.add(defaultRefresher);
		}
		return defaultRefresher;
	}

	/**
	 * Initiate a repaint
	 */
	protected abstract void repaint();

	/**
	 * Get the maximum overlap for painted way-points in pixel
	 * 
	 * @return the maximum overlap for painted way-points in pixel
	 */
	protected abstract int getMaxOverlap();

	/**
	 * @see TileOverlayPainter#paintTile(Graphics2D, int, int, int, int, int,
	 *      int, int, PixelConverter, Rectangle)
	 */
	@Override
	public void paintTile(final Graphics2D gfx, final int x, final int y, final int zoom,
			final int tilePosX, final int tilePosY, final int tileWidth, final int tileHeight,
			final PixelConverter converter, Rectangle viewportBounds) {
		BufferedImage img;
		synchronized (this) {
			img = getCachedTile(x, y, zoom);

			if (img == null) {
				// start tile creation

				// temporarily set empty image TODO load image?
				img = loadingImage;
				cacheTile(x, y, zoom, img);

				TilePaintDelegate paint = new TilePaintDelegate(x, y, tilePosX, tilePosY, tileWidth,
						tileHeight, converter, zoom);
				scheduleTileRepaint(paint);
			}
		}

		if (img == loadingImage) {
			configureGraphics(gfx);
			gfx.drawImage(img, (tileWidth - img.getWidth()) / 2, (tileHeight - img.getHeight()) / 2,
					null);
		}
		else if (img != null && img != emptyImage) {
			configureGraphics(gfx);
			drawOverlay(gfx, img, zoom, tilePosX, tilePosY, tileWidth, tileHeight, viewportBounds,
					converter);
		}
	}

	/**
	 * Draws the overlay image on a tile.
	 * 
	 * @param gfx the graphics to draw the overlay on (with the origin being the
	 *            at the upper left corner of the tile)
	 * @param img the overlay image
	 * @param zoom the current zoom level
	 * @param tilePosX the tile x position in world pixel coordinates
	 * @param tilePosY the tile y position in world pixel coordinates
	 * @param tileWidth the tile width
	 * @param tileHeight the tile height
	 * @param viewportBounds the view-port bounds
	 * @param converter the pixel converter
	 */
	protected void drawOverlay(Graphics2D gfx, BufferedImage img, int zoom, int tilePosX,
			int tilePosY, int tileWidth, int tileHeight, Rectangle viewportBounds,
			PixelConverter converter) {
		gfx.drawImage(img, 0, 0, null);
	}

	/**
	 * Schedule a tile repaint after the {@link #repaintQueue} has been changed
	 * 
	 * @param paint the tile paint delegate
	 */
	private void scheduleTileRepaint(TilePaintDelegate paint) {
		synchronized (repaintQueue) {
			repaintQueue.addFirst(paint);
		}

		executor.execute(new Runnable() {

			@Override
			public void run() {
				TilePaintDelegate paint = null;
				synchronized (repaintQueue) {
					if (!repaintQueue.isEmpty()) {
						paint = repaintQueue.pop();
					}
				}
				if (paint != null) {
					paint.paintTile();
				}
			}
		});
	}

	/**
	 * Create a translucent image
	 * 
	 * @param width the width
	 * @param height the height
	 * @return a translucent image
	 */
	protected BufferedImage createImage(final int width, final int height) {
		return GraphicsUtilities.createCompatibleTranslucentImage(width, height);
	}

	private BufferedImage getCachedTile(int x, int y, int zoom) {
		BufferedImage result = null;

		synchronized (cache) {
			// cache per zoom
			final TIntObjectHashMap<TIntObjectHashMap<SoftReference<BufferedImage>>> zoomCache = cache
					.get(zoom);

			if (zoomCache != null) {
				// cache per x
				final TIntObjectHashMap<SoftReference<BufferedImage>> xCache = zoomCache.get(x);
				if (xCache != null) {
					// img cache
					final SoftReference<BufferedImage> imgCache = xCache.get(y);
					if (imgCache != null) {
						result = imgCache.get();
					}
				}
			}
		}

		return result;
	}

	private void cacheTile(int x, int y, int zoom, BufferedImage img) {
		synchronized (cache) {
			// cache per zoom
			TIntObjectHashMap<TIntObjectHashMap<SoftReference<BufferedImage>>> zoomCache = cache
					.get(zoom);
			if (zoomCache == null) {
				zoomCache = new TIntObjectHashMap<TIntObjectHashMap<SoftReference<BufferedImage>>>();
				cache.put(zoom, zoomCache);
			}

			// cache per x
			TIntObjectHashMap<SoftReference<BufferedImage>> xCache = zoomCache.get(x);
			if (xCache == null) {
				xCache = new TIntObjectHashMap<SoftReference<BufferedImage>>();
				zoomCache.put(x, xCache);
			}

			// image cache
			xCache.put(y, new SoftReference<BufferedImage>(img));
		}
	}

	/**
	 * Paint a tile. Use {@link #createImage(int, int)} to create an image to
	 * paint on.
	 * 
	 * @param posX the x position of the tile in world pixels
	 * @param posY the y position of the tile in world pixels
	 * @param width the tile width
	 * @param height the tile height
	 * @param converter the converter
	 * @param zoom the zoom level
	 * 
	 * @return the painted image tile or null (represents the empty image)
	 */
	public abstract BufferedImage repaintTile(int posX, int posY, int width, int height,
			PixelConverter converter, int zoom);

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
	 * Get the painter priority, painters with higher priority will be painted
	 * later
	 * 
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TileOverlayPainter other) {
		if (this == other)
			return 0;

		if (other instanceof AbstractTileOverlayPainter) {
			int priority = getPriority();
			int otherPriority = ((AbstractTileOverlayPainter) other).getPriority();

			if (priority < otherPriority) {
				return -1;
			}
			else if (priority > otherPriority) {
				return 1;
			}
		}

		int classCompare = getClass().getName().compareTo(other.getClass().getName());

		if (classCompare != 0) {
			return classCompare;
		}

		int hash = System.identityHashCode(this);
		int otherHash = System.identityHashCode(other);

		if (hash < otherHash)
			return -1;
		else if (hash > otherHash)
			return 1;

		return 0;
	}

	/**
	 * @see TileOverlayPainter#dispose()
	 */
	@Override
	public void dispose() {
		executor.shutdownNow();

		clearAll();
	}

}
