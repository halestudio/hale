/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
