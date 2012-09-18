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
