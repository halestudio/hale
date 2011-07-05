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

package eu.esdihumboldt.hale.ui.io.source.internal;

import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.AbstractObjectDefinition;
import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.core.internal.CoreBundle;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.ui.io.ImportSource;

/**
 * {@link ImportSource} extension
 * @author Simon Templer
 */
public class ImportSourceExtension extends AbstractExtension<ImportSource<?, ?>, ImportSourceFactory> {
	
	/**
	 * Factory for {@link ImportSource}s based on a {@link IConfigurationElement}
	 */
	private static class ConfigurationFactory extends AbstractConfigurationFactory<ImportSource<?, ?>>
			implements ImportSourceFactory {

		/**
		 * Create a factory based on the given configuration element
		 * @param conf the configuration
		 */
		protected ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(ImportSource<?, ?> source) {
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

		/**
		 * @see ImportSourceFactory#getProviderFactoryType()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends IOProviderFactory<?>> getProviderFactoryType() {
			String factoryType = conf.getAttribute("providerFactoryType");
			if (factoryType != null && !factoryType.isEmpty()) {
				String bundleName = conf.getContributor().getName();
				//TODO move method from InstanceBundle to OsgiUtils
				Class<? extends IOProviderFactory<?>> result = (Class<? extends IOProviderFactory<?>>) 
						CoreBundle.loadClass(factoryType, bundleName);
				if (result != null) {
					return result;
				}
			}
			
			return (Class<? extends IOProviderFactory<?>>) IOProviderFactory.class;
		}

		/**
		 * @see AbstractObjectDefinition#getPriority()
		 */
		@Override
		public int getPriority() {
			try {
			return Integer.parseInt(conf.getAttribute("priority"));
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		/**
		 * @see AbstractObjectFactory#getIconURL()
		 */
		@Override
		public URL getIconURL() {
			return getIconURL("icon");
		}

		/**
		 * @see ImportSourceFactory#getDescription()
		 */
		@Override
		public String getDescription() {
			return conf.getAttribute("description");
		}

		/**
		 * @see ImportSourceFactory#getContentType()
		 */
		@Override
		public ContentType getContentType() {
			String ct = conf.getAttribute("contentType");
			if (ct == null || ct.isEmpty()) {
				return null;
			}
			else {
				return ContentType.getContentType(ct);
			}
		}

	}

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.io.source";
	
	private static ImportSourceExtension instance;
	
	/**
	 * Get the extension instance
	 * @return the instance
	 */
	public static ImportSourceExtension getInstance() {
		if (instance == null) {
			instance = new ImportSourceExtension();
		}
		return instance;
	}
	
	/**
	 * Default constructor
	 */
	private ImportSourceExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected ImportSourceFactory createFactory(IConfigurationElement conf)
			throws Exception {
		if (conf.getName().equals("source")) {
			return new ConfigurationFactory(conf);
		}
		
		return null;
	}

}
