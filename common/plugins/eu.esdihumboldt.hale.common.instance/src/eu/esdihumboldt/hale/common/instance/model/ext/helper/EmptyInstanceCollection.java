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

package eu.esdihumboldt.hale.common.instance.model.ext.helper;

import java.util.Map;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceCollection2;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Empty instance collection.
 * 
 * @author Simon Templer
 */
public class EmptyInstanceCollection implements InstanceCollection2 {

	/**
	 * The empty instance collection instance.
	 */
	public static final EmptyInstanceCollection INSTANCE = new EmptyInstanceCollection();

	@Override
	public ResourceIterator<Instance> iterator() {
		return new InstanceIterator() {

			@Override
			public void remove() {
				// do nothing
			}

			@Override
			public Instance next() {
				return null;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public void close() {
				// do nothing
			}

			@Override
			public TypeDefinition typePeek() {
				return null;
			}

			@Override
			public boolean supportsTypePeek() {
				return false;
			}

			@Override
			public void skip() {
				// do nothing
			}
		};
	}

	private EmptyInstanceCollection() {
		super();
	}

	@Override
	public boolean hasSize() {
		return true;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public InstanceCollection select(Filter filter) {
		return this;
	}

	@Override
	public InstanceReference getReference(Instance instance) {
		return null;
	}

	@Override
	public Instance getInstance(InstanceReference reference) {
		return null;
	}

	@Override
	public boolean supportsFanout() {
		return false;
	}

	@Override
	public Map<TypeDefinition, InstanceCollection> fanout() {
		return null;
	}

}
