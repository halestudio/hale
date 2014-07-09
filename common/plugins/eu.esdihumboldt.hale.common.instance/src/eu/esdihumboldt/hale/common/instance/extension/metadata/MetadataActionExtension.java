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

package eu.esdihumboldt.hale.common.instance.extension.metadata;

import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;

/**
 * Extension for {@link MetadataAction}s
 * 
 * @author Sebastian Reinhardt
 */
public class MetadataActionExtension extends
		AbstractExtension<MetadataAction, MetadataActionFactory> {

	/**
	 * {@link MetadataAction} factory based on an {@link IConfigurationElement}
	 */
	public static class ConfigurationFactory extends AbstractConfigurationFactory<MetadataAction>
			implements MetadataActionFactory {

		/**
		 * Create a {@link MetadataAction} factory based on the given
		 * configuration element
		 * 
		 * @param conf the configuration element
		 */
		public ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(MetadataAction instance) {
			// do nothing
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		/**
		 * returns the key of the configuration element (meta data key)
		 * 
		 * @return the key
		 */
		@Override
		public String getKey() {
			return conf.getAttribute("key");
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");

		}

		/**
		 * 
		 */
		@Override
		public URL getIconURL() {
			return getIconURL("icon");
		}

	}

	// private static final ALogger log =
	// ALoggerFactory.getLogger(MetadataInfoExtension.class);

	private static MetadataActionExtension instance;

	/**
	 * Extension point ID
	 */
	private static final String EXTENSION_ID = "eu.esdihumboldt.hale.instance.metadata";

	/**
	 * default constructor
	 */
	public MetadataActionExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * Get the extension instance
	 * 
	 * @return the extension
	 */
	public static MetadataActionExtension getInstance() {
		if (instance == null) {
			instance = new MetadataActionExtension();
		}

		return instance;
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected MetadataActionFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("action")) {
			return new ConfigurationFactory(conf);
		}
		return null;
	}

	/**
	 * Creates a list of all {@link MetadataActionFactory}s for a certain key
	 * defined at the extension point
	 * 
	 * @param key the meta data key
	 * @return the list of action factorys
	 */
	public List<MetadataActionFactory> getMetadataActions(final String key) {
		List<MetadataActionFactory> factorys = (getFactories(new FactoryFilter<MetadataAction, MetadataActionFactory>() {

			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<MetadataAction, MetadataActionFactory> collection) {
				return true;
			}

			@Override
			public boolean acceptFactory(MetadataActionFactory factory) {
				return factory.getKey().equals(key);
			}
		}));
		return factorys;

	}

}
