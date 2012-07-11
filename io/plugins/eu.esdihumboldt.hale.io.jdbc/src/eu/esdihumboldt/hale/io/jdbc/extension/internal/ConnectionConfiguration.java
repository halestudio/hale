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

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer;

/**
 * Applies a {@link ConnectionConfigurer}.
 * @author Simon Templer
 */
public class ConnectionConfiguration implements Identifiable {
	
	private static final ALogger log = ALoggerFactory.getLogger(ConnectionConfiguration.class);

	private String elementId;
	private Class<? extends Connection> connectionType;
	private Class<? extends ConnectionConfigurer<?>> configurerClass;

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
		
		connectionType = (Class<? extends Connection>) ExtensionUtil.loadClass(element, "type");
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
