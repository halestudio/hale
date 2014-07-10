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

package eu.esdihumboldt.hale.common.align.extension.engine;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
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
