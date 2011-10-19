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

package eu.esdihumboldt.hale.common.core.io.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import eu.esdihumboldt.hale.common.core.io.IOProvider;

/**
 * Extension for {@link IOProvider}s
 * @author Simon Templer
 */
public class IOProviderExtension extends AbstractExtension<IOProvider, IOProviderDescriptor> {

	/**
	 * {@link IOProvider} factory based on a {@link IConfigurationElement}
	 */
	private static class ConfigurationFactory extends
			AbstractConfigurationFactory<IOProvider> implements
			IOProviderDescriptor {

		/**
		 * Create the {@link IOProvider} factory
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

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

	}

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.provider";
	
	/**
	 * Default constructor
	 */
	public IOProviderExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected IOProviderDescriptor createFactory(IConfigurationElement conf)
			throws Exception {
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
