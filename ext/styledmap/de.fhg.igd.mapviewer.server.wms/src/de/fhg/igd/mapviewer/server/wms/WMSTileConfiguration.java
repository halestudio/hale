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

import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WMS configuration including tiling options
 * 
 * @author Simon Templer
 */
public abstract class WMSTileConfiguration extends WMSConfiguration {

	private static final Log log = LogFactory.getLog(WMSTileConfiguration.class);

	/**
	 * Default minimum map size
	 */
	public static final int DEFAULT_MIN_MAP_SIZE = 512;

	/**
	 * Default minimum tile size
	 */
	public static final int DEFAULT_MIN_TILE_SIZE = 256;

	/**
	 * Default zoom levels
	 */
	public static final int DEFAULT_ZOOM_LEVELS = 16;

	// preference names
	private static final String MIN_MAP_SIZE = "minMapSize"; //$NON-NLS-1$
	private static final String ZOOM_LEVELS = "zoomLevels"; //$NON-NLS-1$
	private static final String MIN_TILE_SIZE = "minTileSize"; //$NON-NLS-1$

	private int zoomLevels = DEFAULT_ZOOM_LEVELS;
	private int minTileSize = DEFAULT_MIN_TILE_SIZE;
	private int minMapSize = DEFAULT_MIN_MAP_SIZE;

	/**
	 * @see WMSConfiguration#validateSettings()
	 */
	@Override
	public boolean validateSettings() {
		try {
			new WMSTileProvider(getBaseUrl(), getPreferredEpsg(), zoomLevels, minTileSize,
					minMapSize, null);
			return true;
		} catch (Exception e) {
			log.error("Error validating wms settings", e); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Get the number of zoom levels
	 * 
	 * @return the number of zoom levels
	 */
	public int getZoomLevels() {
		return zoomLevels;
	}

	/**
	 * Set the number of zoom levels
	 * 
	 * @param zoomLevels the number of zoom levels
	 */
	public void setZoomLevels(int zoomLevels) {
		this.zoomLevels = zoomLevels;
	}

	/**
	 * Get the minimum tile size
	 * 
	 * @return the minimum tile size
	 */
	public int getMinTileSize() {
		return minTileSize;
	}

	/**
	 * Set the minimum tile size
	 * 
	 * @param minTileSize the minimum tile size
	 */
	public void setMinTileSize(int minTileSize) {
		this.minTileSize = minTileSize;
	}

	/**
	 * Get the minimum map size
	 * 
	 * @return the minimum map size
	 */
	public int getMinMapSize() {
		return minMapSize;
	}

	/**
	 * Set the minimum map size
	 * 
	 * @param minMapSize the minimum map size
	 */
	public void setMinMapSize(int minMapSize) {
		this.minMapSize = minMapSize;
	}

	/**
	 * @see WMSConfiguration#saveProperties(Preferences)
	 */
	@Override
	protected void saveProperties(Preferences node) {
		super.saveProperties(node);

		node.putInt(ZOOM_LEVELS, getZoomLevels());
		node.putInt(MIN_TILE_SIZE, getMinTileSize());
		node.putInt(MIN_MAP_SIZE, getMinMapSize());
	}

	/**
	 * @see WMSConfiguration#loadProperties(Preferences)
	 */
	@Override
	protected void loadProperties(Preferences node) {
		super.loadProperties(node);

		setZoomLevels(node.getInt(ZOOM_LEVELS, DEFAULT_ZOOM_LEVELS));
		setMinTileSize(node.getInt(MIN_TILE_SIZE, DEFAULT_MIN_TILE_SIZE));
		setMinMapSize(node.getInt(MIN_MAP_SIZE, DEFAULT_MIN_MAP_SIZE));
	}

}
