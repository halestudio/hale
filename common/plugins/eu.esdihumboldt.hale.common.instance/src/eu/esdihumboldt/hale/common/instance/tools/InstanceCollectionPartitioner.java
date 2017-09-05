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

package eu.esdihumboldt.hale.common.instance.tools;

import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * Interface for classes that are able to partition an instance collection.
 * 
 * @author Simon Templer
 */
public interface InstanceCollectionPartitioner {

	/**
	 * States if the partitioner uses instance references meaning that the
	 * references should be efficiently resolvable and not keep the instances in
	 * memory.
	 * 
	 * @return if the partitioner uses instance references
	 */
	boolean usesReferences();

	/**
	 * Partition an instance collection.
	 * 
	 * @param instances the instances to partition
	 * @param maxObjects the maximum objects threshold that should be met for a
	 *            part if possible
	 * @return the iterator of the parts
	 */
	ResourceIterator<InstanceCollection> partition(InstanceCollection instances, int maxObjects);

}
