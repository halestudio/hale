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

import java.util.NoSuchElementException;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * Instance collection that wraps an instance collection and represents a
 * selection that contains the instances matching a given {@link Filter}.
 * 
 * @author Simon Templer
 */
public class FilteredInstanceCollection extends InstanceCollectionDecorator {

	/**
	 * Filtered resource iterator.
	 */
	public class FilteredIterator implements ResourceIterator<Instance> {

		private final ResourceIterator<Instance> decoratee;

		/**
		 * The next matching instance
		 */
		private Instance preview;

		/**
		 * States if the value in {@link #preview} represents a valid element
		 */
		private boolean previewPresent;

		/**
		 * States if {@link #preview}/{@link #previewPresent} must be updated
		 */
		private boolean updatePreview = true;

		/**
		 * Create a filtered resource iterator.
		 * 
		 * @param decoratee the original iterator
		 */
		public FilteredIterator(ResourceIterator<Instance> decoratee) {
			this.decoratee = decoratee;
		}

		@Override
		public boolean hasNext() {
			update(); // ensure previewPresent/preview are set

			return previewPresent;
		}

		@Override
		public Instance next() {
			update(); // ensure previewPresent/preview are set

			if (!previewPresent) {
				throw new NoSuchElementException();
			}

			updatePreview = true; // next time, update the preview

			return preview;
		}

		/**
		 * Move {@link #preview} to the next match if possible, update
		 * {@link #previewPresent}.
		 */
		private void update() {
			if (updatePreview) {
				previewPresent = false;

				// find first instance matching the filter
				while (!previewPresent && decoratee.hasNext()) {
					Instance instance = decoratee.next();
					if (filter.match(instance)) {
						previewPresent = true;
						preview = instance;
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
					"Removing instances not supported oin filtered collections");
		}

		@Override
		public void close() {
			decoratee.close();
		}

	}

	private final Filter filter;

	/**
	 * Create a filtered instance collection.
	 * 
	 * @param decoratee the instance collection to perform the selection on
	 * @param filter the filter representing the selection
	 */
	public FilteredInstanceCollection(InstanceCollection decoratee, Filter filter) {
		super(decoratee);
		this.filter = filter;
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return new FilteredIterator(decoratee.iterator());
	}

	@Override
	public boolean hasSize() {
		// the size cannot be pre-determined
		return false;
	}

	@Override
	public int size() {
		return UNKNOWN_SIZE;
	}

	@Override
	public boolean isEmpty() {
		ResourceIterator<Instance> it = iterator();
		try {
			return !it.hasNext();
		} finally {
			it.close();
		}
	}

	@Override
	public InstanceCollection select(Filter filter) {
		return new FilteredInstanceCollection(this, filter);
	}

}
