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

package eu.esdihumboldt.cst.internal.util;

import java.util.concurrent.atomic.AtomicInteger;

import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Instance sink decorator that counts the added instances.
 * @author Simon Templer
 */
public class CountingInstanceSink implements InstanceSink {
	
	private final InstanceSink decoratee;
	
	private final AtomicInteger count = new AtomicInteger();

	/**
	 * Create a counting instance sink
	 * @param decoratee the internal instance sink to use
	 */
	public CountingInstanceSink(InstanceSink decoratee) {
		super();
		this.decoratee = decoratee;
	}

	/**
	 * @see InstanceSink#addInstance(Instance)
	 */
	@Override
	public void addInstance(Instance instance) {
		decoratee.addInstance(instance);
		
		countChanged(count.incrementAndGet());
	}

	/**
	 * Called when the count has been incremented
	 * @param count the current count
	 */
	protected void countChanged(int count) {
		// override me
	}

}
