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

package eu.esdihumboldt.hale.ui.service.instance.sample.internal.sampler.skip;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.FullInstanceIteratorSupport;
import eu.esdihumboldt.hale.common.instance.model.impl.InstanceCollectionDecorator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import gnu.trove.TObjectIntHashMap;

/**
 * Simple implementation of an instance collection that returns every n-th
 * instance of each encountered type.
 * 
 * @author Simon Templer
 */
public class SkipSampleInstances extends InstanceCollectionDecorator {

	/**
	 * Iterator that returns at maximum a specific number of instances per type.
	 */
	public class SkipSampleIterator extends FullInstanceIteratorSupport {

		private final TObjectIntHashMap<TypeDefinition> typeSkip = new TObjectIntHashMap<>();

		/**
		 * Constructor.
		 * 
		 * @param decoratee the decoratee
		 */
		public SkipSampleIterator(ResourceIterator<Instance> decoratee) {
			super(decoratee);
		}

		@Override
		public boolean hasNext() {
			proceedToNextValid();

			return super.hasNext();
		}

		private void proceedToNextValid() {
			// skip all invalid instances where the limit has already been
			// reached
			while (super.hasNext() && !acceptType(super.typePeek())) {
				typeSkip.adjustValue(super.typePeek(), 1); // count skip
				super.skip();
			}
		}

		private boolean acceptType(TypeDefinition type) {
			// initialize
			typeSkip.putIfAbsent(type, skip);

			// accept once skip number is reached
			return typeSkip.get(type) >= skip;
		}

		@Override
		public Instance next() {
			proceedToNextValid();

			Instance result = super.next();
			typeSkip.put(result.getDefinition(), 0); // rest skip counter
			return result;
		}

		@Override
		public TypeDefinition typePeek() {
			proceedToNextValid();

			return super.typePeek();
		}

		@Override
		public void skip() {
			proceedToNextValid();
			typeSkip.put(super.typePeek(), 0); // rest skip counter
			super.skip();
		}

		@Override
		public boolean supportsTypePeek() {
			return true;
		}

	}

	private final int skip;

	/**
	 * Constructor.
	 * 
	 * @param instances the original instance collection
	 * @param skip the number of instances per type to skip
	 */
	public SkipSampleInstances(InstanceCollection instances, int skip) {
		super(instances);
		this.skip = skip;
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return new SkipSampleIterator(super.iterator());
	}

	@Override
	public int size() {
		return InstanceCollection.UNKNOWN_SIZE;
	}

	@Override
	public boolean hasSize() {
		return false;
	}

	@Override
	public InstanceCollection select(Filter filter) {
		// filter the samples
		return new FilteredInstanceCollection(this, filter);
	}

}
