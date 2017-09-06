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

import java.util.Collections;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.ResourceIteratorAdapter;
import eu.esdihumboldt.hale.common.instance.tools.InstanceCollectionPartitioner;

/**
 * Partitioner that does no partitioning.
 * 
 * @author Simon Templer
 */
public class NoPartitioner implements InstanceCollectionPartitioner {

	@Override
	public boolean usesReferences() {
		return false;
	}

	@Override
	public ResourceIterator<InstanceCollection> partition(InstanceCollection instances,
			int maxObjects, SimpleLog log) {
		return new ResourceIteratorAdapter<>(Collections.singleton(instances).iterator());
	}

}
