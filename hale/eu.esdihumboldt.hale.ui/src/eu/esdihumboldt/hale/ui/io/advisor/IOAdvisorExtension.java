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

package eu.esdihumboldt.hale.ui.io.advisor;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.expressions.ElementHandler;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.AbstractObjectDefinition;
import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.core.internal.CoreBundle;
import eu.esdihumboldt.hale.core.io.IOAdvisor;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * {@link IOAdvisor} extension
 * @author Simon Templer
 */
public class IOAdvisorExtension extends AbstractExtension<IOAdvisor<?>, IOAdvisorFactory> {
	
	private static final ALogger log = ALoggerFactory.getLogger(IOAdvisorExtension.class);

	/**
	 * Factory for {@link IOAdvisor}s based on a {@link IConfigurationElement}
	 */
	private static class ConfigurationFactory extends AbstractConfigurationFactory<IOAdvisor<?>>
			implements IOAdvisorFactory {

		/**
		 * Create a factory based on the given configuration element
		 * @param conf the configuration
		 */
		protected ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see IOAdvisorFactory#getEnabledWhen()
		 */
		@Override
		public Expression getEnabledWhen() {
			IConfigurationElement[] children = conf.getChildren("enabledWhen");
			if (children != null && children.length > 0) {
				// get child of enabled when
				children = children[0].getChildren();
				
				if (children != null && children.length > 0) {
					try {
						return ElementHandler.getDefault().create(ExpressionConverter.getDefault(),
								children[0]);
					} catch (CoreException e) {
						log.error("Could not evaluate expression for action enablement.", e);
					}
				}
			}
			
			return null;
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
			return conf.getAttribute("label");
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		/**
		 * @see IOAdvisorFactory#getProviderType()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends IOProvider> getProviderType() {
			String bundleName = conf.getContributor().getName();
			//TODO move method from InstanceBundle to OsgiUtils
			return (Class<? extends IOProvider>) CoreBundle.loadClass(conf.getAttribute("providerType"), bundleName);
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
		 * @see IOAdvisorFactory#createWizard()
		 */
		@Override
		public IOWizard<?, ?> createWizard() {
			try {
				return (IOWizard<?, ?>) conf.createExecutableExtension("wizard");
			} catch (CoreException e) {
				throw new IllegalStateException(e);
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
		 * @see IOAdvisorFactory#getDependencies()
		 */
		@Override
		public Set<String> getDependencies() {
			IConfigurationElement[] children = conf.getChildren("dependsOn");
			
			if (children != null) {
				Set<String> result = new HashSet<String>();
				
				for (IConfigurationElement child : children) {
					result.add(child.getAttribute("advisor"));
				}
				
				return result;
			}
			else {
				return Collections.emptySet();
			}
		}

		/**
		 * @see IOAdvisorFactory#isProjectResource()
		 */
		@Override
		public boolean isProjectResource() {
			return Boolean.parseBoolean(conf.getAttribute("projectResource"));
		}

	}

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.io.advisor";
	
	private static IOAdvisorExtension instance;
	
	/**
	 * Get the extension instance
	 * @return the instance
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
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected IOAdvisorFactory createFactory(IConfigurationElement conf)
			throws Exception {
		if (conf.getName().equals("advisor")) {
			return new ConfigurationFactory(conf);
		}
		
		return null;
	}
	
	/**
	 * XXX should be implemented in {@link AbstractExtension}
	 * Get the factory for the given identifier
	 * @param identifier the identifier
	 * @return the factory with the given identifier or <code>null</code> if it
	 *   doesn't exist
	 */
	public IOAdvisorFactory getFactory(String identifier) {
		for (IOAdvisorFactory factory : getFactories()) {
			if (identifier.equals(factory.getIdentifier())) {
				return factory;
			}
		}
		
		return null;
	}

}
