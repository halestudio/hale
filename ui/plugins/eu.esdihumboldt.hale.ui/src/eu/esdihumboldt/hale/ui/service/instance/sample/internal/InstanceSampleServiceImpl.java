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

package eu.esdihumboldt.hale.ui.service.instance.sample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.ui.service.instance.sample.InstanceSampleService;

/**
 * Reference data sample service implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class InstanceSampleServiceImpl extends Observable implements InstanceSampleService {

	private Collection<Instance> sample = new ArrayList<Instance>();

	/**
	 * @see InstanceSampleService#getReferenceInstances()
	 */
	@Override
	public Collection<Instance> getReferenceInstances() {
		return sample;
	}

	/**
	 * @see InstanceSampleService#setReferenceInstances(Collection)
	 */
	@Override
	public void setReferenceInstances(Collection<Instance> instances) {
		this.sample = instances;

		setChanged();
		notifyObservers();
	}

}
