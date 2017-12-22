/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.instance.index;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;

/**
 * Service for maintaining an instance index.
 * 
 * @author Florian Esser
 */
public interface InstanceIndexService {

	/**
	 * Add an {@link Instance} to the index
	 * 
	 * @param instance Instance to add
	 * @param reference Resolvable reference to the instance
	 */
	void add(Instance instance, ResolvableInstanceReference reference);

	/**
	 * Add an instance to the index
	 * 
	 * @param instance Instance to add
	 * @param instances Source collection containing the instance
	 */
	void add(Instance instance, InstanceCollection instances);

	/**
	 * Add an instance to the index
	 * 
	 * @param reference Reference to the instance
	 * @param instances Source collection containing the instance
	 */
	void add(InstanceReference reference, InstanceCollection instances);

	/**
	 * Add properties whose values will be indexed in a combined key (may be
	 * single property)
	 * 
	 * @param properties Properties to index
	 */
	void addPropertyMapping(List<PropertyEntityDefinition> properties);

	/**
	 * Add all property mappings for the given collection of cells
	 * 
	 * @param cells Cells to add property mappings for
	 * @param serviceProvider the service provider
	 * @return true if the property mappings were changed
	 */
	boolean addPropertyMappings(Iterable<? extends Cell> cells, ServiceProvider serviceProvider);

	/**
	 * Removes properties from the index
	 * 
	 * @param properties Properties to remove
	 */
	void removePropertyMapping(List<PropertyEntityDefinition> properties);

	/**
	 * Retrieve instance references from the index grouped by the given
	 * properties.
	 * 
	 * @param typeName Type that contains the properties
	 * @param properties Properties to group by
	 * @return Grouped instance references
	 */
	Collection<Collection<ResolvableInstanceReference>> groupBy(QName typeName,
			List<List<QName>> properties);

	/**
	 * Retrieves instances with the given property values
	 * 
	 * @param typeName Type of the instances to search
	 * @param query Collection of property values
	 * @return Collection of matching instances
	 */
	InstanceCollection find(QName typeName, Collection<List<IndexedPropertyValue>> query);

	/**
	 * Retrieves instances of the given type
	 * 
	 * @param typeName Type of the instances to search
	 * @return Collection of matching instances
	 */
	Collection<ResolvableInstanceReference> find(QName typeName);

	/**
	 * Clear indexes and mappings
	 */
	void clearAll();

	/**
	 * Clear indexes but retain mappings
	 */
	void clearIndexedValues();

	/**
	 * Find instances of the given type that have the specified property values
	 * 
	 * @param typeName Type of instances to search
	 * @param propertyPath Property to search
	 * @param values Values that the instance must have in the property
	 *            specified by <code>propertyPath</code>
	 * @return References to matching instances
	 */
	Collection<ResolvableInstanceReference> getInstancesByValue(QName typeName,
			List<QName> propertyPath, List<?> values);

	/**
	 * Find property values by the specified instance
	 * 
	 * @param typeName Type of the instance
	 * @param propertyPath Property whose values should be returned
	 * @param instanceId The ID of the instance to return property values of
	 * @return The instance's values in the given property
	 */
	List<IndexedPropertyValue> getInstancePropertyValues(QName typeName, List<QName> propertyPath,
			Object instanceId);
}
