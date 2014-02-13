/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Properties file that is in sync with its physical counterpart.
 * 
 * @author Simon Templer
 */
public class SyncedPropertiesFile {

	private static final ALogger log = ALoggerFactory.getLogger(SyncedPropertiesFile.class);

	/**
	 * Properties file storing the project configuration
	 */
	private final PropertiesFile properties;

	/**
	 * Create a synced properties file.
	 * 
	 * @param propertiesFile the properties file
	 * @param defaults the default properties, may be <code>null</code>
	 * @throws IOException if reading the file fails
	 */
	public SyncedPropertiesFile(File propertiesFile, Properties defaults) throws IOException {
		super();

		this.properties = new PropertiesFile(propertiesFile, defaults);
	}

	/**
	 * Manually sync.
	 * 
	 * @throws IOException if the synchronisation fails
	 */
	public void sync() throws IOException {
		properties.sync(false);
	}

	/**
	 * Get the internal properties.
	 * 
	 * @return the properties
	 */
	public PropertiesFile getProperties() {
		return properties;
	}

	/**
	 * Get the property value for the given key. Before retrieving the property
	 * value, the properties are synced with the file.
	 * 
	 * @param key the property key
	 * @return the property value or <code>null</code> if no default is found
	 * @throws IOException if syncing with the file fails.
	 */
	public String getProperty(String key) throws IOException {
		properties.sync(false);
		return properties.getProperty(key);
	}

	/**
	 * Get the property value for the given key. Before retrieving the property
	 * value, the properties are synced with the file, a failure is ignored.
	 * 
	 * @param key the property key
	 * @return the property value or <code>null</code> if no default is found
	 */
	public String getPropertyQuiet(String key) {
		try {
			properties.sync(false);
		} catch (IOException e) {
			// ignore, but log
			log.warn("Error syncing properties", e);
		}
		return properties.getProperty(key);
	}

	/**
	 * Set the property value for the given key. Before and after setting the
	 * property value, the properties are synced with the file.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @throws IOException if syncing with the file fails.
	 */
	public void setProperty(String key, String value) throws IOException {
		properties.sync(false);
		if (value == null) {
			properties.remove(key);
		}
		else {
			properties.setProperty(key, value);
		}
		properties.sync(true);
	}

	/**
	 * Set the property value for the given key. Before and after setting the
	 * property value, the properties are synced with the file, a failure is
	 * ignored.
	 * 
	 * @param key the property key
	 * @param value the property value
	 */
	public void setPropertyQuiet(String key, String value) {
		try {
			properties.sync(false);
		} catch (IOException e) {
			// ignore, but log
			log.warn("Error reading configuration", e);
		}
		if (value == null) {
			properties.remove(key);
		}
		else {
			properties.setProperty(key, value);
		}
		try {
			// save
			properties.sync(true);
		} catch (IOException e) {
			// ignore, but log
			log.error("Error writing configuration", e);
		}
	}

}
