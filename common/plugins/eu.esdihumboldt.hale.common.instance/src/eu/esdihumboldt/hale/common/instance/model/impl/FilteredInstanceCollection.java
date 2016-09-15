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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import eu.esdihumboldt.hale.common.instance.model.ContextAwareFilter;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.TypeFilter;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceCollection2;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.EmptyInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.InstanceCollectionDecorator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Instance collection that wraps an instance collection and represents a
 * selection that contains the instances matching a given {@link Filter}.
 * 
 * @author Simon Templer
 */
public class FilteredInstanceCollection extends InstanceCollectionDecorator {

	/**
	 * Create an instance collection that applies a filter to the given instance
	 * collection.
	 * 
	 * @param instances the instance collection to filter
	 * @param filter the filter
	 * @return the filtered instance collection
	 */
	public static InstanceCollection applyFilter(InstanceCollection instances, Filter filter) {
		if (filter instanceof TypeFilter && instances instanceof InstanceCollection2) {
			/*
			 * For type filters check if we can make use of fan-out.
			 */
			InstanceCollection2 instances2 = (InstanceCollection2) instances;

			if (instances2.supportsFanout()) {
				TypeDefinition type = ((TypeFilter) filter).getType();
				InstanceCollection result = instances2.fanout().get(type);
				if (result == null) {
					result = EmptyInstanceCollection.INSTANCE;
				}
				return result;
			}
		}

		// create a filtered collection
		return new FilteredInstanceCollection(instances, filter);
	}

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
		 * Iteration context for filters.
		 */
		private final Map<Object, Object> context;

		/**
		 * Create a filtered resource iterator.
		 * 
		 * @param decoratee the original iterator
		 */
		public FilteredIterator(ResourceIterator<Instance> decoratee) {
			this.decoratee = decoratee;

			if (filter instanceof ContextAwareFilter) {
				context = Collections.synchronizedMap(new HashMap<>());
			}
			else {
				context = null;
			}
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
					boolean match;
					if (context != null) {
						match = ((ContextAwareFilter) filter).match(instance, context);
					}
					else {
						match = filter.match(instance);
					}
					if (match) {
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
					"Removing instances not supported on filtered collections");
		}

		@Override
		public void close() {
			decoratee.close();

			// in case the iterator is kept around, clear the context
			if (context != null) {
				context.clear();
			}
		}

	}

	private final Filter filter;

	/**
	 * Create a filtered instance collection.
	 * 
	 * @param decoratee the instance collection to perform the selection on
	 * @param filter the filter representing the selection
	 */
	private FilteredInstanceCollection(InstanceCollection decoratee, Filter filter) {
		super(decoratee);
		this.filter = filter;
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		ResourceIterator<Instance> it = decoratee.iterator();
		if (filter instanceof TypeFilter && it instanceof InstanceIterator
				&& ((InstanceIterator) it).supportsTypePeek()) {
			// make use of type peek if possible
			return new TypeFilteredIterator(it, ((TypeFilter) filter).getType());
		}
		return new FilteredIterator(it);
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

	@Override
	public Map<TypeDefinition, InstanceCollection> fanout() {
		Map<TypeDefinition, InstanceCollection> fanout = super.fanout();
		if (fanout != null) {
			return Maps.transformValues(fanout,
					new Function<InstanceCollection, InstanceCollection>() {

						@Override
						public InstanceCollection apply(InstanceCollection from) {
							return new FilteredInstanceCollection(from, filter);
						}
					});
		}

		return null;
	}

}
