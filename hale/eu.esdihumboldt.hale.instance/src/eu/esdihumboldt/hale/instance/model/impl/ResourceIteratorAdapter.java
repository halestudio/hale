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

package eu.esdihumboldt.hale.instance.model.impl;

import java.util.Iterator;

import eu.esdihumboldt.hale.instance.model.ResourceIterator;

/**
 * {@link ResourceIterator} adapter for a normal iterator
 * @param <T> the object type
 * @author Simon Templer
 */
public class ResourceIteratorAdapter<T> implements ResourceIterator<T> {
	
	private Iterator<T> iterator;

	/**
	 * Create a {@link ResourceIterator} adapter for the given iterator.
	 * @param iterator the iterator to adapt 
	 */
	public ResourceIteratorAdapter(Iterator<T> iterator) {
		super();
		this.iterator = iterator;
	}

	/**
	 * @see Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/**
	 * @see Iterator#next()
	 */
	@Override
	public T next() {
		return iterator.next();
	}

	/**
	 * @see Iterator#remove()
	 */
	@Override
	public void remove() {
		iterator.remove();
	}

	/**
	 * @see ResourceIterator#dispose()
	 */
	@Override
	public void dispose() {
		if (iterator instanceof ResourceIterator<?>) {
			((ResourceIterator<?>) iterator).dispose();
		}
	}

}
