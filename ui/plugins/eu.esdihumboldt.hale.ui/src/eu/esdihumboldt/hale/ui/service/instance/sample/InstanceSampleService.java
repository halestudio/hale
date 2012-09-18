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

package eu.esdihumboldt.hale.ui.service.instance.sample;

import java.util.Collection;
import java.util.Observer;

import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Reference sample service
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface InstanceSampleService {

	/**
	 * Set the reference instance sample
	 * 
	 * @param instances the reference instance sample
	 */
	public void setReferenceInstances(Collection<Instance> instances);

	/**
	 * Get the reference instance sample
	 * 
	 * @return the reference instance sample
	 */
	public Collection<Instance> getReferenceInstances();

	/**
	 * Adds a listener
	 * 
	 * @param listener the listener to add
	 */
	public void addObserver(Observer listener);

	/**
	 * Removes a listener
	 * 
	 * @param listener the listener to be removed
	 */
	public void deleteObserver(Observer listener);

}
