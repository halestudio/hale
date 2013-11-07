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

package eu.esdihumboldt.hale.ui.service.instance.sample.internal.sampler.first;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.FullInstanceIteratorSupport;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.InstanceCollectionDecorator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import gnu.trove.TObjectIntHashMap;

/**
 * Simple implementation of an instance collection where only a specific number
 * of instances per types is returned.
 * 
 * @author Simon Templer
 */
public class FirstInstancesPerType extends InstanceCollectionDecorator {

	/**
	 * Iterator that returns at maximum a specific number of instances per type.
	 */
	private class FirstSampleIterator extends FullInstanceIteratorSupport {

		private final TObjectIntHashMap<TypeDefinition> typeCount = new TObjectIntHashMap<>();

		/**
		 * Constructor.
		 * 
		 * @param decoratee the decoratee
		 */
		public FirstSampleIterator(ResourceIterator<Instance> decoratee) {
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
			while (super.hasNext() && typeCount.get(super.typePeek()) >= max) {
				super.skip();
			}
		}

		@Override
		public Instance next() {
			proceedToNextValid();

			Instance result = super.next();
			typeCount.adjustOrPutValue(result.getDefinition(), 1, 1);
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
			super.skip();
		}

		@Override
		public boolean supportsTypePeek() {
			return true;
		}

	}

	private final int max;

	/**
	 * Constructor.
	 * 
	 * @param instances the original instance collection
	 * @param max the maximum number of instances per type
	 */
	public FirstInstancesPerType(InstanceCollection instances, int max) {
		super(instances);
		this.max = max;
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return new FirstSampleIterator(super.iterator());
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
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	@Override
	public Map<TypeDefinition, InstanceCollection> fanout() {
		Map<TypeDefinition, InstanceCollection> fanout = super.fanout();

		if (fanout != null) {
			// individually sample each instance collection
			return Maps.transformValues(fanout,
					new Function<InstanceCollection, InstanceCollection>() {

						@Override
						public InstanceCollection apply(InstanceCollection org) {
							return new FirstOverallInstances(org, max);
						}

					});
		}

		return null;
	}

}
