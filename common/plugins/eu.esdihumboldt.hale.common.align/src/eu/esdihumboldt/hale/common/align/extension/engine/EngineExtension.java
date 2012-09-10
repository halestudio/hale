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

package eu.esdihumboldt.hale.common.align.extension.engine;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;

/**
 * Extension for {@link TransformationEngine}s
 * 
 * @author Simon Templer
 */
public class EngineExtension extends AbstractExtension<TransformationEngine, EngineFactory> {

	/**
	 * {@link TransformationEngine} factory based on an
	 * {@link IConfigurationElement}
	 */
	public static class ConfigurationFactory extends
			AbstractConfigurationFactory<TransformationEngine> implements EngineFactory {

		/**
		 * Create a {@link TransformationEngine} factory based on the given
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
		public void dispose(TransformationEngine instance) {
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
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");
		}

		/**
		 * @see EngineFactory#getDescription()
		 */
		@Override
		public String getDescription() {
			return conf.getAttribute("description");
		}

	}

	private static final String EXTENSION_ID = "eu.esdihumboldt.hale.align.engine";

	private static EngineExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the extension
	 */
	public static EngineExtension getInstance() {
		if (instance == null) {
			instance = new EngineExtension();
		}

		return instance;
	}

	/**
	 * Default constructor
	 */
	protected EngineExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected EngineFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("engine")) {
			return new ConfigurationFactory(conf);
		}

		return null;
	}

}
