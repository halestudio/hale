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

package eu.esdihumboldt.hale.common.core.io.supplier;

import java.util.Collection;

/**
 * {@link LocatableInputSupplier} with the ability to provide a collection of
 * {@link LocatableInputSupplier}s representing partitions of itself.
 * 
 * @see #getLocation()
 * @author Florian Esser
 */
public interface PartitioningInputSupplier<T> extends LocatableInputSupplier<T> {

	/**
	 * Provides a {@link Collection} of {@link LocatableInputSupplier}s, each
	 * representing a partition of the input represented by this object. If no
	 * partitioning can be performed, this function may return the
	 * <code>PartitioningInputSupplier</code> itself as the only element of the
	 * <code>Collection</code>.
	 * 
	 * @return the partitions
	 */
	Collection<LocatableInputSupplier<T>> getPartitions();
}
