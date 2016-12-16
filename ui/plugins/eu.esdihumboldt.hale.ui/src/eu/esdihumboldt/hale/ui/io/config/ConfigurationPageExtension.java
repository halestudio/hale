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

package eu.esdihumboldt.hale.ui.io.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.AbstractObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Utilities for the configuration page extension point
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class ConfigurationPageExtension
		extends AbstractExtension<AbstractConfigurationPage<?, ?>, ConfigurationPageFactory> {

	/**
	 * Factory for {@link AbstractConfigurationPage}s based on an
	 * {@link IConfigurationElement}
	 */
	private static class ConfigurationFactory
			extends AbstractConfigurationFactory<AbstractConfigurationPage<?, ?>>
			implements ConfigurationPageFactory {

		/**
		 * Create a factory based on the given configuration
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
		public void dispose(AbstractConfigurationPage<?, ?> page) {
			// do nothing
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("class");
		}

		/**
		 * @see AbstractObjectDefinition#getPriority()
		 */
		@Override
		public int getPriority() {
			String orderString = conf.getAttribute("order");
			int order;
			if (orderString == null) {
				order = 0;
			}
			else {
				try {
					order = Integer.parseInt(orderString);
				} catch (Exception e) {
					order = 0;
				}
			}

			return Integer.valueOf(order);
		}

		/**
		 * @see ConfigurationPageFactory#getSupportedProviderIDs()
		 */
		@Override
		public Set<String> getSupportedProviderIDs() {
			IConfigurationElement[] children = conf.getChildren("provider");

			if (children != null) {
				Set<String> result = new HashSet<String>();

				for (IConfigurationElement child : children) {
					result.add(child.getAttribute("ref"));
				}

				return result;
			}
			else {
				return Collections.emptySet();
			}
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(ConfigurationPageExtension.class);

	/**
	 * Extension point ID
	 */
	public static final String EXTENSION_POINT_ID = "eu.esdihumboldt.hale.ui.io.config";

	private static ConfigurationPageExtension instance;

	/**
	 * Get the configuration page extension instance
	 * 
	 * @return the extension instance
	 */
	public static ConfigurationPageExtension getInstance() {
		if (instance == null) {
			instance = new ConfigurationPageExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private ConfigurationPageExtension() {
		super(EXTENSION_POINT_ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected ConfigurationPageFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("configPage")) {
			return new ConfigurationFactory(conf);
		}
		else {
			return null;
		}
	}

	/**
	 * Get the configuration pages registered for the given I/O provider
	 * descriptors
	 * 
	 * @param
	 * 			<P>
	 *            the {@link IOProvider} type used in the wizard
	 * 
	 * @param descriptors the provider descriptors
	 * @return the configuration pages in a multimap where the corresponding
	 *         provider identifier is mapped to the configuration page, one page
	 *         (the same instance) might be mapped for multiple identifiers
	 */
	@SuppressWarnings("unchecked")
	public <P extends IOProvider> ListMultimap<String, AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> getConfigurationPages(
			Iterable<IOProviderDescriptor> descriptors) {
		// collect provider IDs
		final Set<String> providerIds = new HashSet<String>();
		for (IOProviderDescriptor descriptor : descriptors) {
			providerIds.add(descriptor.getIdentifier());
		}

		// get all factories that support at least one of the providers
		List<ConfigurationPageFactory> factories = getFactories(
				new FactoryFilter<AbstractConfigurationPage<?, ?>, ConfigurationPageFactory>() {

					@Override
					public boolean acceptFactory(ConfigurationPageFactory factory) {
						Set<String> supported = new HashSet<String>(
								factory.getSupportedProviderIDs());
						supported.retainAll(providerIds);
						return !supported.isEmpty();
					}

					@Override
					public boolean acceptCollection(
							ExtensionObjectFactoryCollection<AbstractConfigurationPage<?, ?>, ConfigurationPageFactory> collection) {
						return false;
					}
				});

		ListMultimap<String, AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> result = ArrayListMultimap
				.create();

		// add pages to result map
		for (ConfigurationPageFactory factory : factories) {
			AbstractConfigurationPage<? extends P, ? extends IOWizard<P>> page = null;
			try {
				page = (AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>) factory
						.createExtensionObject();
			} catch (Exception e) {
				log.error("Error creating configuration page " + factory.getTypeName(), e);
				break;
			}

			if (page != null) {
				for (String providerId : factory.getSupportedProviderIDs()) {
					if (providerIds.contains(providerId)) {
						result.put(providerId, page);
					}
				}
			}
		}

		return result;
	}

}
