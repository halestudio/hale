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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * An instance collection which consists of multiple instance collections. For
 * instance references it uses the underlying instance collections mechanism
 * which may be inefficient. The iterator supports
 * {@link ResourceIterator#remove()} if the underlying InstanceCollection's
 * ResourceIterator does so.
 * 
 * @author Kai Schwierczek
 */
public class MultiInstanceCollection implements InstanceCollection {

	private final List<InstanceCollection> collections;

	/**
	 * Constructor using a list of instance collections..
	 * 
	 * @param collections the list of instance collections
	 */
	public MultiInstanceCollection(List<InstanceCollection> collections) {
		this.collections = new ArrayList<InstanceCollection>(collections);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.InstanceResolver#getReference(eu.esdihumboldt.hale.common.instance.model.Instance)
	 */
	@Override
	public InstanceReference getReference(Instance instance) {
		MultiInstanceCollectionInstance inst = (MultiInstanceCollectionInstance) instance;
		return new MultiInstanceCollectionReference(collections.get(inst.listIndex).getReference(
				inst.getOriginalInstance()), inst.listIndex);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.InstanceResolver#getInstance(eu.esdihumboldt.hale.common.instance.model.InstanceReference)
	 */
	@Override
	public Instance getInstance(InstanceReference reference) {
		MultiInstanceCollectionReference ref = (MultiInstanceCollectionReference) reference;
		return new MultiInstanceCollectionInstance(collections.get(ref.listIndex).getInstance(
				ref.reference), ref.listIndex);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.InstanceCollection#iterator()
	 */
	@Override
	public ResourceIterator<Instance> iterator() {
		return new MultiInstanceCollectionResourceIterator();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.InstanceCollection#hasSize()
	 */
	@Override
	public boolean hasSize() {
		for (InstanceCollection collection : collections)
			if (!collection.hasSize())
				return false;
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.InstanceCollection#size()
	 */
	@Override
	public int size() {
		int result = 0;
		for (InstanceCollection collection : collections) {
			int size = collection.size();
			if (size == UNKNOWN_SIZE)
				return UNKNOWN_SIZE;
			else
				result += size;
		}
		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.InstanceCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		for (InstanceCollection collection : collections)
			if (!collection.isEmpty())
				return false;
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.InstanceCollection#select(eu.esdihumboldt.hale.common.instance.model.Filter)
	 */
	@Override
	public InstanceCollection select(Filter filter) {
		return new FilteredInstanceCollection(this, filter);
	}

	/**
	 * Internal resource iterator iterating over all given instance collections
	 * in order. Supports {@link #remove()} if the underlying iterator supports
	 * remove.
	 * 
	 * @author Kai Schwierczek
	 */
	private class MultiInstanceCollectionResourceIterator implements ResourceIterator<Instance> {

		private int currentCollection = -1;
		private ResourceIterator<Instance> currentIterator = null;
		private int hasNextIndex = -1; // index of the next collection that has
										// elements
		private boolean closed = false;

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			if (closed)
				return false;
			// do not advance here, because after hasNext(), before next(),
			// a call to remove may still work
			if (currentIterator != null && currentIterator.hasNext()) {
				hasNextIndex = currentCollection;
				return true;
			}
			for (int i = currentCollection + 1; i < collections.size(); i++)
				if (!collections.get(i).isEmpty()) {
					hasNextIndex = i;
					return true;
				}
			hasNextIndex = -1;
			return false;
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Instance next() {
			// check for hasNext first, because after a unsuccessful call to
			// next(),
			// remove() may still work
			if (hasNext()) {
				// advance iterator if necessary
				if (currentCollection != hasNextIndex) {
					if (currentIterator != null)
						currentIterator.close();
					currentCollection = hasNextIndex;
					currentIterator = collections.get(currentCollection).iterator();
				}

				// return next of current iterator
				return new MultiInstanceCollectionInstance(currentIterator.next(),
						currentCollection);
			}
			else
				throw new NoSuchElementException();
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			if (currentIterator != null)
				currentIterator.remove();
			else
				throw new IllegalStateException();
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.ResourceIterator#close()
		 */
		@Override
		public void close() {
			closed = true;
			if (currentIterator != null) {
				currentIterator.close();
				currentIterator = null;
			}
		}
	}

	/**
	 * Internal class for decorating the instances with the index of the
	 * instance collection list.
	 * 
	 * @author Kai Schwierczek
	 */
	private static class MultiInstanceCollectionInstance extends InstanceDecorator {

		private final int listIndex;

		/**
		 * Default constructor.
		 * 
		 * @param instance the instance to decorate
		 * @param listIndex the index of the list this instance originated from
		 */
		public MultiInstanceCollectionInstance(Instance instance, int listIndex) {
			super(instance);
			this.listIndex = listIndex;
		}
	}

	/**
	 * Internal class for decorating the references with the index of the
	 * instance collection list.
	 * 
	 * @author Kai Schwierczek
	 */
	private static class MultiInstanceCollectionReference implements InstanceReference {

		private final InstanceReference reference;
		private final int listIndex;

		/**
		 * Default constructor.
		 * 
		 * @param reference the reference to decorate
		 * @param listIndex the index of the list this reference originated from
		 */
		public MultiInstanceCollectionReference(InstanceReference reference, int listIndex) {
			this.reference = reference;
			this.listIndex = listIndex;
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.InstanceReference#getDataSet()
		 */
		@Override
		public DataSet getDataSet() {
			return reference.getDataSet();
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + listIndex;
			result = prime * result + reference.hashCode();
			return result;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MultiInstanceCollectionReference) {
				MultiInstanceCollectionReference other = (MultiInstanceCollectionReference) obj;
				return listIndex == other.listIndex && reference.equals(other.reference);
			}
			else
				return false;
		}
	}
}
