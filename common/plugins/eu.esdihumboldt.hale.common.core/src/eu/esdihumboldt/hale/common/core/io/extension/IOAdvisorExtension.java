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

package eu.esdihumboldt.hale.common.core.io.extension;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOAdvisorRegister;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Extension for {@link IOAdvisor}s
 * 
 * @author Simon Templer
 */
public class IOAdvisorExtension extends AbstractExtension<IOAdvisor<?>, IOAdvisorFactory>
		implements IOAdvisorRegister {

	/**
	 * Factory for {@link IOAdvisor} based on an {@link IConfigurationElement}
	 */
	private static class ConfigurationFactory extends AbstractConfigurationFactory<IOAdvisor<?>>
			implements IOAdvisorFactory {

		/**
		 * Create an {@link IOAdvisor} factory basd on the given configuration
		 * 
		 * @param conf the configuration element
		 */
		protected ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(IOAdvisor<?> advisor) {
			// do nothing
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			// XXX instead return action name?
			return getIdentifier();
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		/**
		 * @see IOAdvisorFactory#getActionID()
		 */
		@Override
		public String getActionID() {
			return conf.getAttribute("action");
		}

		@Override
		public IOAdvisor<?> createExtensionObject() throws Exception {
			throw new IllegalStateException(
					"Creating an I/O advisor w/o service provider forbidden");
		}

		@Override
		public IOAdvisor<?> createAdvisor(ServiceProvider serviceProvider) throws Exception {
			if (serviceProvider == null)
				throw new IllegalArgumentException(
						"Service provider must be specified when creating an I/O advisor");

			IOAdvisor<?> advisor = super.createExtensionObject();
			advisor.setServiceProvider(serviceProvider);
			return advisor;
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(IOAdvisorExtension.class);

	private static IOAdvisorExtension instance;

	/**
	 * Get the I/O advisor extension instance
	 * 
	 * @return the extension instance
	 */
	public static IOAdvisorExtension getInstance() {
		if (instance == null) {
			instance = new IOAdvisorExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private IOAdvisorExtension() {
		super(IOActionExtension.ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected IOAdvisorFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("advisor")) {
			return new ConfigurationFactory(conf);
		}
		return null;
	}

	/**
	 * Find the advisor for an action
	 * 
	 * @param actionId the action identifier
	 * @param serviceProvider the service provider the new advisor shall be
	 *            configured with
	 * @return the advisor or <code>null</code>
	 */
	@Override
	public IOAdvisor<?> findAdvisor(final String actionId, final ServiceProvider serviceProvider) {
		// find associated advisor(s)
		List<IOAdvisorFactory> advisors = getFactories(
				new FactoryFilter<IOAdvisor<?>, IOAdvisorFactory>() {

					@Override
					public boolean acceptFactory(IOAdvisorFactory factory) {
						return factory.getActionID().equals(actionId);
					}

					@Override
					public boolean acceptCollection(
							ExtensionObjectFactoryCollection<IOAdvisor<?>, IOAdvisorFactory> collection) {
						return true;
					}
				});

		// create advisor if possible
		IOAdvisor<?> advisor;
		if (advisors == null || advisors.isEmpty()) {
			throw new IllegalStateException(
					MessageFormat.format("No advisor for action {0} found", actionId));
		}
		else {
			if (advisors.size() > 1) {
				log.warn(MessageFormat.format("Multiple advisors for action {0} found", actionId));
			}

			try {
				advisor = advisors.get(0).createAdvisor(serviceProvider);
			} catch (Exception e) {
				log.error("Error creating advisor instance", e);
				advisor = null;
			}
		}
		return advisor;
	}

}
