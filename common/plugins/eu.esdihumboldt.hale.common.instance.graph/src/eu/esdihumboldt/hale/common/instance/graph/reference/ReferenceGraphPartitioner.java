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

package eu.esdihumboldt.hale.common.instance.graph.reference;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.ResourceIteratorAdapter;
import eu.esdihumboldt.hale.common.instance.tools.InstanceCollectionPartitioner;

/**
 * Instance collection partitioner based on {@link ReferenceGraph}
 * 
 * @author Simon Templer
 */
public class ReferenceGraphPartitioner implements InstanceCollectionPartitioner {

	private final IdentityReferenceInspector<String> inspector;

	/**
	 * Create a reference graph based partitioner.
	 * 
	 * @param inspector the identity reference inspector
	 */
	public ReferenceGraphPartitioner(IdentityReferenceInspector<String> inspector) {
		super();
		this.inspector = inspector;
	}

	@Override
	public boolean usesReferences() {
		return true;
	}

	@Override
	public ResourceIterator<InstanceCollection> partition(InstanceCollection instances,
			int maxObjects, SimpleLog log) {
		ReferenceGraph<String> rg = new ReferenceGraph<String>(inspector, instances);
		return new ResourceIteratorAdapter<>(rg.partition(maxObjects, log));
	}

}
