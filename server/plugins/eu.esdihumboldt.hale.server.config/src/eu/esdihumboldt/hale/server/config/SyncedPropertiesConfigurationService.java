/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.server.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.configuration.AbstractConfigurationService;
import eu.esdihumboldt.util.SyncedPropertiesFile;

/**
 * Configuration service based on a properties file.
 * 
 * @author Simon Templer
 */
public class SyncedPropertiesConfigurationService extends AbstractConfigurationService {

	private static final ALogger log = ALoggerFactory
			.getLogger(SyncedPropertiesConfigurationService.class);

	private static final String SYSTEM_PROPERTY_SERVER_CONFIG = "hale.server.config";

	private final SyncedPropertiesFile properties;

	/**
	 * Create a configuration service based a default properties file, with the
	 * system properties as default values.
	 * 
	 * The properties file location is defined by the system property
	 * {@value #SYSTEM_PROPERTY_SERVER_CONFIG} if present or a default location
	 * in the instance location is used.
	 * 
	 * @throws IOException if accessing the properties file fails
	 */
	public SyncedPropertiesConfigurationService() throws IOException {
		this(null);
	}

	/**
	 * Create a configuration service based on the given properties file, with
	 * the system properties as default values.
	 * 
	 * @param propertiesFile the properties file or <code>null</code> if a
	 *            location defined by the system property
	 *            {@value #SYSTEM_PROPERTY_SERVER_CONFIG} should be used if
	 *            present or a default location in the instance location
	 * @throws IOException if accessing the properties file fails
	 */
	public SyncedPropertiesConfigurationService(File propertiesFile) throws IOException {
		this(determineConfigFile(propertiesFile, "server.properties"), System.getProperties());
	}

	private static File determineConfigFile(File propertiesFile, String instanceLocPath) {
		// try system property
		String path = System.getProperty(SYSTEM_PROPERTY_SERVER_CONFIG);
		if (path != null && !path.isEmpty()) {
			propertiesFile = new File(path);
		}

		// try default location
		if (propertiesFile == null && instanceLocPath != null) {
			// use default location
			Location location = Platform.getInstanceLocation();
			if (location != null) {
				try {
					File instanceLoc = new File(URI.create(location.getURL().toString()
							.replaceAll(" ", "%20")));
					propertiesFile = new File(instanceLoc, instanceLocPath);
					if (!propertiesFile.exists()) {
						propertiesFile.createNewFile();
					}
				} catch (Exception e) {
					throw new IllegalStateException(
							"Unable to determine instance location, can't initialize configuration service.",
							e);
				}
			}
			else {
				throw new IllegalStateException(
						"No instance location, can't initialize configuration service.");
			}
		}

		if (propertiesFile == null) {
			throw new IllegalStateException("No server configuration file defined.");
		}

		log.info("Server configuration file is " + propertiesFile.getAbsolutePath());
		return propertiesFile;
	}

	/**
	 * Create a configuration service based on the given properties file and the
	 * given default properties.
	 * 
	 * @param propertiesFile the properties file
	 * @param defaults the default properties, may be <code>null</code>
	 * @throws IOException if accessing the properties file fails
	 */
	public SyncedPropertiesConfigurationService(File propertiesFile, Properties defaults)
			throws IOException {
		super();
		this.properties = new SyncedPropertiesFile(propertiesFile, defaults);
	}

	@Override
	protected String getValue(String key) {
		return properties.getPropertyQuiet(key);
	}

	@Override
	protected void removeValue(String key) {
		properties.setPropertyQuiet(key, null);
	}

	@Override
	protected void setValue(String key, String value) {
		properties.setPropertyQuiet(key, value);
	}

}
