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
package de.fhg.igd.mapviewer.server;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileInfo;
import org.jdesktop.swingx.mapviewer.TileProvider;

/**
 * StatusTileFactory
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class StatusTileFactory extends DefaultTileFactory {

	/**
	 * Tile load listener
	 */
	public interface TileLoadListener {

		/**
		 * Called when the tiles have been reseted
		 */
		public void reset();

		/**
		 * Called when the loading of a tile has started
		 * 
		 * @param tile the tile
		 */
		public void loadingStarted(TileInfo tile);

		/**
		 * Called when the loading of a tile has finished
		 * 
		 * @param tile the tile
		 */
		public void loadingFinished(TileInfo tile);

		/**
		 * Called when the loading of a tile has failed
		 * 
		 * @param tile the tile
		 * @param error the error
		 */
		public void loadingFailed(TileInfo tile, Throwable error);

		/**
		 * Called when the loading of a tile is retried
		 * 
		 * @param tile the tile
		 */
		public void loadingRetry(TileInfo tile);

	}

	private static final Log log = LogFactory.getLog(StatusTileFactory.class);

	private static final Set<TileLoadListener> tileLoadListeners = new HashSet<TileLoadListener>();

	/**
	 * Adds a tile load listener
	 * 
	 * @param listener the listener
	 */
	public static void addTileLoadListener(TileLoadListener listener) {
		tileLoadListeners.add(listener);
	}

	/**
	 * Removes a tile load listener
	 * 
	 * @param listener the listener
	 */
	public static void removeTileLoadListener(TileLoadListener listener) {
		tileLoadListeners.remove(listener);
	}

	private synchronized static void fireReset() {
		log.debug("Tile load reset"); //$NON-NLS-1$

		for (TileLoadListener l : tileLoadListeners) {
			l.reset();
		}
	}

	private synchronized static void fireLoadingStarted(final TileInfo tile) {
		// log.debug("Tile loading started");

		for (TileLoadListener l : tileLoadListeners) {
			l.loadingStarted(tile);
		}
	}

	private synchronized static void fireLoadingFinished(final TileInfo tile) {
		// log.debug("Tile loading finished");

		for (TileLoadListener l : tileLoadListeners) {
			l.loadingFinished(tile);
		}
	}

	private synchronized static void fireLoadingFailed(final TileInfo tile, final Throwable error) {
		// log.warn("Tile loading failed", error);

		for (TileLoadListener l : tileLoadListeners) {
			l.loadingFailed(tile, error);
		}
	}

	private synchronized static void fireLoadingRetry(final TileInfo tile) {
		log.debug("Tile loading retry"); //$NON-NLS-1$

		for (TileLoadListener l : tileLoadListeners) {
			l.loadingRetry(tile);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param tileProvider the tile provider
	 * @param cache the tile cache
	 */
	public StatusTileFactory(TileProvider tileProvider, TileCache cache) {
		super(tileProvider, cache);

		fireReset();
	}

	private class StatusTileRunner extends TileRunner {

		@Override
		protected void onFailed(TileInfo tile, Throwable error) {
			super.onFailed(tile, error);

			fireLoadingFailed(tile, error);
		}

		@Override
		protected void onLoaded(TileInfo tile) {
			super.onLoaded(tile);

			fireLoadingFinished(tile);
		}

		@Override
		protected void onOutOfMem(TileInfo tile) {
			super.onOutOfMem(tile);

			fireLoadingFailed(tile, null);
		}

		@Override
		protected void onRetry(TileInfo tile, int triesLeft, Throwable error) {
			super.onRetry(tile, triesLeft, error);

			fireLoadingRetry(tile);
		}

	}

	/**
	 * @see DefaultTileFactory#createTileRunner(TileInfo)
	 */
	@Override
	protected Runnable createTileRunner(TileInfo tile) {
		fireLoadingStarted(tile);
		return new StatusTileRunner();
	}

	/**
	 * @see DefaultTileFactory#cleanup()
	 */
	@Override
	public void clearCache() {
		super.clearCache();

		fireReset();
	}

}
