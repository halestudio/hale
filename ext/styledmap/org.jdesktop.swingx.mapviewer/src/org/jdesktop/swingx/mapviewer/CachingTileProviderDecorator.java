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

/**
 * CachingTileProviderDecorator
 *
 * @author Simon Templer
 */
public class CachingTileProviderDecorator extends AbstractTileProviderDecorator {

	private final int[] width;
	private final int[] height;
	private final int[] tileWidth;
	private final int[] tileHeight;

	/**
	 * @param tileProvider the wrapped tile provider
	 */
	public CachingTileProviderDecorator(TileProvider tileProvider) {
		super(tileProvider);

		width = new int[tileProvider.getMaximumZoom() - tileProvider.getMinimumZoom() + 1];
		height = new int[tileProvider.getMaximumZoom() - tileProvider.getMinimumZoom() + 1];
		tileWidth = new int[tileProvider.getMaximumZoom() - tileProvider.getMinimumZoom() + 1];
		tileHeight = new int[tileProvider.getMaximumZoom() - tileProvider.getMinimumZoom() + 1];

		for (int i = 0; i <= tileProvider.getMaximumZoom() - tileProvider.getMinimumZoom(); i++) {
			// preget width, height and tilesize values
			int zoom = i + tileProvider.getMinimumZoom();
			width[i] = tileProvider.getMapWidthInTiles(zoom);
			height[i] = tileProvider.getMapHeightInTiles(zoom);
			tileWidth[i] = tileProvider.getTileWidth(zoom);
			tileHeight[i] = tileProvider.getTileHeight(zoom);
		}
	}

	/**
	 * @see AbstractTileProviderDecorator#getMapHeightInTiles(int)
	 */
	@Override
	public int getMapHeightInTiles(int zoom) {
		int index = zoom - tileProvider.getMinimumZoom();
		if (index >= 0 && index < height.length)
			return height[index];
		else
			return tileProvider.getMapHeightInTiles(zoom);
	}

	/**
	 * @see AbstractTileProviderDecorator#getMapWidthInTiles(int)
	 */
	@Override
	public int getMapWidthInTiles(int zoom) {
		int index = zoom - tileProvider.getMinimumZoom();
		if (index >= 0 && index < width.length)
			return width[index];
		else
			return tileProvider.getMapWidthInTiles(zoom);
	}

	/**
	 * @see AbstractTileProviderDecorator#getTileHeight(int)
	 */
	@Override
	public int getTileHeight(int zoom) {
		int index = zoom - tileProvider.getMinimumZoom();
		if (index >= 0 && index < tileHeight.length)
			return tileHeight[index];
		else
			return tileProvider.getTileHeight(zoom);
	}

	/**
	 * @see AbstractTileProviderDecorator#getTileWidth(int)
	 */
	@Override
	public int getTileWidth(int zoom) {
		int index = zoom - tileProvider.getMinimumZoom();
		if (index >= 0 && index < tileWidth.length)
			return tileWidth[index];
		else
			return tileProvider.getTileWidth(zoom);
	}

	/**
	 * @see AbstractTileProviderDecorator#getTileUris(int, int, int)
	 */
	@Override
	public URI[] getTileUris(int x, int y, int zoom) {
		// TODO cache tile uris?
		return super.getTileUris(x, y, zoom);
	}

}
