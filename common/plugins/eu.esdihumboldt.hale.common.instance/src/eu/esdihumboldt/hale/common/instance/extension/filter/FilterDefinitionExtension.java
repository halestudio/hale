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

package eu.esdihumboldt.hale.common.instance.extension.filter;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;

/**
 * Extension for {@link FilterDefinition}s.
 * 
 * @author Simon Templer
 */
public class FilterDefinitionExtension extends
		AbstractExtension<FilterDefinition<?>, FilterDefinitionFactory> {

	/**
	 * Factory for {@link FilterDefinition} based on an
	 * {@link IConfigurationElement}.
	 */
	private static class ConfigurationFactory extends
			AbstractConfigurationFactory<FilterDefinition<?>> implements FilterDefinitionFactory {

		/**
		 * Create a {@link FilterDefinition} factory based on the given
		 * configuration element.
		 * 
		 * @param conf the configuration element
		 */
		public ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(FilterDefinition<?> instance) {
			// do nothing
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

	}

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.instance.filter";

	/**
	 * Default constructor.
	 */
	public FilterDefinitionExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected FilterDefinitionFactory createFactory(IConfigurationElement conf) throws Exception {
		return new ConfigurationFactory(conf);
	}

}
