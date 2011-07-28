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

package eu.esdihumboldt.hale.ui.views.data.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.ui.views.data.ReferenceSampleService;

/**
 * Reference data sample service implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReferenceSampleServiceImpl extends Observable implements ReferenceSampleService {
	
	private Collection<Instance> sample = new ArrayList<Instance>();

	/**
	 * @see ReferenceSampleService#getReferenceInstances()
	 */
	@Override
	public Collection<Instance> getReferenceInstances() {
		return sample;
	}

	/**
	 * @see ReferenceSampleService#setReferenceInstances(Collection)
	 */
	@Override
	public void setReferenceInstances(Collection<Instance> instances) {
		this.sample = instances;
		
		notifyObservers();
	}

}
