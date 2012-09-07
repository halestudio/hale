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

package eu.esdihumboldt.hale.common.instance.extension.metadata;

import java.util.ArrayList;
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



/**
 * Extension for {@link MetadataAction}s
 * @author Sebastian Reinhardt
 */
public class MetadataActionExtension extends AbstractExtension<MetadataAction, MetadataActionFactory> {

	
	/**
	 * {@link MetadataAction} factory based on an {@link IConfigurationElement} 
	 */
	public static class ConfigurationFactory extends
			AbstractConfigurationFactory<MetadataAction> implements
			MetadataActionFactory {

		
		/**
		 * Create a {@link MetadataAction} factory based on the given 
		 * configuration element
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
		 * @return the key
		 */
		@Override
		public String getKey() {
			return conf.getAttribute("key");
		}

		/**
		 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");
			
		}
		
		

		
	}
	
	
	private static final ALogger log = ALoggerFactory.getLogger(MetadataInfoExtension.class);
	
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
	protected MetadataActionFactory createFactory(IConfigurationElement conf)
			throws Exception {
		if (conf.getName().equals("action")) {
			return new ConfigurationFactory(conf);
		}
		return null;
	}

	/**
	 * Creates a list of all {@link MetadataAction}s for a certain key defined at the extension point
	 * @param key the meta data key
	 * @return the list of actions
	 */
	public List<MetadataAction> getMetadataAction(final String key){
		List<MetadataActionFactory> factorys = (getFactories(new FactoryFilter<MetadataAction, MetadataActionFactory>(){
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
		
		List<MetadataAction> metaActions = new ArrayList<MetadataAction>();
		
		if (factorys == null || factorys.isEmpty()) {
			return null;
		}
		
		else{
			for(MetadataActionFactory fac : factorys){
				try {
					metaActions.add(fac.createExtensionObject());
				} catch (Exception e) {
					log.error("Error creating metadata action", e);
				}
			}
			return metaActions;
		}
		
	}

}
