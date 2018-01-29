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

package eu.esdihumboldt.hale.common.instance.tools.impl;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.tools.InstanceCollectionPartitioner;

/**
 * Instance partitioner that splits an instance collection independent of its
 * content.
 * 
 * Each part has to be consumed in order.
 * 
 * @author Simon Templer
 */
public class SimplePartitioner implements InstanceCollectionPartitioner {

	@Override
	public boolean usesReferences() {
		return false;
	}

	@Override
	public ResourceIterator<InstanceCollection> partition(InstanceCollection instances,
			int maxObjects, SimpleLog log) {
		return new PartitionIterator(instances, maxObjects);
	}

	@Override
	public boolean requiresImmediateConsumption() {
		// parts must be handled in sequence, next() may not be called before
		// the previous part has been completely handled
		return true;
	}

}
