/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.extension.internal;

import java.sql.Connection;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.DriverConfigurationExtension;
import eu.esdihumboldt.hale.io.jdbc.extension.JDBCSchemaReaderAdvisor;

/**
 * Extension for {@link JDBCSchemaReaderAdvisor}s.
 * 
 * @author Simon Templer
 */
public class SchemaReaderAdvisorExtension extends
		AbstractExtension<JDBCSchemaReaderAdvisor, SchemaReaderAdvisorDescriptor> {

	private static final ALogger log = ALoggerFactory.getLogger(SchemaReaderAdvisorExtension.class);

	/**
	 * Default factory for {@link JDBCSchemaReaderAdvisor}s.
	 */
	private static class ConfigurationFactory extends
			AbstractConfigurationFactory<JDBCSchemaReaderAdvisor> implements
			SchemaReaderAdvisorDescriptor {

		private final Class<?> connectionType;

		/**
		 * Create a factory/descriptor for a {@link JDBCSchemaReaderAdvisor}.
		 * 
		 * @param conf the configuration element
		 */
		protected ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");

			connectionType = ExtensionUtil.loadClass(conf, "connection");
		}

		@Override
		public void dispose(JDBCSchemaReaderAdvisor instance) {
			// nothing to do
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

		@Override
		public boolean applies(Connection connection) {
			return connectionType.isInstance(connection);
		}

	}

	private static SchemaReaderAdvisorExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the extension instance
	 */
	public static SchemaReaderAdvisorExtension getInstance() {
		if (instance == null) {
			instance = new SchemaReaderAdvisorExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	protected SchemaReaderAdvisorExtension() {
		super(DriverConfigurationExtension.EXTENSION_ID);
	}

	@Override
	protected SchemaReaderAdvisorDescriptor createFactory(IConfigurationElement conf)
			throws Exception {
		if ("readSchemaAdvisor".equals(conf.getName())) {
			return new ConfigurationFactory(conf);
		}
		return null;
	}

	/**
	 * Get the schema reader advisor applicable for the given connection.
	 * 
	 * @param connection the database connection
	 * @return the advisor or <code>null</code>
	 */
	@Nullable
	public JDBCSchemaReaderAdvisor getAdvisor(Connection connection) {
		for (SchemaReaderAdvisorDescriptor factory : getFactories()) {
			if (factory.applies(connection)) {
				try {
					return factory.createExtensionObject();
				} catch (Exception e) {
					log.error(
							"Could not create JDBC schema reader advisor "
									+ factory.getIdentifier(), e);
				}
			}
		}

		return null;
	}

}
