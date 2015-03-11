/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.instance.graph.reference.internal;

import java.util.Iterator;
import java.util.List;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;

/**
 * An instance collection based on a collection of instance references.
 * 
 * @author Simon Templer
 */
public class ReferencesInstanceCollection implements InstanceCollection {

	private final List<InstanceReference> references;
	private final InstanceCollection originalCollection;

	/**
	 * Create a new instance collection based on the given instance references
	 * 
	 * @param references the references
	 * @param originalCollection the instance collection to be used to resolve
	 *            instance references
	 */
	public ReferencesInstanceCollection(List<InstanceReference> references,
			InstanceCollection originalCollection) {
		this.references = references;
		this.originalCollection = originalCollection;
	}

	@Override
	public InstanceReference getReference(Instance instance) {
		return originalCollection.getReference(instance);
	}

	@Override
	public Instance getInstance(InstanceReference reference) {
		return originalCollection.getInstance(reference);
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return new ResourceIterator<Instance>() {

			private final Iterator<InstanceReference> it = references.iterator();

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public Instance next() {
				InstanceReference ref = it.next();
				if (ref != null) {
					return getInstance(ref);
				}
				return null;
			}

			@Override
			public void remove() {
				it.remove();
			}

			@Override
			public void close() {
				// nothing to do
			}
		};
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
