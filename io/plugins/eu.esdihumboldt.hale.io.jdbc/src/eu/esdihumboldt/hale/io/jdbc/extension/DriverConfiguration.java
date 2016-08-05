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
import java.sql.Driver;
import java.sql.SQLException;

import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.base.Throwables;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * JDBC driver configuration.
 * 
 * @author Simon Templer
 */
public class DriverConfiguration implements Identifiable {

	private static final ALogger log = ALoggerFactory.getLogger(DriverConfiguration.class);

	private final String elementId;
	private final String name;
	private final String testURI;
	private final Class<? extends URIBuilder> builderClass;
	private URIBuilder builder = null;
	private final String className;
	private final IConfigurationElement[] prefixes;

	/**
	 * Create a connection configuration from a corresponding configuration
	 * element.
	 * 
	 * @param elementId the identifier
	 * @param element the configuration element
	 */
	@SuppressWarnings("unchecked")
	public DriverConfiguration(String elementId, IConfigurationElement element) {
		this.elementId = elementId;

		name = element.getAttribute("name");
		testURI = element.getAttribute("testUri");
		builderClass = (Class<? extends URIBuilder>) ExtensionUtil.loadClass(element, "uriBuilder");
		className = element.getAttribute("class");
		prefixes = element.getChildren("prefix");
	}

	@Override
	public String getId() {
		return elementId;
	}

	/**
	 * Determines if the configuration matches the given driver.
	 * 
	 * @param driver the JDBC driver to test
	 * @return if the driver matches the configuration
	 */
	public boolean matchesDriver(Driver driver) {
		try {
			return driver.acceptsURL(testURI);
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Get the URI builder associated with the driver configuration.
	 * 
	 * @return the URI builder
	 */
	public URIBuilder getURIBuilder() {
		if (builder == null) {
			try {
				builder = builderClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				Throwables.propagate(e);
			}
		}
		return builder;
	}

	/**
	 * @return the driver configuration display name
	 */
	public String getName() {
		return name;
	}

	/**
	 * match if configuration's any prefix match URI.
	 * 
	 * @param jdbcUri JDBCuri
	 * @return true if matches any of the prefix, otherwise false
	 */
	public boolean matchURIPrefix(URI jdbcUri) {
		String uri = jdbcUri.toString();
		for (IConfigurationElement prefix : prefixes)
			if (uri.startsWith(prefix.getAttribute("value")))
				return true;
		return false;
	}

	/**
	 * Loading a driver
	 * 
	 * @return Driver the {@link Driver} implements instance
	 * 
	 * @throws ClassNotFoundException throws a Class not found exception
	 */
	public Driver loadDriver() throws ClassNotFoundException {
		try {
			return (Driver) Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}

}
