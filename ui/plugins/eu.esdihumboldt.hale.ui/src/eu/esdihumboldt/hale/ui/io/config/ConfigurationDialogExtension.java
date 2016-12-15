/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.io.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;

/**
 * TODO Type description
 * 
 * @author Flo
 */
public class ConfigurationDialogExtension
		extends AbstractExtension<AbstractConfigurationDialog, ConfigurationDialogFactory> {

	private static final ALogger log = ALoggerFactory.getLogger(ConfigurationDialogExtension.class);

	/**
	 * Extension point ID
	 */
	public static final String EXTENSION_POINT_ID = "eu.esdihumboldt.hale.ui.io.config";

	private static ConfigurationDialogExtension instance;

	/**
	 * Default constructor
	 */
	public ConfigurationDialogExtension() {
		super(EXTENSION_POINT_ID);
	}

	/**
	 * Get the configuration page extension instance
	 * 
	 * @return the extension instance
	 */
	public static ConfigurationDialogExtension getInstance() {
		if (instance == null) {
			instance = new ConfigurationDialogExtension();
		}
		return instance;
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected ConfigurationDialogFactory createFactory(IConfigurationElement conf)
			throws Exception {
		if (conf.getName().equals("configDialog")) {
			return new ConfigurationDialogFactoryImpl(conf);
		}
		else {
			return null;
		}
	}

	/**
	 * Get the configuration dialog registered for the given I/O provider
	 * descriptors
	 * 
	 * @param
	 * 			<P>
	 *            the {@link IOProvider} type used in the wizard
	 * 
	 * @param descriptors the provider descriptors
	 * @return the configuration dialog where the corresponding provider
	 *         identifier is mapped to the configuration dialog, one page (the
	 *         same instance) might be mapped for multiple identifiers
	 */
	@SuppressWarnings("unchecked")
	public ConfigurationDialogFactory getConfigurationDialog(
			final IOProviderDescriptor descriptor) {

		List<ConfigurationDialogFactory> factories = getFactories(
				new FactoryFilter<AbstractConfigurationDialog, ConfigurationDialogFactory>() {

					@Override
					public boolean acceptFactory(ConfigurationDialogFactory factory) {
						return factory.getSupportedProviderIDs()
								.contains(descriptor.getIdentifier());
					}

					@Override
					public boolean acceptCollection(
							ExtensionObjectFactoryCollection<AbstractConfigurationDialog, ConfigurationDialogFactory> collection) {
						return false;
					}
				});

		if (factories.isEmpty()) {
			return null;
		}
		else if (factories.size() > 1) {
			log.warn("Multiple configuration dialogs for provider {}. Using first one.",
					descriptor.getIdentifier());
		}

		return factories.get(0);
	}

	/**
	 * Factory for {@link AbstractConfigurationPage}s based on an
	 * {@link IConfigurationElement}
	 */
	private static class ConfigurationDialogFactoryImpl
			extends AbstractConfigurationFactory<AbstractConfigurationDialog>
			implements ConfigurationDialogFactory {

		/**
		 * Create a factory based on the given configuration
		 * 
		 * @param conf the configuration element
		 */
		protected ConfigurationDialogFactoryImpl(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory#dispose(java.lang.Object)
		 */
		@Override
		public void dispose(AbstractConfigurationDialog instance) {
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("class");
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.io.config.ConfigurationDialogFactory#getSupportedProviderIDs()
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
}
