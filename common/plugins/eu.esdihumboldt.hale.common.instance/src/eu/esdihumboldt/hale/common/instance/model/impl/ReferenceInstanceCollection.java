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

package eu.esdihumboldt.hale.common.instance.model.impl;

import java.util.Collection;
import java.util.Iterator;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.InstanceResolver;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * Instance collection based on references.
 * 
 * @author Simon Templer
 */
public class ReferenceInstanceCollection implements InstanceCollection {

	/**
	 * Instance iterator based on instance references.
	 * 
	 * @param <X> the instance reference type
	 */
	private class ReferenceIterator<X extends InstanceReference>
			extends GenericResourceIteratorAdapter<X, Instance> {

		/**
		 * Constructor.
		 * 
		 * @param iterator iterator on instances references
		 */
		public ReferenceIterator(Iterator<X> iterator) {
			super(iterator);
		}

		@Override
		protected Instance convert(X next) {
			return instanceResolver.getInstance(next);
		}

	}

	private final Collection<? extends InstanceReference> references;
	private final InstanceResolver instanceResolver;

	/**
	 * Create an instance collection based on the given references.
	 * 
	 * @param references the instance references
	 * @param instanceResolver the instance resolver
	 */
	public ReferenceInstanceCollection(Collection<? extends InstanceReference> references,
			InstanceResolver instanceResolver) {
		this.references = references;
		this.instanceResolver = instanceResolver;
	}

	@Override
	public InstanceReference getReference(Instance instance) {
		// TODO instead have references associated to instances and retrieve
		// those?
		return new PseudoInstanceReference(instance);
	}

	@Override
	public Instance getInstance(InstanceReference reference) {
		if (reference instanceof PseudoInstanceReference) {
			return ((PseudoInstanceReference) reference).getInstance();
		}

		return instanceResolver.getInstance(reference);
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return new ReferenceIterator<>(references.iterator());
	}

	@Override
	public boolean hasSize() {
		return true;
	}

	@Override
	public int size() {
		return references.size();
	}

	@Override
	public boolean isEmpty() {
		return references.isEmpty();
	}

	@Override
	public InstanceCollection select(Filter filter) {
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

}
