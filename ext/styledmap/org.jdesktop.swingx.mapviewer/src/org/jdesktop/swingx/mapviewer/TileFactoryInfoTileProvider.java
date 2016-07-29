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
 * DefaultTileProvider
 *
 * @author Simon Templer
 */
@Deprecated
public class TileFactoryInfoTileProvider extends AbstractTileProvider {

	private final TileFactoryInfo info;

	private final GeoConverter geoConverter;

	/**
	 * Constructor
	 * 
	 * @param info the tile factory info
	 * @param geoConverter the geo converter
	 */
	public TileFactoryInfoTileProvider(TileFactoryInfo info, GeoConverter geoConverter) {
		super();

		this.info = info;
		this.geoConverter = geoConverter;
	}

	/**
	 * @see AbstractTileProvider#createConverter()
	 */
	@Override
	protected PixelConverter createConverter() {
		return new TileFactoryInfoConverter(info, geoConverter);
	}

	/**
	 * @see TileProvider#getDefaultZoom()
	 */
	@Override
	public int getDefaultZoom() {
		return info.getDefaultZoomLevel();
	}

	/**
	 * @see TileProvider#getMapHeightInTiles(int)
	 */
	@Override
	public int getMapHeightInTiles(int zoom) {
		// TileFactoryInfo doesn't support non square tile layout
		return info.getMapWidthInTilesAtZoom(zoom);
	}

	/**
	 * @see TileProvider#getMapWidthInTiles(int)
	 */
	@Override
	public int getMapWidthInTiles(int zoom) {
		return info.getMapWidthInTilesAtZoom(zoom);
	}

	/**
	 * @see TileProvider#getMaximumZoom()
	 */
	@Override
	public int getMaximumZoom() {
		return info.getMaximumZoomLevel();
	}

	/**
	 * @see TileProvider#getMinimumZoom()
	 */
	@Override
	public int getMinimumZoom() {
		return info.getMinimumZoomLevel();
	}

	/**
	 * @see TileProvider#getTileHeight(int)
	 */
	@Override
	public int getTileHeight(int zoom) {
		return info.getTileSize(zoom);
	}

	/**
	 * @see TileProvider#getTileWidth(int)
	 */
	@Override
	public int getTileWidth(int zoom) {
		return info.getTileSize(zoom);
	}

	/**
	 * @see TileProvider#getTileUris(int, int, int)
	 */
	@Override
	public URI[] getTileUris(int x, int y, int zoom) {
		try {
			String[] urls = info.getTileUrls(x, y, zoom);
			if (urls == null)
				return null;
			else {
				URI[] result = new URI[urls.length];
				for (int i = 0; i < urls.length; ++i) {
					result[i] = URI.create(urls[i]);
				}
				return result;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @see TileProvider#getTotalMapZoom()
	 */
	@Override
	public int getTotalMapZoom() {
		return info.getTotalMapZoom();
	}

}
