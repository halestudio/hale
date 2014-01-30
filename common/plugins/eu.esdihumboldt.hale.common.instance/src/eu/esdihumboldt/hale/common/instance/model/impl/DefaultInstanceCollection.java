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

package eu.esdihumboldt.hale.common.instance.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.InstanceResolver;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * Default instance collection implementation backed by a collection.
 * 
 * @author Simon Templer
 */
public class DefaultInstanceCollection implements InstanceCollection {

	private final List<Instance> collection;

	/**
	 * Create an instance collection backed by an array list
	 * 
	 * @param collection the instance collection holding the initial contained
	 *            instances
	 */
	public DefaultInstanceCollection(Collection<? extends Instance> collection) {
		super();
		this.collection = new ArrayList<Instance>(collection);
	}

	/**
	 * Create an empty instance collection.
	 */
	public DefaultInstanceCollection() {
		super();
		this.collection = new ArrayList<Instance>();
	}

	/**
	 * Adds an instance to the collection
	 * 
	 * @param instance the instance to add
	 */
	public void add(Instance instance) {
		collection.add(instance);
	}

	/**
	 * @see InstanceCollection#iterator()
	 */
	@Override
	public ResourceIterator<Instance> iterator() {
		return new ResourceIteratorAdapter<Instance>(collection.iterator());
	}

	/**
	 * @see InstanceCollection#hasSize()
	 */
	@Override
	public boolean hasSize() {
		return true;
	}

	/**
	 * @see InstanceCollection#size()
	 */
	@Override
	public int size() {
		return collection.size();
	}

	/**
	 * @see InstanceCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return collection.isEmpty();
	}

	/**
	 * @see InstanceCollection#select(Filter)
	 */
	@Override
	public InstanceCollection select(Filter filter) {
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	/**
	 * @see InstanceResolver#getReference(Instance)
	 */
	@Override
	public InstanceReference getReference(Instance instance) {
		return new PseudoInstanceReference(instance);
	}

	/**
	 * @see InstanceResolver#getInstance(InstanceReference)
	 */
	@Override
	public Instance getInstance(InstanceReference reference) {
		if (reference instanceof PseudoInstanceReference) {
			return ((PseudoInstanceReference) reference).getInstance();
		}

		return null;
	}

}
