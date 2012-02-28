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

package eu.esdihumboldt.hale.common.instance.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;

/**
 * Extension for {@link FilterDefinition}s.
 * @author Simon Templer
 */
public class FilterDefinitionExtension extends AbstractExtension<FilterDefinition, FilterDefinitionFactory> {

	/**
	 * Factory for {@link FilterDefinition} based on an {@link IConfigurationElement}.
	 */
	private static class ConfigurationFactory extends
			AbstractConfigurationFactory<FilterDefinition> implements
			FilterDefinitionFactory {

		/**
		 * Create a {@link FilterDefinition} factory based on the given
		 * configuration element.
		 * @param conf the configuration element
		 */
		public ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(FilterDefinition instance) {
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
	protected FilterDefinitionFactory createFactory(IConfigurationElement conf)
			throws Exception {
		return new ConfigurationFactory(conf);
	}
	
}
