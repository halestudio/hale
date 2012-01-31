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

package eu.esdihumboldt.hale.common.instance.model.impl;

import java.util.Collection;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * Default instance collection implementation backed by a collection.
 * @author Simon Templer
 */
public class DefaultInstanceCollection implements InstanceCollection {

	private final Collection<Instance> collection;
	
	/**
	 * Create an instance collection backed 
	 * @param collection the instance collection
	 */
	public DefaultInstanceCollection(Collection<Instance> collection) {
		super();
		this.collection = collection;
	}
	
	/**
	 * Adds an instance to the collection
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
		return new FilteredInstanceCollection(this, filter);
	}

}
