/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.model.impl;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * Instance collection that uses a poll mechanism to create instances on the
 * fly.
 * 
 * @param <C> the type of the context held for an iterator
 * @author Simon Templer
 */
public abstract class AbstractPollingInstanceCollection<C> implements InstanceCollection {

	/**
	 * Polling iterator.
	 */
	private class PollingIterator implements ResourceIterator<Instance> {

		private final C context;

		/**
		 * Default constructor.
		 */
		public PollingIterator() {
			super();
			context = createContext();
		}

		@Override
		public boolean hasNext() {
			return AbstractPollingInstanceCollection.this.hasNext(context);
		}

		@Override
		public Instance next() {
			return AbstractPollingInstanceCollection.this.next(context);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void close() {
			dispose(context);
		}

	}

	@Override
	public InstanceReference getReference(Instance instance) {
		return new PseudoInstanceReference(instance);
	}

	/**
	 * Create a new context for a new iterator.
	 * 
	 * @return the iterator context
	 */
	protected abstract C createContext();

	/**
	 * Determines if for an iterator there is an additional instance.
	 * 
	 * @param context the iterator context
	 * @return if there is an additional instance to retrieve using
	 *         {@link #next(Object)}
	 */
	protected abstract boolean hasNext(C context);

	/**
	 * Get the next instance for the iterator.
	 * 
	 * @param context the iterator context
	 * @return the next instance
	 */
	protected abstract Instance next(C context);

	/**
	 * Dispose the context after the iterator has been closed.
	 * 
	 * @param context the iterator context
	 */
	protected abstract void dispose(C context);

	@Override
	public Instance getInstance(InstanceReference reference) {
		if (reference instanceof PseudoInstanceReference) {
			return ((PseudoInstanceReference) reference).getInstance();
		}

		return null;
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return new PollingIterator();
	}

	@Override
	public boolean hasSize() {
		return size() != UNKNOWN_SIZE;
	}

	@Override
	public int size() {
		return UNKNOWN_SIZE;
	}

	@Override
	public InstanceCollection select(Filter filter) {
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

}
