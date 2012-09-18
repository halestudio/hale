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
 * {@link ResourceIterator} adapter for a normal iterator
 * 
 * @param <T> the object type
 * @author Simon Templer
 */
public class ResourceIteratorAdapter<T> extends GenericResourceIteratorAdapter<T, T> {

	/**
	 * Create a {@link ResourceIterator} adapter for the given iterator.
	 * 
	 * @param iterator the iterator to adapt
	 */
	public ResourceIteratorAdapter(Iterator<T> iterator) {
		super(iterator);
	}

	/**
	 * @see GenericResourceIteratorAdapter#convert(Object)
	 */
	@Override
	protected T convert(T next) {
		return next;
	}

}
