/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.instance.tools.impl;

import java.util.NoSuchElementException;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;

/**
 * Iterator dividing an instance collection.
 */
public class PartitionIterator implements ResourceIterator<InstanceCollection> {

	private class PartIterator implements ResourceIterator<Instance> {

		private final int cap;

		/**
		 * Create an iterator for a part.
		 * 
		 * @param cap the index of the instance up to which (exclusive) the
		 *            iterator will serve instances
		 */
		public PartIterator(int cap) {
			this.cap = cap;
		}

		@Override
		public boolean hasNext() {
			return (currentPos < (cap - 1)) && mainIterator.hasNext();
		}

		@Override
		public Instance next() {
			if (currentPos < (cap - 1)) {
				currentPos++;
				return mainIterator.next();
			}
			throw new NoSuchElementException();
		}

		@Override
		public void close() {
			// move forward to cap
			while (hasNext()) {
				if (mainIterator instanceof InstanceIterator) {
					currentPos++;
					((InstanceIterator) mainIterator).skip();
				}
				else {
					next();
				}
			}
			current = null;
		}

	}

	private class PartCollection implements InstanceCollection {

		private final int collIndex;

		private final boolean empty = !mainIterator.hasNext();

		private PartIterator iterator;

		/**
		 * Create a new part collection.
		 * 
		 * @param numCollection number of the collection (zero-based)
		 */
		public PartCollection(int numCollection) {
			super();
			this.collIndex = numCollection;
		}

		@Override
		public InstanceReference getReference(Instance instance) {
			return instances.getReference(instance);
		}

		@Override
		public Instance getInstance(InstanceReference reference) {
			return instances.getInstance(reference);
		}

		@Override
		public ResourceIterator<Instance> iterator() {
			if (iterator == null) {
				int cap = (collIndex + 1) * maxObjects;
				iterator = new PartIterator(cap);
				return iterator;
			}
			throw new IllegalStateException("Iterator can only be retrieved once");
		}

		@Override
		public boolean hasSize() {
			return instances.hasSize();
		}

		@Override
		public int size() {
			if (!instances.hasSize()) {
				return UNKNOWN_SIZE;
			}
			int cap = (collIndex + 1) * maxObjects;
			if (instances.size() > cap) {
				return maxObjects;
			}
			else {
				return instances.size() % cap;
			}
		}

		@Override
		public boolean isEmpty() {
			return empty;
		}

		@Override
		public InstanceCollection select(Filter filter) {
			return FilteredInstanceCollection.applyFilter(this, filter);
		}

		public void close() {
			if (iterator == null) {
				iterator().close();
			}
			else {
				iterator.close();
			}
		}

	}

	private final int maxObjects;
	private final InstanceCollection instances;

	private ResourceIterator<Instance> mainIterator;
	private int currentPos = -1;
	private int numCollection = -1;
	private PartCollection current;

	/**
	 * Create a new iterator that creates partial instance collection from a
	 * given instance collection.
	 * 
	 * The parts must be consumed in succession to assure a consistent behavior.
	 * 
	 * @param instances the instances to partition
	 * @param maxObjects the maximum number of objects per part
	 */
	public PartitionIterator(InstanceCollection instances, int maxObjects) {
		this.maxObjects = maxObjects;
		this.instances = instances;
	}

	@Override
	public boolean hasNext() {
		if (mainIterator == null) {
			mainIterator = instances.iterator();
		}

		// there are still instances to be retrieved
		return mainIterator.hasNext();
	}

	@Override
	public InstanceCollection next() {
		if (mainIterator == null) {
			mainIterator = instances.iterator();
		}

		if (current != null) {
			current.close();
		}
		current = null;

		// is another collection possible?
		if (mainIterator.hasNext()) {
			current = new PartCollection(++numCollection);
		}
		else {
			// empty collection in case hasNext was called
			// before the end of the last collection
			return new DefaultInstanceCollection();
		}

		return current;
	}

	@Override
	public void close() {
		if (mainIterator != null) {
			mainIterator.close();
		}
	}

}