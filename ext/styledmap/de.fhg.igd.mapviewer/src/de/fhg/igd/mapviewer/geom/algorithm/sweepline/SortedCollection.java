/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.geom.algorithm.sweepline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A list that is always sorted.
 * 
 * @author Michel Kraemer
 * @param <E> the type of the list elements
 */
public class SortedCollection<E> implements Collection<E> {

	/**
	 * The actual list
	 */
	private List<E> _list = new ArrayList<E>();

	/**
	 * The comparator used to sort the items in the collection
	 */
	private Comparator<? super E> _comp;

	/**
	 * Default constructor
	 */
	public SortedCollection() {
		// nothing to do here
	}

	/**
	 * Copy constructor
	 * 
	 * @param c the collection to copy
	 */
	public SortedCollection(Collection<? extends E> c) {
		addAll(c);
	}

	/**
	 * Default constructor
	 * 
	 * @param comp the comparator used to sort the elements in this list
	 */
	public SortedCollection(Comparator<? super E> comp) {
		_comp = comp;
	}

	/**
	 * Copy constructor
	 * 
	 * @param c the collection to copy
	 * @param comp the comparator used to sort the elements in this list
	 */
	public SortedCollection(Collection<? extends E> c, Comparator<? super E> comp) {
		this(comp);
		addAll(c);
	}

	/**
	 * @see Collection#add(Object)
	 */
	@Override
	public boolean add(E e) {
		int index = Collections.binarySearch(_list, e, _comp);
		if (index < 0) {
			index = -(index + 1);
		}
		_list.add(index, e);
		return true;
	}

	/**
	 * @see Collection#addAll(Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E e : c) {
			if (!add(e)) {
				throw new IllegalStateException();
			}
		}
		return !c.isEmpty();
	}

	/**
	 * @see Collection#contains(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		int index = Collections.binarySearch(_list, (E) o, _comp);
		return (index >= 0);
	}

	/**
	 * @see Collection#containsAll(Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the element at the given position
	 * 
	 * @param index the position
	 * @return the element
	 * @throws IndexOutOfBoundsException if index is out of bounds
	 */
	public E get(int index) {
		return _list.get(index);
	}

	/**
	 * Searches for the index of a given element
	 * 
	 * @param e the element
	 * @return the index or -1 if the list does not contain this element
	 */
	public int indexOf(E e) {
		int index = Collections.binarySearch(_list, e, _comp);
		if (index < 0) {
			return -1;
		}
		return index;
	}

	/**
	 * @see Collection#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return Collections.unmodifiableList(_list).iterator();
	}

	/**
	 * @see Collection#remove(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		int index = Collections.binarySearch(_list, (E) o, _comp);
		if (index < 0) {
			return false;
		}
		_list.remove(index);
		return true;
	}

	/**
	 * Removes the element at the given position from the list
	 * 
	 * @param index the position
	 * @return the removed element
	 * @throws IndexOutOfBoundsException if index is out of bounds
	 */
	public E remove(int index) {
		return _list.remove(index);
	}

	/**
	 * @see Collection#removeAll(Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		for (Object o : c) {
			result |= remove(o);
		}
		return result;
	}

	/**
	 * @see Collection#retainAll(Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return _list.retainAll(c);
	}

	/**
	 * @see Collection#size()
	 */
	@Override
	public int size() {
		return _list.size();
	}

	/**
	 * @see Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return _list.isEmpty();
	}

	/**
	 * @see Collection#clear()
	 */
	@Override
	public void clear() {
		_list.clear();
	}

	/**
	 * @see Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return _list.toArray();
	}

	/**
	 * @see Collection#toArray(Object[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return _list.toArray(a);
	}

	/**
	 * Resorts this list (for example, if the comparator changed its internal
	 * state)
	 */
	public void resort() {
		Collections.sort(_list, _comp);
	}
}
