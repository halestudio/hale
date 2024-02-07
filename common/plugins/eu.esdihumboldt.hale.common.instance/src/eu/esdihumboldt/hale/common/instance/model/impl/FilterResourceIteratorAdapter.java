/*
 * Copyright (c) 2024 wetransform GmbH
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

package eu.esdihumboldt.hale.common.instance.model.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * {@link ResourceIterator} adapter for a normal iterator that can perform a
 * conversion from the iterator elements to a target element type. It filters
 * out items that are converted to a <code>null</code> value.
 * 
 * @param <S> the source object type served by the wrapped iterator
 * @param <T> the object type served by the resource iterator
 * @author Simon Templer
 */
public abstract class FilterResourceIteratorAdapter<S, T> implements ResourceIterator<T> {

	/**
	 * The next matching instance
	 */
	private T preview;

	/**
	 * States if the value in {@link #preview} represents a valid element
	 */
	private boolean previewPresent;

	/**
	 * States if {@link #preview}/{@link #previewPresent} must be updated
	 */
	private boolean updatePreview = true;

	private final Iterator<S> iterator;

	/**
	 * Create a {@link ResourceIterator} adapter for the given iterator.
	 * 
	 * @param iterator the iterator to adapt
	 */
	public FilterResourceIteratorAdapter(Iterator<S> iterator) {
		super();
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext() {
		update(); // ensure previewPresent/preview are set

		return previewPresent;
	}

	@Override
	public T next() {
		update(); // ensure previewPresent/preview are set

		if (!previewPresent) {
			throw new NoSuchElementException();
		}

		updatePreview = true; // next time, update the preview

		return preview;
	}

	/**
	 * Move {@link #preview} to the next non-null converted item if possible,
	 * update {@link #previewPresent}.
	 */
	private void update() {
		if (updatePreview) {
			previewPresent = false;

			// find first instance matching the filter
			while (!previewPresent && iterator.hasNext()) {
				S item = iterator.next();
				T converted = convert(item);

				if (converted != null) {
					previewPresent = true;
					preview = converted;
				}
			}

			if (!previewPresent) {
				preview = null;
			}

			updatePreview = false;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"Removing instances not supported on filtered collections");
	}

	/**
	 * Convert an object before it is returned by {@link #next()}.
	 * 
	 * @param next the object to convert
	 * @return the converted object or null if it should be skipped
	 */
	protected abstract T convert(S next);

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
