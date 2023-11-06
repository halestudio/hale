package org.jdesktop.swingx.mapviewer;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The <code>DefaultTileFactory</code> (was AbstractTileFactory) provides a
 * basic implementation for the TileFactory.
 * 
 * @author unknown
 * @author Simon Templer
 * 
 * @version $Id$
 */
public class DefaultTileFactory implements TileFactory {

	private static final Log log = LogFactory.getLog(DefaultTileFactory.class);

	private int threadPoolSize = 2;
	private ExecutorService service;

	private final TileProvider provider;

	// TODO the tile map should be static ALWAYS, regardless of the number
	// of GoogleTileFactories because each tile is, really, a singleton.
	private final Map<String, Tile> tileMap = new HashMap<String, Tile>();

	private TileCache cache;

	/**
	 * Create a {@link TileFactory} using a {@link TileProvider}
	 * 
	 * @param provider the {@link TileProvider}
	 * @param cache the tile cache to use
	 */
	public DefaultTileFactory(TileProvider provider, TileCache cache) {
		this.provider = provider;
		this.cache = cache;
	}

	/**
	 * Returns the tile that is located at the given tile point for this zoom.
	 * 
	 * @param x the tile's x coordinate
	 * @param y the tile's y coordinate
	 * @param zoom the zoom level
	 * 
	 * @return the tile for the given coordinates and zoom level
	 */
	@Override
	public Tile getTile(int x, int y, int zoom) {
		return getTile(x, y, zoom, true);
	}

	private Tile getTile(int tpx, int tpy, int zoom, boolean eagerLoad) {
		// wrap the tiles horizontally --> mod the X with the max width and use
		// that
		int tileX = tpx;

		int numTilesWide = provider.getMapWidthInTiles(zoom);

		if (tileX < 0) {
			tileX = numTilesWide - (Math.abs(tileX) % numTilesWide);
		}

		tileX = tileX % numTilesWide;

		int tileY = tpy;
		URI[] uris;
		if (TileProviderUtils.isValidTile(provider, tileX, tileY, zoom)) {
			try {
				uris = provider.getTileUris(tileX, tileY, zoom);
			} catch (Exception e) {
				uris = null;
				log.warn("Error getting tile urls", e);
			}
		}
		else {
			uris = null;
			log.warn(
					"Invalid tile requested: x = " + tileX + ", y = " + tileY + ", zoom = " + zoom);
		}

		Tile.Priority pri = Tile.Priority.High;
		if (!eagerLoad) {
			pri = Tile.Priority.Low;
		}
		Tile tile = null;

		if (uris == null || uris.length == 0) {
			// invalid/empty tile
			tile = new Tile(tileX, tileY, zoom);
		}
		else if (!tileMap.containsKey(uris[0].toString())) {
			// tile not found -> create new tile
			tile = new Tile(tileX, tileY, zoom, pri, this, uris);
			startLoading(tile);
			tileMap.put(uris[0].toString(), tile);
		}
		else {
			// retrieve tile from map
			tile = tileMap.get(uris[0].toString());
			// if its in the map but is low and isn't loaded yet
			// but we are in high mode
			if (tile.getPriority() == Tile.Priority.Low && eagerLoad && !tile.isLoaded()) {
				promote(tile);
			}
		}

		/*
		 * if (eagerLoad && doEagerLoading) { for (int i = 0; i<1; i++) { for
		 * (int j = 0; j<1; j++) { // preload the 4 tiles under the current one
		 * if(zoom > 0) { eagerlyLoad(tilePoint.getX()*2, tilePoint.getY()*2,
		 * zoom-1); eagerlyLoad(tilePoint.getX()*2+1, tilePoint.getY()*2,
		 * zoom-1); eagerlyLoad(tilePoint.getX()*2, tilePoint.getY()*2+1,
		 * zoom-1); eagerlyLoad(tilePoint.getX()*2+1, tilePoint.getY()*2+1,
		 * zoom-1); } } } }
		 */

		return tile;
	}

	/**
	 * Get the tile cache
	 * 
	 * @return the tile cache
	 */
	public TileCache getTileCache() {
		return cache;
	}

	/**
	 * Set the tile cache
	 * 
	 * @param cache the tile cache
	 */
	public void setTileCache(TileCache cache) {
		this.cache = cache;
	}

	/** ==== threaded tile loading stuff === */
	/**
	 * Thread pool for loading the tiles
	 */
	private final BlockingQueue<Tile> tileQueue = new PriorityBlockingQueue<Tile>(5,
			new Comparator<Tile>() {

				@Override
				public int compare(Tile o1, Tile o2) {
					if (o1.getPriority() == Tile.Priority.Low
							&& o2.getPriority() == Tile.Priority.High) {
						return 1;
					}
					if (o1.getPriority() == Tile.Priority.High
							&& o2.getPriority() == Tile.Priority.Low) {
						return -1;
					}
					return 0;

				}

				@Override
				public int hashCode() {
					return super.hashCode();
				}

				@Override
				public boolean equals(Object obj) {
					return obj == this;
				}
			});

	/**
	 * Subclasses may override this method to provide their own executor
	 * services. This method will be called each time a tile needs to be loaded.
	 * Implementations should cache the ExecutorService when possible.
	 * 
	 * @return ExecutorService to load tiles with
	 */
	protected synchronized ExecutorService getService() {
		if (service == null) {
			// System.out.println("creating an executor service with a
			// threadpool of size " + threadPoolSize);
			service = Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {

				private int count = 0;

				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r, "tile-pool-" + count++);
					t.setPriority(Thread.MIN_PRIORITY);
					t.setDaemon(true);
					return t;
				}
			});
		}
		return service;
	}

	/**
	 * Set the number of threads to use for loading the tiles. This controls the
	 * number of threads used by the ExecutorService returned from getService().
	 * Note, this method should be called before loading the first tile. Calls
	 * after the first tile are loaded will have no effect by default.
	 * 
	 * @param size the thread pool size
	 */
	public synchronized void setThreadPoolSize(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("size invalid: " + size
					+ ". The size of the threadpool must be greater than 0.");
		}
		threadPoolSize = size;
	}

	/**
	 * Start loading a tile
	 * 
	 * @param tile the tile to start loading
	 */
	@Override
	public synchronized void startLoading(Tile tile) {
		if (tile.isLoading()) {
			System.out.println("already loading. bailing");
			return;
		}
		tile.setLoading(true);
		try {
			tileQueue.put(tile);
			if (!getService().isShutdown()) {
				getService().submit(createTileRunner(tile));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Subclasses can override this if they need custom TileRunners for some
	 * reason
	 * 
	 * @param tile the tile to be loaded
	 * 
	 * @return a tile runner
	 */
	protected Runnable createTileRunner(TileInfo tile) {
		return new TileRunner();
	}

	/**
	 * Increase the priority of this tile so it will be loaded sooner.
	 * 
	 * @param tile the tile for which to increase the priority
	 */
	public synchronized void promote(Tile tile) {
		if (tileQueue.contains(tile)) {
			try {
				tileQueue.remove(tile);
				tile.setPriority(Tile.Priority.High);
				tileQueue.put(tile);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * An inner class which actually loads the tiles. Used by the thread queue.
	 * Subclasses can override this if necessary.
	 */
	public class TileRunner implements Runnable {

		/**
		 * @return the maximum number of tries to fetch a tile
		 */
		protected int maxTries() {
			return 3;
		}

		/**
		 * Called when loading failed and there are tries left
		 * 
		 * @param tile the tile to load
		 * @param triesLeft number of tries left
		 * @param error the error that occurred (if available)
		 */
		protected void onRetry(TileInfo tile, int triesLeft, Throwable error) {
			log.debug("Failed to load tile: " + tile.getURI() + " - Retrying", error);
		}

		/**
		 * Called when loading of a tile failed in all tries
		 * 
		 * @param tile the tile to load
		 * @param error the error that occurred last (if available)
		 */
		protected void onFailed(TileInfo tile, Throwable error) {
			log.warn("Failed to load tile: " + tile.getURI(), error);
		}

		/**
		 * Called when a tile was successfully loaded
		 * 
		 * @param tile the tile
		 */
		protected void onLoaded(TileInfo tile) {
			// do nothing
		}

		/**
		 * Called loading of a tile failed because there was not enough memory
		 * 
		 * @param tile the tile to load
		 */
		protected void onOutOfMem(TileInfo tile) {
			log.error("Failed to load tile: " + tile.getURI() + " - Not enough memory");
//    		cache.clear();
		}

		/**
		 * implementation of the Runnable interface.
		 */
		@Override
		public void run() {
			// get a tile out of the queue
			final Tile tile = tileQueue.remove();

			// XXX tries handled by HttpClient?
			int trys = maxTries();

			try {
				while (!tile.isLoaded() && trys > 0) {
					try {
						BufferedImage img = null;
						img = cache.get(tile);
						if (img == null) {
							trys--;

							if (trys == 0)
								// tile loading failed
								onFailed(tile, null);
							else {
								// give it another try
								onRetry(tile, trys, null);
								tile.notifyBeforeRetry();
							}
						}
						else {
							final BufferedImage i = img;
							SwingUtilities.invokeAndWait(new Runnable() {

								@Override
								public void run() {
									try {
										tile.image = new SoftReference<BufferedImage>(i);
										tile.setLoaded(true);
										// tile was succesfully loaded
										onLoaded(tile);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
									}
								}
							});
						}
					} catch (OutOfMemoryError memErr) {
						onOutOfMem(tile);
						trys = 0;
					} catch (Throwable e) {
						Object oldError = tile.getError();
						tile.setError(e);
						tile.firePropertyChangeOnEDT("loadingError", oldError, e);
						if (trys == 0) {
							tile.firePropertyChangeOnEDT("unrecoverableError", null, e);
						}
						else {
							trys--;
						}

						if (trys == 0)
							onFailed(tile, e);
						else
							onRetry(tile, trys, e);
					}
				}
			} finally {
				tile.setLoading(false);
			}
		}
	}

	/**
	 * @see TileFactory#getTileProvider()
	 */
	@Override
	public TileProvider getTileProvider() {
		return provider;
	}

	/**
	 * @see TileFactory#cleanup()
	 */
	@Override
	public void cleanup() {
		getService().shutdownNow();
		clearCache();
	}

	/**
	 * @see TileFactory#clearCache()
	 */
	@Override
	public void clearCache() {
		getTileCache().clear();
//		setTileCache(new DefaultTileCache());
	}
}
