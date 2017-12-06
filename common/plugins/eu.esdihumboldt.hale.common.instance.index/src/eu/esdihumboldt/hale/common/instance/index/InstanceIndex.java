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

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;

/**
 * Interface for {@link Instance} indexes
 * 
 * @author Florian Esser
 * @param <V> Value type
 * @param <M> Mapping type
 */
public interface InstanceIndex<V, M extends IndexMapping<Instance, V>>
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
	 * @return References to all indexed instances
	 */
	Collection<ResolvableInstanceReference> getReferences();

	/**
	 * Retrieve all indexed values for the given instance reference
	 * 
	 * @param reference Instance reference
	 * @return Collection of values
	 */
	Collection<V> getValues(ResolvableInstanceReference reference);

	/**
	 * Find all indexed instances with the given property value
	 * 
	 * @param value Property value to search
	 * @return References to all matching instances
	 */
	Collection<ResolvableInstanceReference> find(V value);

	/**
	 * Clear indexes and mappings
	 */
	void clearAll();

	/**
	 * Clear indexes but retain mappings
	 */
	void clearIndexes();
}
