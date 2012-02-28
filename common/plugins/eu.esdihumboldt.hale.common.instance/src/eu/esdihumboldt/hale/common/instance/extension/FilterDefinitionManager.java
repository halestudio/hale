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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.util.definition.AbstractObjectFactory;

/**
 * {@link FilterDefinition} manager.
 * @author Simon Templer
 */
public class FilterDefinitionManager extends
		AbstractObjectFactory<Filter, FilterDefinition> {
	
	private static volatile FilterDefinitionManager instance;
	
	/**
	 * Get the {@link FilterDefinitionManager} instance.
	 * @return the singleton instance
	 */
	public static final FilterDefinitionManager getInstance() {
		if (instance == null) {
			instance = new FilterDefinitionManager();
		}
		return instance;
	}
	
	private final FilterDefinitionExtension extension = new FilterDefinitionExtension();

	/**
	 * @see AbstractObjectFactory#getDefinitions()
	 */
	@Override
	protected Iterable<FilterDefinition> getDefinitions() {
		return Collections2.transform(extension.getFactories(), new Function<FilterDefinitionFactory, FilterDefinition>() {

			@Override
			public FilterDefinition apply(FilterDefinitionFactory input) {
				try {
					return input.createExtensionObject();
				} catch (Exception e) {
					throw new IllegalStateException("Could not create filter definition", e);
				}
			}
		});
	}

}
