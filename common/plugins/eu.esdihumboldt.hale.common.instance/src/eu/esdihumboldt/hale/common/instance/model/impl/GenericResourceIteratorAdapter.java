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

import java.util.Iterator;

import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * {@link ResourceIterator} adapter for a normal iterator that can perform a
 * conversion from the iterator elements to a target element type.
 * 
 * @param <S> the source object type served by the wrapped iterator
 * @param <T> the object type served by the resource iterator
 * @author Simon Templer
 */
public abstract class GenericResourceIteratorAdapter<S, T> implements ResourceIterator<T> {

	private Iterator<S> iterator;

	/**
	 * Create a {@link ResourceIterator} adapter for the given iterator.
	 * 
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
	 * 
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
