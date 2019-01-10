/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.model.ext.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.TypeFilter;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceCollection2;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.SingleTypeInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Multi instance collection that consists of one instance collection per type.
 * 
 * @author Simon Templer
 */
public class PerTypeInstanceCollection extends MultiInstanceCollection
		implements InstanceCollection2 {

	private final Map<TypeDefinition, InstanceCollection> collections;

	/**
	 * Create an instance collection consisting of the given instance
	 * collections.
	 * 
	 * @param collections the instance collections mapped to the type associated
	 *            to the instances they contain
	 */
	public PerTypeInstanceCollection(Map<TypeDefinition, InstanceCollection> collections) {
		super(new ArrayList<>(collections.values()));
		this.collections = ImmutableMap.copyOf(collections);
	}

	@Override
	public boolean supportsFanout() {
		return true;
	}

	@Override
	public Map<TypeDefinition, InstanceCollection> fanout() {
		return collections;
	}

	/**
	 * @return Iterator over {@link SingleTypeInstanceCollection}s
	 */
	public Iterator<InstanceCollection> collectionsIterator() {
		return collections.entrySet().stream()
				.map(e -> (InstanceCollection) new SingleTypeInstanceCollection(e.getValue(),
						e.getKey()))
				.collect(Collectors.toList()).iterator();
	}

	@Override
	public InstanceCollection select(Filter filter) {
		// apply filter to this collection, as it supports fan-out an probably
		// the filter can be optimized
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	/**
	 * Create a PerTypeInstanceCollection from an {@link InstanceCollection} by
	 * selecting from it all instances of the given types.
	 * 
	 * @param instances Instance collection to use as input
	 * @param types Types to select from the input collection
	 * @return The created PerTypeInstanceCollection
	 */
	public static PerTypeInstanceCollection fromInstances(InstanceCollection instances,
			Collection<TypeDefinition> types) {

		Map<TypeDefinition, InstanceCollection> partitioned = new HashMap<>();
		for (TypeDefinition type : types) {
			InstanceCollection typed = instances.select(new TypeFilter(type));
			if (!typed.isEmpty()) {
				partitioned.put(type, typed);
			}
		}

		return new PerTypeInstanceCollection(partitioned);
	}
}
