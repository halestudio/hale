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

package eu.esdihumboldt.cst.internal.util;

import java.util.concurrent.atomic.AtomicInteger;

import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Instance sink decorator that counts the added instances.
 * 
 * @author Simon Templer
 */
public class CountingInstanceSink implements InstanceSink {

	private final InstanceSink decoratee;

	private final AtomicInteger count = new AtomicInteger();

	/**
	 * Create a counting instance sink
	 * 
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
	 * 
	 * @param count the current count
	 */
	protected void countChanged(int count) {
		// override me
	}

}
