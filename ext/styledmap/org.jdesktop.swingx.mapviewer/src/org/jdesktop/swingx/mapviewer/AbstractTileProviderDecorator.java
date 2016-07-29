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

import java.net.URI;

import org.jdesktop.swingx.painter.Painter;

/**
 * AbstractTileProviderDecorator
 *
 * @author Simon Templer
 */
public abstract class AbstractTileProviderDecorator implements TileProvider {

	/**
	 * The decorated tile provider
	 */
	protected final TileProvider tileProvider;

	/**
	 * Constructor
	 * 
	 * @param tileProvider the tile provider to decorate
	 */
	public AbstractTileProviderDecorator(final TileProvider tileProvider) {
		this.tileProvider = tileProvider;
	}

	/**
	 * @see TileProvider#getAllowHorizontalWrapping()
	 */
	@Override
	public boolean getAllowHorizontalWrapping() {
		return tileProvider.getAllowHorizontalWrapping();
	}

	/**
	 * @see TileProvider#getConverter()
	 */
	@Override
	public PixelConverter getConverter() {
		return tileProvider.getConverter();
	}

	/**
	 * @see TileProvider#getDefaultZoom()
	 */
	@Override
	public int getDefaultZoom() {
		return tileProvider.getDefaultZoom();
	}

	/**
	 * @see TileProvider#getDrawTileBorders()
	 */
	@Override
	public boolean getDrawTileBorders() {
		return tileProvider.getDrawTileBorders();
	}

	/**
	 * @see TileProvider#getMapHeightInTiles(int)
	 */
	@Override
	public int getMapHeightInTiles(int zoom) {
		return tileProvider.getMapHeightInTiles(zoom);
	}

	/**
	 * @see TileProvider#getMapWidthInTiles(int)
	 */
	@Override
	public int getMapWidthInTiles(int zoom) {
		return tileProvider.getMapWidthInTiles(zoom);
	}

	/**
	 * @see TileProvider#getMaximumZoom()
	 */
	@Override
	public int getMaximumZoom() {
		return tileProvider.getMaximumZoom();
	}

	/**
	 * @see TileProvider#getMinimumZoom()
	 */
	@Override
	public int getMinimumZoom() {
		return tileProvider.getMinimumZoom();
	}

	/**
	 * @see TileProvider#getTileHeight(int)
	 */
	@Override
	public int getTileHeight(int zoom) {
		return tileProvider.getTileHeight(zoom);
	}

	/**
	 * @see TileProvider#getTileWidth(int)
	 */
	@Override
	public int getTileWidth(int zoom) {
		return tileProvider.getTileWidth(zoom);
	}

	/**
	 * @see TileProvider#getTileUris(int, int, int)
	 */
	@Override
	public URI[] getTileUris(int x, int y, int zoom) {
		return tileProvider.getTileUris(x, y, zoom);
	}

	/**
	 * @see TileProvider#getTotalMapZoom()
	 */
	@Override
	public int getTotalMapZoom() {
		return tileProvider.getTotalMapZoom();
	}

	/**
	 * @see TileProvider#getMapOverlayPainter()
	 */
	@Override
	public Painter<JXMapViewer> getMapOverlayPainter() {
		return tileProvider.getMapOverlayPainter();
	}

}
