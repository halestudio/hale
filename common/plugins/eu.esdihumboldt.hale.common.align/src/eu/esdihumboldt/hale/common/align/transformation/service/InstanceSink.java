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

package eu.esdihumboldt.hale.common.align.transformation.service;

import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Sink for instances
 * 
 * @author Simon Templer
 * @since 2.5.0
 */
public interface InstanceSink {

	/**
	 * Adds an instance to the sink
	 * 
	 * @param instance the instance to add
	 */
	public void addInstance(Instance instance);

}
