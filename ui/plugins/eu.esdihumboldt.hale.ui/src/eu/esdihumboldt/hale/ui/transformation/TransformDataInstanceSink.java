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

package eu.esdihumboldt.hale.ui.transformation;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.PseudoInstanceReference;

/**
 * Instance sink for the transform data wizard.
 *
 * @author Kai Schwierczek
 */
public class TransformDataInstanceSink implements InstanceSink {
	private static final Instance END = new DefaultInstance(null, null);

	private TargetInstanceCollection collection = new TargetInstanceCollection();
	private TargetResourceIterator iterator = null;

	// XXX use a maximum number of entries? If so we should use a ArrayBlockingQueue
	private final BlockingQueue<Instance> queue = new LinkedBlockingQueue<Instance>();

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink#addInstance(eu.esdihumboldt.hale.common.instance.model.Instance)
	 */
	@Override
	public void addInstance(Instance instance) {
		// ignore incoming instances if the iterator is closed already
		if (iterator != null && iterator.closed)
			return;
		try {
			queue.put(instance);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/**
	 * Called if the transformation is done. Subsequent calls to {@link #addInstance(Instance)}
	 * result in undetermined behavior.
	 */
	public void done() {
		try {
			queue.put(END);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/**
	 * Returns the associated instance collection, whose iterator will receive
	 * the instances that are added to the instance sink.
	 *
	 * @return the instance collection for this sink
	 */
	public InstanceCollection getInstanceCollection() {
		return collection;
	}

	private class TargetResourceIterator implements ResourceIterator<Instance> {
		private Instance next = null;
		private boolean closed = false;

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			synchronized (this) {
				if (closed)
					return false;

				// hasNext has to block until it knows whether the transformation
				// is done, or more instances are coming
				Instance result;
				try {
					result = queue.take();
				} catch (InterruptedException e) {
					// shouldn't happen
					return false;
				}
				if (result == END)
					return false;
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
					Instance tmp = next;
					next = null;
					return tmp;
				} else
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
			closed = true;
			done(); // add end element in case hasNext is still blocking
		}
	}

	/**
	 * Simple internal instance collection, does not support remove and filter and uses
	 * {@link PseudoInstanceReference}s and {@link TargetResourceIterator}.
	 * Only one iterator may be created.
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
			} else
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

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.InstanceCollection#select(eu.esdihumboldt.hale.common.instance.model.Filter)
		 */
		@Override
		public InstanceCollection select(Filter filter) {
			throw new UnsupportedOperationException();
		}
		
	}
}
