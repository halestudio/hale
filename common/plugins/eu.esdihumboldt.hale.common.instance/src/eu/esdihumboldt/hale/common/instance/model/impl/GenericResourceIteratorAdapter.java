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

import java.util.Iterator;

import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * {@link ResourceIterator} adapter for a normal iterator that can perform a
 * conversion from the iterator elements to a target element type.
 * @param <S> the source object type served by the wrapped iterator
 * @param <T> the object type served by the resource iterator
 * @author Simon Templer
 */
public abstract class GenericResourceIteratorAdapter<S, T> implements ResourceIterator<T> {
	
	private Iterator<S> iterator;

	/**
	 * Create a {@link ResourceIterator} adapter for the given iterator.
	 * @param iterator the iterator to adapt 
	 */
	public GenericResourceIteratorAdapter(Iterator<S> iterator) {
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
		return convert(iterator.next());
	}

	/**
	 * Convert an object before it is returned by {@link #next()}.
	 * @param next the object to convert
	 * @return the converted object
	 */
	protected abstract T convert(S next);

	/**
	 * @see Iterator#remove()
	 */
	@Override
	public void remove() {
		iterator.remove();
	}

	/**
	 * @see ResourceIterator#close()
	 */
	@Override
	public void close() {
		if (iterator instanceof ResourceIterator<?>) {
			((ResourceIterator<?>) iterator).close();
		}
	}

}
