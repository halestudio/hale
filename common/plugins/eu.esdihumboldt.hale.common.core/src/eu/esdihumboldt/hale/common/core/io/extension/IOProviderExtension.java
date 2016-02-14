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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.content.IContentType;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.parameter.IOProviderParameter;

/**
 * Extension for {@link IOProvider}s
 * 
 * @author Simon Templer
 */
public class IOProviderExtension extends AbstractExtension<IOProvider, IOProviderDescriptor> {

	/**
	 * {@link IOProvider} factory based on a {@link IConfigurationElement}
	 */
	private static class ConfigurationFactory extends AbstractConfigurationFactory<IOProvider>
			implements IOProviderDescriptor {

		/**
		 * Create the {@link IOProvider} factory
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
		public void dispose(IOProvider provider) {
			// do nothing
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");
		}

		@Override
		public String getDescription() {
			return conf.getAttribute("description");
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		/**
		 * @see IOProviderDescriptor#getSupportedTypes()
		 */
		@Override
		public Set<IContentType> getSupportedTypes() {
			IConfigurationElement[] children = conf.getChildren("contentType");

			if (children != null) {
				Set<IContentType> result = new HashSet<IContentType>();

				for (IConfigurationElement child : children) {
					String id = child.getAttribute("ref");
					IContentType ct = HalePlatform.getContentTypeManager().getContentType(id);
					if (ct != null) {
						result.add(ct);
					}
					else {
						log.error(MessageFormat.format(
								"Content type with ID {0} not known by the platform", id));
					}
				}

				return result;
			}
			else {
				return Collections.emptySet();
			}
		}

		/**
		 * @see IOProviderDescriptor#getProviderType()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends IOProvider> getProviderType() {
			return (Class<? extends IOProvider>) ExtensionUtil.loadClass(conf, "class");
		}

		@Override
		public Set<IOProviderParameter> getProviderParameter() {
			IConfigurationElement[] children = conf.getChildren("providerParameter");

			if (children != null) {
				Set<IOProviderParameter> result = new LinkedHashSet<IOProviderParameter>();
				for (IConfigurationElement child : children) {
					result.add(new IOProviderParameter(child));
				}
				return result;
			}
			else
				return Collections.emptySet();
		}

	}

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.provider";

	private static final ALogger log = ALoggerFactory.getLogger(IOProviderExtension.class);

	private static IOProviderExtension instance;

	/**
	 * Get the I/O provider extension instance
	 * 
	 * @return the extension instance
	 */
	public static IOProviderExtension getInstance() {
		if (instance == null) {
			instance = new IOProviderExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private IOProviderExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected IOProviderDescriptor createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("provider")) {
			return new ConfigurationFactory(conf);
		}
		return null;
	}

	/**
	 * @see AbstractExtension#createCollection(IConfigurationElement)
	 */
	@Override
	protected ExtensionObjectFactoryCollection<IOProvider, IOProviderDescriptor> createCollection(
			IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("factory")) {
			return (IOProviderFactory) conf.createExecutableExtension("class");
		}
		return super.createCollection(conf);
	}

}
