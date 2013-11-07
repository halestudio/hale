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

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.InstanceCollectionDecorator;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.InstanceIteratorDecorator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Simple implementation of an instance collection where only a specific number
 * of instances is returned.
 * 
 * @author Simon Templer
 */
public class FirstOverallInstances extends InstanceCollectionDecorator {

	/**
	 * Iterator that returns at maximum a specific number of instances.
	 */
	private class FirstSampleIterator extends InstanceIteratorDecorator {

		private int toServe = max;

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
			if (toServe <= 0) {
				return false;
			}

			return super.hasNext();
		}

		@Override
		public Instance next() {
			toServe--;
			return super.next();
		}

		@Override
		public void skip() {
			toServe--;
			super.skip();
		}

	}

	private final int max;

	/**
	 * Constructor.
	 * 
	 * @param instances the original instance collection
	 * @param max the maximum number of instances per type
	 */
	public FirstOverallInstances(InstanceCollection instances, int max) {
		super(instances);
		this.max = max;
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return new FirstSampleIterator(super.iterator());
	}

	@Override
	public int size() {
		return Math.min(super.size(), max);
	}

	@Override
	public InstanceCollection select(Filter filter) {
		// filter the samples
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	@Override
	public boolean supportsFanout() {
		// with a fan-out we could not guarantee to get the first instances
		return false;
	}

	@Override
	public Map<TypeDefinition, InstanceCollection> fanout() {
		return null;
	}

}
