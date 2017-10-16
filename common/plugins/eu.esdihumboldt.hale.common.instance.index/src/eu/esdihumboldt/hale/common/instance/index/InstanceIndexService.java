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

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
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
	void add(Instance instance, ResolvableInstanceReference reference); // AlignmentUtil.getAllDefaultEntity

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
	 * Add properties whose values will be indexed
	 * 
	 * @param properties Properties to index
	 */
	void addPropertyMappings(Collection<PropertyEntityDefinition> properties);

	/**
	 * Removes properties from the index
	 * 
	 * @param properties Properties to remove
	 */
	void removePropertyMappings(Collection<PropertyEntityDefinition> properties);

	/**
	 * Retrieve instance references from the index grouped by the given
	 * properties.
	 * 
	 * @param typeName Type that contains the properties
	 * @param properties Properties to group by
	 * @return Grouped instance references
	 */
	Collection<Collection<ResolvableInstanceReference>> groupBy(QName typeName,
			List<QName> properties);

	/**
	 * Retrieves instances with the given property values
	 * 
	 * @param typeName Type of the instances to search
	 * @param query Collection of property values
	 * @return Collection of matching instances
	 */
	InstanceCollection find(QName typeName, Collection<IndexedPropertyValue> query);

	/**
	 * Clear the index
	 */
	void clear();
}
