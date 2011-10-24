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

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;

/**
 * Extension for {@link IOAdvisor}s
 * @author Simon Templer
 */
public class IOAdvisorExtension extends AbstractExtension<IOAdvisor<?>, IOAdvisorFactory> {

	/**
	 * Factory for {@link IOAdvisor} based on an {@link IConfigurationElement}
	 */
	private static class ConfigurationFactory extends
			AbstractConfigurationFactory<IOAdvisor<?>> implements IOAdvisorFactory {

		/**
		 * Create an {@link IOAdvisor} factory basd on the given configuration
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
			//XXX instead return action name?
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

	}
	
	private static final ALogger log = ALoggerFactory.getLogger(IOAdvisorExtension.class);
	
	private static IOAdvisorExtension instance;
	
	/**
	 * Get the I/O advisor extension instance
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
	protected IOAdvisorFactory createFactory(IConfigurationElement conf)
			throws Exception {
		if (conf.getName().equals("advisor")) {
			return new ConfigurationFactory(conf);
		}
		return null;
	}
	
	/**
	 * Find the advisor for an action
	 * @param actionId the action identifier
	 * @return the advisor or <code>null</code>
	 */
	public IOAdvisor<?> findAdvisor(final String actionId) {
		// find associated advisor(s)
		List<IOAdvisorFactory> advisors = getFactories(new FactoryFilter<IOAdvisor<?>, IOAdvisorFactory>() {
			
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
			throw new IllegalStateException(MessageFormat.format(
					"No advisor for action {0} found", actionId));
		}
		else {
			if (advisors.size() > 1) {
				log.warn(MessageFormat.format(
						"Multiple advisors for action {0} found", 
						actionId));
			}
			
			try {
				advisor = advisors.get(0).createExtensionObject();
			} catch (Exception e) {
				log.error("Error creating advisor instance", e);
				advisor = null;
			}
		}
		return advisor;
	}

}
