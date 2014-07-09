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

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer;

/**
 * Extension for {@link ConnectionConfigurer}s.
 * @author Simon Templer
 */
public class ConnectionConfigurerExtension extends IdentifiableExtension<ConnectionConfiguration> {
	
	/**
	 * Identifier of the JDBC configuration extension point.
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.io.jdbc.config";
	
	private static ConnectionConfigurerExtension instance;
	
	/**
	 * Get the extension instance.
	 * @return the extension instance
	 */
	public static ConnectionConfigurerExtension getInstance() {
		if (instance == null) {
			instance = new ConnectionConfigurerExtension();
		}
		return instance;
	}
	
	/**
	 * Default constructor
	 */
	public ConnectionConfigurerExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension#create(java.lang.String, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected ConnectionConfiguration create(String elementId,
			IConfigurationElement element) {
		if (element.getName().equals("connection")) {
			return new ConnectionConfiguration(elementId, element);
		}
		return null;
	}
	
	/**
	 * Apply the configurations to a connection. Nothing is done for 
	 * configurations that are not applicable for this type of
	 * connection.
	 * @param connection the database connection
	 */
	public void applyAll(Connection connection) {
		for (ConnectionConfiguration conf : getElements()) {
			conf.apply(connection);
		}
	}

}
