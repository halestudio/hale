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

package eu.esdihumboldt.hale.common.instance.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic instance collection interface.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface InstanceCollection extends InstanceResolver {

	/**
	 * Get an iterator over all instances contained in the collection. The
	 * iterator must be disposed after use (using
	 * {@link ResourceIterator#close()}).
	 * 
	 * @return an iterator over the instances
	 */
	public ResourceIterator<Instance> iterator();

	/**
	 * Constant for unknown collection size
	 */
	public static final int UNKNOWN_SIZE = -1;

	/**
	 * States if the collection has a known size.
	 * 
	 * @return if the collection size is known
	 */
	public boolean hasSize();

	/**
	 * Get the collection size if known.
	 * 
	 * @see #hasSize()
	 * 
	 * @return the collection size or {@link #UNKNOWN_SIZE}
	 */
	public int size();

	/**
	 * States if the collection has no instances. This must return a valid value
	 * even if {@link #hasSize()} returns false.
	 * 
	 * @return if the collection is empty
	 */
	public boolean isEmpty();

	/**
	 * Select the instances in the collection, matching the given filter.
	 * 
	 * @param filter the instance filter
	 * @return the instance collection representing the selection
	 */
	public InstanceCollection select(Filter filter);

	// TODO what else is needed?
	// public InstanceCollection[] partition(...);

	/**
	 * Helper for converting to list of instances.
	 * 
	 * Use with care only in cases where it is clear that the data is not too
	 * big.
	 * 
	 * @return a list containing all instances
	 */
	default List<Instance> toList() {
		List<Instance> result = new ArrayList<Instance>();
		try (ResourceIterator<Instance> iterator = iterator()) {
			while (iterator.hasNext()) {
				result.add(iterator.next());
			}
		}
		return result;
	}

}
