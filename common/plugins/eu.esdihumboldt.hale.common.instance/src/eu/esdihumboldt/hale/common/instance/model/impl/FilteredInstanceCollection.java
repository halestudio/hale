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

import java.util.NoSuchElementException;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * Instance collection that wraps an instance collection and represents a
 * selection that contains the instances matching a given {@link Filter}.
 * @author Simon Templer
 */
public class FilteredInstanceCollection implements InstanceCollection {

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
		 * Move {@link #preview} to the next match if possible, 
		 * update {@link #previewPresent}.
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

	private final InstanceCollection decoratee;
	
	private final Filter filter;

	/**
	 * Create a filtered instance collection.
	 * @param decoratee the instance collection to perform the selection on
	 * @param filter the filter representing the selection
	 */
	public FilteredInstanceCollection(InstanceCollection decoratee,
			Filter filter) {
		super();
		this.decoratee = decoratee;
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

	@Override
	public InstanceReference getReference(Instance instance) {
		return decoratee.getReference(instance);
	}

	@Override
	public Instance getInstance(InstanceReference reference) {
		return decoratee.getInstance(reference);
	}

}
