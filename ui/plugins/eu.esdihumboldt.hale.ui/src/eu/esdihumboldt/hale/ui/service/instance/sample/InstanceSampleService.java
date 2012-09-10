/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
