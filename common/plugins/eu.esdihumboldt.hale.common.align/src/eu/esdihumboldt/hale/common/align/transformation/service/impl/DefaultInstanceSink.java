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

package eu.esdihumboldt.hale.common.align.transformation.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Default instance sink backed by a list
 * 
 * @author Simon Templer
 * @since 2.5.0
 */
public class DefaultInstanceSink implements InstanceSink {

	private final List<Instance> instances = new ArrayList<Instance>();

	/**
	 * @see InstanceSink#addInstance(Instance)
	 */
	@Override
	public void addInstance(Instance instance) {
		instances.add(instance);
	}

	/**
	 * Get the instances collected in the sink.
	 * 
	 * @return the instances
	 */
	public List<Instance> getInstances() {
		return Collections.unmodifiableList(instances);
	}

}
