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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.jdbc.extension;

import java.net.URI;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;

/**
 * Extension for {@link ConnectionConfigurer}s.
 * 
 * @author Simon Templer
 */
public class DriverConfigurationExtension extends IdentifiableExtension<DriverConfiguration> {

	/**
	 * Identifier of the JDBC configuration extension point.
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.io.jdbc.config";

	private static DriverConfigurationExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the extension instance
	 */
	public static DriverConfigurationExtension getInstance() {
		if (instance == null) {
			instance = new DriverConfigurationExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	public DriverConfigurationExtension() {
		super(EXTENSION_ID);
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	@Override
	protected DriverConfiguration create(String elementId, IConfigurationElement element) {
		if (element.getName().equals("driver")) {
			return new DriverConfiguration(elementId, element);
		}
		return null;
	}

	/**
	 * To find driver using driver configuration
	 * 
	 * @param jdbcUri uri of JDBC connection
	 * @return {@link DriverConfiguration} or <code>null</code>
	 */
	public DriverConfiguration findDriver(URI jdbcUri) {
		for (DriverConfiguration config : getElements()) {
			if (config.matchURIPrefix(jdbcUri))
				return config;
		}
		return null;
	}

}
