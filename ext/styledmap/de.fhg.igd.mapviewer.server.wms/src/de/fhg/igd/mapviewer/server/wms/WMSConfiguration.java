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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.fhg.igd.mapviewer.server.wms.capabilities.WMSUtil;

/**
 * Basic WMS client configuration
 * 
 * @author Simon Templer
 */
public abstract class WMSConfiguration {

	private static final Log log = LogFactory.getLog(WMSConfiguration.class);

	/**
	 * Any SRS
	 */
	public static final int DEFAULT_PREFERRED_EPSG = 0;

	// preference names
	private static final String PREFERRED_EPSG = "preferredEpsg"; //$NON-NLS-1$
	private static final String BASE_URL = "baseUrl"; //$NON-NLS-1$
	private static final String LAYERS = "layers"; //$NON-NLS-1$

	private String name = ""; //$NON-NLS-1$
	private String baseUrl;
	private int preferredEpsg = DEFAULT_PREFERRED_EPSG;

	private String layers = ""; //$NON-NLS-1$

	/**
	 * Validate the configuration
	 * 
	 * @return if the configuration is valid
	 */
	public boolean validateSettings() {
		try {
			WMSUtil.getCapabilities(baseUrl);
			return true;
		} catch (Exception e) {
			log.error("Error validating wms settings", e); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Get the configuration name
	 * 
	 * @return the configuration name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the configuration name
	 * 
	 * @param name the configuration name
	 */
	public void setName(String name) {
		this.name = name;
	}

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
	 * Get the base URL
	 * 
	 * @return the base URL
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Set the base URL
	 * 
	 * @param baseUrl the base URL
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * Get the EPSG code of the preferred SRS
	 * 
	 * @return the EPSG code of the preferred SRS, zero if no SRS is preferred
	 */
	public int getPreferredEpsg() {
		return preferredEpsg;
	}

	/**
	 * Set the EPSG code of the preferred SRS
	 * 
	 * @param preferredEpsg the EPSG code, zero stands for any SRS
	 */
	public void setPreferredEpsg(int preferredEpsg) {
		this.preferredEpsg = preferredEpsg;
	}

	/**
	 * Get the configured layers
	 * 
	 * @return the layers
	 */
	public String getLayers() {
		return layers;
	}

	/**
	 * Set the configured layers
	 * 
	 * @param layers the layers
	 */
	public void setLayers(String layers) {
		this.layers = layers;
	}

	/**
	 * Save the configuration
	 * 
	 * @param overwrite if old settings/servers with the same name shall be
	 *            overridden
	 */
	public void save(boolean overwrite) {
		Preferences preferences = getPreferences();

		try {
			String name = getName();

			if (!overwrite) {
				int i = 1;
				// find unique name
				while (preferences.nodeExists(name)) {
					name = getName() + "_" + i; //$NON-NLS-1$
					i++;
				}
			}

			Preferences node = preferences.node(name);
			setName(name);

			saveProperties(node);

			node.flush();
		} catch (BackingStoreException e) {
			log.error("Error saving map server preferences", e); //$NON-NLS-1$
		}
	}

	/**
	 * Get the preferences where the configurations are saved
	 * 
	 * @return the preferences
	 */
	protected abstract Preferences getPreferences();

	/**
	 * Save the configuration's properties to the given preference node
	 * 
	 * @param node the preference node
	 */
	protected void saveProperties(Preferences node) {
		node.put(BASE_URL, getBaseUrl());
		node.putInt(PREFERRED_EPSG, getPreferredEpsg());
		node.put(LAYERS, getLayers());
	}

	/**
	 * Load a WMS configuration with the given name
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
			log.error("Error loading WMS configuration"); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Load the configuration's properties
	 * 
	 * @param node the preference node
	 */
	protected void loadProperties(Preferences node) {
		setBaseUrl(node.get(BASE_URL, null));
		setPreferredEpsg(node.getInt(PREFERRED_EPSG, DEFAULT_PREFERRED_EPSG));
		setLayers(node.get(LAYERS, "")); //$NON-NLS-1$
	}
}
