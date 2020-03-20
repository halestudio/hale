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

package eu.esdihumboldt.hale.common.headless.transform;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.PseudoInstanceReference;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Sink that holds instances in a limbo, to be collected through the offered
 * instance collection.
 * 
 * @author Kai Schwierczek
 */
public class LimboInstanceSink extends AbstractTransformationSink {

	private static final Instance END = new DefaultInstance(null, null);

	private final TargetInstanceCollection collection = new TargetInstanceCollection();
	private TargetResourceIterator iterator = null;
	private boolean cancelled;

	// XXX use a maximum number of entries?
	private final BlockingDeque<Instance> queue = new LinkedBlockingDeque<Instance>(50);

	@Override
	protected synchronized void internalAddInstance(Instance instance) {
		// ignore incoming instances if we are cancelled
		if (cancelled)
			return;
		try {
			queue.put(instance);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	@Override
	protected void internalDone(boolean cancel) {
		try {
			if (cancel) {
				cancelled = true;
				// ensure that the iterator doesn't block anymore
				if (queue.remainingCapacity() > 10)
					queue.putFirst(END);
				// ensure that addInstance doesn't block anymore
				queue.clear();
			}
			// in either case add another end indicator
			queue.put(END);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	@Override
	public InstanceCollection getInstanceCollection() {
		return collection;
	}

	@Override
	public void setTypes(TypeIndex types) {
		// ignore - not needed
	}

	@Override
	public void dispose() {
		queue.clear();

		super.dispose();
	}

	private class TargetResourceIterator implements ResourceIterator<Instance> {

		private Instance next = null;
		private boolean endRead = false;

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			synchronized (this) {
				// first check whether a previous hasNext call got something
				if (next != null)
					return true;

				// then check whether the execution was cancelled
				// or we are finished because we read the END instance
				if (cancelled || endRead)
					return false;

				// hasNext has to block until it knows whether the
				// transformation is done, or more instances are coming
				Instance result;
				try {
					result = queue.take();
				} catch (InterruptedException e) {
					// shouldn't happen
					return false;
				}
				if (result == END) {
					endRead = true;
					return false;
				}
				else {
					next = result;
					return true;
				}
			}
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Instance next() {
			synchronized (this) {
				if (hasNext()) {
					// take the element from hasNext and set it back to null
					Instance tmp = next;
					next = null;
					return tmp;
				}
				else
					throw new NoSuchElementException();
			}
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.ResourceIterator#close()
		 */
		@Override
		public void close() {
			done(true);
		}
	}

	/**
	 * Simple internal instance collection, does not support remove and filter
	 * and uses {@link PseudoInstanceReference}s and
	 * {@link TargetResourceIterator}. Only one iterator may be created.
	 * 
	 * @author Kai Schwierczek
	 */
	private class TargetInstanceCollection implements InstanceCollection {

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.InstanceResolver#getReference(eu.esdihumboldt.hale.common.instance.model.Instance)
		 */
		@Override
		public InstanceReference getReference(Instance instance) {
			return new PseudoInstanceReference(instance);
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.InstanceResolver#getInstance(eu.esdihumboldt.hale.common.instance.model.InstanceReference)
		 */
		@Override
		public Instance getInstance(InstanceReference reference) {
			return ((PseudoInstanceReference) reference).getInstance();
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.InstanceCollection#iterator()
		 */
		@Override
		public ResourceIterator<Instance> iterator() {
			if (iterator == null) {
				iterator = new TargetResourceIterator();
				return iterator;
			}
			else
				throw new IllegalStateException();
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.InstanceCollection#hasSize()
		 */
		@Override
		public boolean hasSize() {
			return false;
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.InstanceCollection#size()
		 */
		@Override
		public int size() {
			return UNKNOWN_SIZE;
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.InstanceCollection#isEmpty()
		 */
		@Override
		public boolean isEmpty() {
			// XXX have to return false, even if it actually may be empty
			return false;
		}

		@Override
		public InstanceCollection select(Filter filter) {
			return FilteredInstanceCollection.applyFilter(this, filter);
		}

	}
}
