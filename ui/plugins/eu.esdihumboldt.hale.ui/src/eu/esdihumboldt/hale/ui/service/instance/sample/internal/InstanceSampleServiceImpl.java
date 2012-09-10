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
