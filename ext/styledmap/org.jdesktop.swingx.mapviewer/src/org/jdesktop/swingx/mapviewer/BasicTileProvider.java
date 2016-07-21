/*+-------------+----------------------------------------------------------*
 *|  |  |_|_|_|_|   Fraunhofer-Institut fuer Graphische Datenverarbeitung  *
 *|__|__|_|_|_|_|     (Fraunhofer Institute for Computer Graphics)         *
 *|  |  |_|_|_|_|                                                          *
 *|__|__|_|_|_|_|                                                          *
 *|  __ |    ___|                                                          *
 *| /_  /_  / _ |     Fraunhoferstrasse 5                                  *
 *|/   / / /__/ |     D-64283 Darmstadt, Germany                           *
 *+-------------+----------------------------------------------------------*/
package org.jdesktop.swingx.mapviewer;

/**
 * BasicTileProvider
 *
 * @author Simon Templer
 */
public abstract class BasicTileProvider extends AbstractTileProvider {

	private final int defaultZoom;
	private final int minimumZoom;
	private final int maximumZoom;
	private final int totalMapZoom;

	private final int tileWidth;
	private final int tileHeight;

	/**
	 * Constructor
	 * 
	 * @param defaultZoom the default zoom level
	 * @param minimumZoom the minimum zoom level
	 * @param maximumZoom the maximum zoom level
	 * @param totalMapZoom the top zoom level
	 * @param tileWidth the tile width
	 * @param tileHeight the tile height
	 */
	public BasicTileProvider(final int defaultZoom, final int minimumZoom, final int maximumZoom,
			final int totalMapZoom, final int tileWidth, final int tileHeight) {
		super();
		this.defaultZoom = defaultZoom;
		this.minimumZoom = minimumZoom;
		this.maximumZoom = maximumZoom;
		this.totalMapZoom = totalMapZoom;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	/**
	 * @see TileProvider#getDefaultZoom()
	 */
	@Override
	public int getDefaultZoom() {
		return defaultZoom;
	}

	/**
	 * @see TileProvider#getMaximumZoom()
	 */
	@Override
	public int getMaximumZoom() {
		return maximumZoom;
	}

	/**
	 * @see TileProvider#getMinimumZoom()
	 */
	@Override
	public int getMinimumZoom() {
		return minimumZoom;
	}

	/**
	 * @see TileProvider#getTileHeight(int)
	 */
	@Override
	public int getTileHeight(int zoom) {
		return tileHeight;
	}

	/**
	 * @see TileProvider#getTileWidth(int)
	 */
	@Override
	public int getTileWidth(int zoom) {
		return tileWidth;
	}

	/**
	 * @see TileProvider#getTotalMapZoom()
	 */
	@Override
	public int getTotalMapZoom() {
		return totalMapZoom;
	}

}
