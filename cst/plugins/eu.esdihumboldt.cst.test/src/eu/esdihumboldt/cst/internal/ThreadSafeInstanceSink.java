/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.internal;

import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Decorator for an instance sink that ensures thread safe addition to the sink.
 * 
 * @author Simon Templer
 * @param <T> the instance sink type
 */
public class ThreadSafeInstanceSink<T extends InstanceSink> implements InstanceSink {

	private final T decoratee;

	/**
	 * Create a new thread safe instance sink.
	 * 
	 * @param decoratee the instance sink to decorate
	 */
	public ThreadSafeInstanceSink(T decoratee) {
		this.decoratee = decoratee;
	}

	@Override
	public synchronized void addInstance(Instance instance) {
		decoratee.addInstance(instance);
	}

	/**
	 * @return the decorated instance sink
	 */
	public T getDecoratee() {
		return decoratee;
	}

}
