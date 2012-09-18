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

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.util.definition.AbstractObjectFactory;

/**
 * {@link FilterDefinition} manager.
 * 
 * @author Simon Templer
 */
public class FilterDefinitionManager extends AbstractObjectFactory<Filter, FilterDefinition<?>> {

	private static volatile FilterDefinitionManager instance;

	private FilterDefinitionManager() {
		super();
	}

	/**
	 * Get the {@link FilterDefinitionManager} instance.
	 * 
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
	protected List<FilterDefinition<?>> getDefinitions() {
		return Lists.transform(extension.getFactories(),
				new Function<FilterDefinitionFactory, FilterDefinition<?>>() {

					@Override
					public FilterDefinition<?> apply(FilterDefinitionFactory input) {
						try {
							return input.createExtensionObject();
						} catch (Exception e) {
							throw new IllegalStateException("Could not create filter definition", e);
						}
					}
				});
	}

}
