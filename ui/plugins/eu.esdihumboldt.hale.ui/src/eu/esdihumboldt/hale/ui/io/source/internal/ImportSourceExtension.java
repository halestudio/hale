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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.AbstractObjectDefinition;
import de.cs3d.util.eclipse.extension.AbstractObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionUtil;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.ui.io.ImportSource;

/**
 * {@link ImportSource} extension
 * 
 * @author Simon Templer
 */
public class ImportSourceExtension extends AbstractExtension<ImportSource<?>, ImportSourceFactory> {

	/**
	 * Factory for {@link ImportSource}s based on a
	 * {@link IConfigurationElement}
	 */
	private static class ConfigurationFactory extends AbstractConfigurationFactory<ImportSource<?>>
			implements ImportSourceFactory {

		/**
		 * Create a factory based on the given configuration element
		 * 
		 * @param conf the configuration
		 */
		protected ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(ImportSource<?> source) {
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
		 * @see ImportSourceFactory#getProviderType()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends ImportProvider> getProviderType() {
			String val = conf.getAttribute("providerType");
			if (val == null || val.isEmpty()) {
				// default value
				return ImportProvider.class;
			}
			return (Class<? extends ImportProvider>) ExtensionUtil.loadClass(conf, "providerType");
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
		public IContentType getContentType() {
			String ct = conf.getAttribute("contentType");
			if (ct == null || ct.isEmpty()) {
				return null;
			}
			else {
				return Platform.getContentTypeManager().getContentType(ct);
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
	 * 
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
	protected ImportSourceFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("source")) {
			return new ConfigurationFactory(conf);
		}

		return null;
	}

}
