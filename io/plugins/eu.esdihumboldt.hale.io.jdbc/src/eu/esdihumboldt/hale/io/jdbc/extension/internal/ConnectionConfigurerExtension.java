/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.jdbc.extension.internal;

import java.sql.Connection;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
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
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#create(java.lang.String, org.eclipse.core.runtime.IConfigurationElement)
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
