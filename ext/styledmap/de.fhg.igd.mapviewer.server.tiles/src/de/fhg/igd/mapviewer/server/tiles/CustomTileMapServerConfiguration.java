/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package de.fhg.igd.mapviewer.server.tiles;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.fhg.igd.mapviewer.server.AbstractMapServer;

/**
 * abstract configuration of custom tile map server. It comprise methods to load
 * and save map server information using preferences.
 * 
 * @author Arun
 */
public abstract class CustomTileMapServerConfiguration extends AbstractMapServer {

	private static final Log log = LogFactory.getLog(CustomTileMapServerConfiguration.class);

	private static final String URL_PATTERN = "urlPattern";
	private static final String ATTRIBUTION = "attribution";
	private static final String ZOOM_LEVELS = "zoomLevels";

	/**
	 * Default zoom levels
	 */
	public static final int DEFAULT_ZOOM_LEVELS = 16;

	/**
	 * URL pattern
	 */
	private String urlPattern;

	/**
	 * zoom level for CustomTileMapServer
	 */
	private int zoomLevel = DEFAULT_ZOOM_LEVELS;

	/**
	 * Attribution Text
	 */
	private String attributionText;

	/**
	 * The preferences
	 */
	private static final Preferences PREF_SERVERS = Preferences
			.userNodeForPackage(CustomTileMapServer.class).node("customtiles");

	/**
	 * Determines if a configuration name already exists
	 * 
	 * @param name the configuration name
	 * @return if the name already exists
	 * @throws BackingStoreException if an error occurs accessing the
	 *             preferences
	 */
	public boolean nameExists(String name) throws BackingStoreException {
		return getPreferences().nodeExists(name);
	}

	/**
	 * Preferences for CustomTileMapServer
	 * 
	 * @return {@link Preferences} of custom Tile Map Server
	 */
	public Preferences getPreferences() {
		return PREF_SERVERS;
	}

	/**
	 * Remove the configuration with the given name
	 * 
	 * @param name the name
	 * 
	 * @return if removing the configuration succeeded
	 */
	public static boolean removeConfiguration(String name) {
		try {
			PREF_SERVERS.node(name).removeNode();
			return true;
		} catch (BackingStoreException e) {
			log.error("Error removing configuration " + name, e); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Get the names of the existing configurations
	 * 
	 * @return the configuration names
	 */
	public static String[] getConfigurationNames() {
		try {
			return PREF_SERVERS.childrenNames();
		} catch (BackingStoreException e) {
			return new String[] {};
		}
	}

	/**
	 * Save the configuration
	 * 
	 */
	public void save() {
		Preferences preferences = getPreferences();

		try {
			String name = getName();
			Preferences node = preferences.node(name);
			setName(name);

			saveProperties(node);

			node.flush();
		} catch (BackingStoreException e) {
			log.error("Error saving map server preferences", e); //$NON-NLS-1$
		}
	}

	/**
	 * Load configuration with the given name
	 * 
	 * @param name the configuration name
	 * 
	 * @return if loading the configuration succeeded
	 */
	public boolean load(String name) {
		Preferences preferences = getPreferences();

		try {
			if (preferences.nodeExists(name)) {
				Preferences node = preferences.node(name);

				setName(name);

				loadProperties(node);

				return true;
			}
			else {
				log.warn("No configuration named " + name + " found"); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
		} catch (BackingStoreException e) {
			log.error("Error loading CustomTile configuration"); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * @return the urlPattern
	 */
	public String getUrlPattern() {
		return urlPattern;
	}

	/**
	 * @param urlPattern the urlPattern to set
	 */
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	/**
	 * @return the zoomLevel
	 */
	public int getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * @param zoomLevel the zoomLevel to set
	 */
	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	/**
	 * @return the attributionText
	 */
	public String getAttributionText() {
		return attributionText;
	}

	/**
	 * @param attributionText the attributionText to set
	 */
	public void setAttributionText(String attributionText) {
		this.attributionText = attributionText;
	}

	/**
	 * Load the configuration's properties
	 * 
	 * @param node the preference node
	 */
	protected void loadProperties(Preferences node) {
		setUrlPattern(node.get(URL_PATTERN, null));
		setZoomLevel(node.getInt(ZOOM_LEVELS, DEFAULT_ZOOM_LEVELS));
		setAttributionText(node.get(ATTRIBUTION, null));
	}

	/**
	 * Save the configuration's properties to the given preference node
	 * 
	 * @param node the preference node
	 */
	protected void saveProperties(Preferences node) {
		node.put(URL_PATTERN, getUrlPattern());
		node.putInt(ZOOM_LEVELS, getZoomLevel());
		node.put(ATTRIBUTION, getAttributionText());
	}

}
