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

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;

/**
 * Interface for {@link Instance} indexes
 * 
 * @param <V> Value type
 * @param <M> Mapping type
 * @param <R> Instance reference type
 * 
 * @author Florian Esser
 */
public interface InstanceIndex<V, M extends IndexMapping<Instance, V>, R extends InstanceReference>
		extends Index<ResolvableInstanceReference, Instance, InstanceIndexQuery> {

	/**
	 * Add the given mapping
	 * 
	 * @param mapping Mapping to add
	 */
	void addMapping(M mapping);

	/**
	 * Remove the given mapping
	 * 
	 * @param mapping Mapping to remove
	 */
	void removeMapping(M mapping);

	/**
	 * @return an unmodifiable collection of mappings
	 */
	Collection<M> getMappings();

	/**
	 * @return References to all indexed instances
	 */
	Collection<R> getReferences();

	/**
	 * Retrieve all indexed values for the given instance reference
	 * 
	 * @param reference Instance reference
	 * @return Collection of values
	 */
	Collection<V> getInstancePropertyValues(R reference);

	/**
	 * Retrieve all indexed values for the instance identified by the given ID
	 * 
	 * @param instanceId ID of the instance
	 * @return Collection of values
	 */
	Collection<V> getInstancePropertyValuesById(Object instanceId);

	/**
	 * Retrieve all indexed instances with the given values
	 * 
	 * @param values Values
	 * @return Collection of instances
	 */
	Collection<R> getInstancesByValue(V values);

	/**
	 * Retrieve all indexed instances with the given property values
	 * 
	 * @param propertyPath Property path
	 * @param values Values
	 * @return Collection of instances
	 */
	Collection<R> getInstancesByValue(List<QName> propertyPath, List<?> values);

	/**
	 * Find all indexed instances with the given property value
	 * 
	 * @param value Property value to search
	 * @return References to all matching instances
	 */
	Collection<R> find(V value);

	/**
	 * Clear indexes and mappings
	 */
	void clearAll();

	/**
	 * Clear indexes but retain mappings
	 */
	void clearIndexes();
}
