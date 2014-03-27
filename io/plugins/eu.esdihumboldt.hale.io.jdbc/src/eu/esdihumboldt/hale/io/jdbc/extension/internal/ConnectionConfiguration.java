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

package eu.esdihumboldt.hale.io.jdbc.extension.internal;

import java.sql.Connection;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer;

/**
 * Applies a {@link ConnectionConfigurer}.
 * @author Simon Templer
 */
public class ConnectionConfiguration implements Identifiable {
	
	private static final ALogger log = ALoggerFactory.getLogger(ConnectionConfiguration.class);

	private final String elementId;
	private final Class<?> connectionType;
	private final Class<? extends ConnectionConfigurer<?>> configurerClass;

	/**
	 * Create a connection configuration from a corresponding 
	 * configuration element.
	 * @param elementId the identifier
	 * @param element the configuration element
	 */
	@SuppressWarnings("unchecked")
	public ConnectionConfiguration(String elementId,
			IConfigurationElement element) {
		this.elementId = elementId;
		
		connectionType = ExtensionUtil.loadClass(element, "type");
		configurerClass = (Class<? extends ConnectionConfigurer<?>>) ExtensionUtil.loadClass(element, "configurer");
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable#getId()
	 */
	@Override
	public String getId() {
		return elementId;
	}
	
	/**
	 * Apply the configuration to a connection. Does nothing
	 * if the configuration is not applicable for this type of
	 * connection.
	 * @param connection the database connection
	 */
	@SuppressWarnings("unchecked")
	public void apply(Connection connection) {
		if (connectionType.isInstance(connection)) {
			// create configurer
			try {
				@SuppressWarnings("rawtypes")
				ConnectionConfigurer configurer = configurerClass.newInstance();
				configurer.configureConnection(connection);
			} catch (Exception e) {
				log.error("Failed to create configurer for database connection.", e);
			}
		}
	}

}
