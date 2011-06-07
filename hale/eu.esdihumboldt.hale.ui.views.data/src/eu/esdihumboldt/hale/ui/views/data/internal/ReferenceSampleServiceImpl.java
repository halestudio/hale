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
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.ui.service.HaleServiceListener;
import eu.esdihumboldt.hale.ui.views.data.ReferenceSampleService;

/**
 * Reference data sample service implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReferenceSampleServiceImpl implements ReferenceSampleService {
	
	private final Set<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();
	
	private Collection<Instance> sample = new ArrayList<Instance>();

	/**
	 * @see ReferenceSampleService#addListener(HaleServiceListener)
	 */
	@Override
	public void addListener(HaleServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see ReferenceSampleService#getReferenceInstances()
	 */
	@Override
	public Collection<Instance> getReferenceInstances() {
		return sample;
	}

	/**
	 * @see ReferenceSampleService#removeListener(HaleServiceListener)
	 */
	@Override
	public void removeListener(HaleServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see ReferenceSampleService#setReferenceInstances(Collection)
	 */
	@Override
	public void setReferenceInstances(Collection<Instance> instances) {
		this.sample = instances;
		
		for (HaleServiceListener listener : listeners) {
			listener.update(null);
		}
	}

}
