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
package de.fhg.igd.mapviewer.server.wms;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.AbstractTileProvider;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.jdesktop.swingx.mapviewer.TileProvider;

import de.fhg.igd.mapviewer.server.LinearBoundsConverter;
import de.fhg.igd.mapviewer.server.wms.capabilities.Layer;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSBounds;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSCapabilities;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSCapabilitiesException;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSUtil;

/**
 * WMSTileProvider
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class WMSTileProvider extends AbstractTileProvider {

	private static final int EPSG_WGS84 = 4326;

	private static final Log log = LogFactory.getLog(WMSTileProvider.class);

	private static final Set<String> SUPPORTED_FORMATS = new LinkedHashSet<String>();

	static {
		SUPPORTED_FORMATS.add("image/png"); //$NON-NLS-1$
		SUPPORTED_FORMATS.add("image/jpeg"); //$NON-NLS-1$
		SUPPORTED_FORMATS.add("image/gif"); //$NON-NLS-1$
	}

	private final String serverUrl;

	private final int maxZoom;
	private final int totalMapZoom;
	private final String version;
	private String format = null;

	private int epsg;
	private double minX;
	private double minY;
	private double xRange;
	private double yRange;

	private final int xTileSize;
	private final int yTileSize;

	private String layerString;

	private final WMSCapabilities capabilities;

	/**
	 * Tile provider using WMS services
	 * 
	 * @param baseUrl the base URL for the GetCapabilities request
	 * @param preferredEpsg the EPSG code of preferred SRS
	 * @param zoomLevels the number of zoom levels
	 * @param minTileSize the minimum tile size
	 * @param minMapSize the minimum map size
	 * @param layers the layers to display
	 * 
	 * @throws WMSCapabilitiesException if reading the capabilities fails
	 */
	public WMSTileProvider(final String baseUrl, final int preferredEpsg, final int zoomLevels,
			final int minTileSize, final int minMapSize, final String layers)
					throws WMSCapabilitiesException {

		this.capabilities = WMSUtil.getCapabilities(baseUrl);

		// get server URL
		String mapUrl = capabilities.getMapURL();

		if (mapUrl.endsWith("?") || mapUrl.endsWith("&")) //$NON-NLS-1$ //$NON-NLS-2$
			serverUrl = mapUrl;
		else if (mapUrl.indexOf('?') >= 0)
			serverUrl = mapUrl + '&';
		else
			serverUrl = mapUrl + '?';

		log.info("GetMap URL: " + serverUrl); //$NON-NLS-1$

		// get bounding box
		WMSBounds bb = WMSUtil.getBoundingBox(capabilities, preferredEpsg);

		if (bb != null) {
			// determine parameters from bounding box
			try {
				epsg = Integer.parseInt(bb.getSRS().substring(5));
				minX = bb.getMinX();
				xRange = bb.getMaxX() - minX;
				minY = bb.getMinY();
				yRange = bb.getMaxY() - minY;
			} catch (Exception e) {
				// invalid bb
				log.error("Invalid bounding box", e); //$NON-NLS-1$
				bb = null;
			}
		}
		if (bb == null) {
			// no bounding boxes -> world wms
			log.warn("No valid bounding box found, creating world wms"); //$NON-NLS-1$
			// default tiles
			epsg = EPSG_WGS84;
			minX = -180;
			xRange = 360;
			minY = -90;
			yRange = 180;
		}

		log.info("Map bounds - epsg: " + epsg + ", minX: " + minX + ", xRange: " + xRange //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ ", minY: " + minY + ", yRange: " + yRange); //$NON-NLS-1$ //$NON-NLS-2$

		// determine tile sizes
		double tileRatio = xRange / yRange;
		if (tileRatio >= 1.0) {
			// xRange is bigger
			yTileSize = minTileSize;
			xTileSize = (int) (tileRatio * yTileSize);
		}
		else {
			// yRange is bigger
			xTileSize = minTileSize;
			yTileSize = (int) (xTileSize / tileRatio);
		}
		log.info("Tile size: " + xTileSize + "x" + yTileSize); //$NON-NLS-1$ //$NON-NLS-2$

		// determine format
		Iterable<String> formats = capabilities.getFormats();
		Iterator<String> itFormat = formats.iterator();
		while (format == null && itFormat.hasNext()) {
			String f = itFormat.next();
			if (SUPPORTED_FORMATS.contains(f))
				format = f;
		}
		if (format == null) {
			throw new WMSCapabilitiesException("No supported format found: " + formats); //$NON-NLS-1$
		}

		// zoom levels
		maxZoom = zoomLevels - 1;
		totalMapZoom = (int) (maxZoom
				+ (Math.log((double) minMapSize / (double) Math.max(xTileSize, yTileSize))
						/ Math.log(2)));

		// version
		version = capabilities.getVersion();

		// layers
		List<Layer> layerList = WMSUtil.getLayers(layers, capabilities);
		this.layerString = WMSUtil.getLayerString(layerList, true);
	}

	/**
	 * @see AbstractTileProvider#createConverter()
	 */
	@Override
	protected PixelConverter createConverter() {
		// TODO values for swapAxes, reverseX, reverseY?
		return new LinearBoundsConverter(GeotoolsConverter.getInstance(), this, minX, minY, xRange,
				yRange, epsg, false, false, true);
	}

	/**
	 * @see TileProvider#getDefaultZoom()
	 */
	@Override
	public int getDefaultZoom() {
		return maxZoom;
	}

	/**
	 * @see TileProvider#getMapHeightInTiles(int)
	 */
	@Override
	public int getMapHeightInTiles(int zoom) {
		return 1 << (totalMapZoom - zoom);
	}

	/**
	 * @see TileProvider#getMapWidthInTiles(int)
	 */
	@Override
	public int getMapWidthInTiles(int zoom) {
		return 1 << (totalMapZoom - zoom);
	}

	/**
	 * @see TileProvider#getMaximumZoom()
	 */
	@Override
	public int getMaximumZoom() {
		return maxZoom;
	}

	/**
	 * @see TileProvider#getMinimumZoom()
	 */
	@Override
	public int getMinimumZoom() {
		return 0;
	}

	/**
	 * @see TileProvider#getTileHeight(int)
	 */
	@Override
	public int getTileHeight(int zoom) {
		return yTileSize;
	}

	/**
	 * @see TileProvider#getTileUris(int, int, int)
	 */
	@Override
	public URI[] getTileUris(int x, int y, int zoom) {
		String styles = ""; //$NON-NLS-1$

		double geoTileWidth = xRange / getMapWidthInTiles(zoom);
		double geoTileHeight = yRange / getMapHeightInTiles(zoom);

		y = (getMapHeightInTiles(zoom) - 1) - y; // reverse y

		// Bounding Box needs lower left and upper right corner
		double lx = minX + geoTileWidth * x;
		double by = minY + geoTileHeight * y;
		double rx = minX + geoTileWidth * (x + 1);
		double ty = minY + geoTileHeight * (y + 1);

		String bbox = lx + "," + by + "," + rx + "," + ty; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// create query string
		String url = serverUrl + "VERSION=" + version + "&REQUEST=" + //$NON-NLS-1$ //$NON-NLS-2$
				"GetMap&SERVICE=WMS&Layers=" + layerString + //$NON-NLS-1$
				"&FORMAT=" + format + //$NON-NLS-1$
				"&BBOX=" + bbox + //$NON-NLS-1$
				"&WIDTH=" + xTileSize + "&HEIGHT=" + yTileSize + //$NON-NLS-1$ //$NON-NLS-2$
				"&SRS=EPSG:" + epsg + //$NON-NLS-1$
				"&STYLES=" + styles + //$NON-NLS-1$
				"&TRANSPARENT=FALSE" + //$NON-NLS-1$
		// "&BGCOLOR=0xffffff" +
		"&EXCEPTIONS=application/vnd.ogc.se_inimage" + //$NON-NLS-1$
				""; //$NON-NLS-1$

		return new URI[] { URI.create(url) };
	}

	/**
	 * @see TileProvider#getTileWidth(int)
	 */
	@Override
	public int getTileWidth(int zoom) {
		return xTileSize;
	}

	/**
	 * @see TileProvider#getTotalMapZoom()
	 */
	@Override
	public int getTotalMapZoom() {
		return totalMapZoom;
	}

}
