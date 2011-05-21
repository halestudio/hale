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

package eu.esdihumboldt.hale.ui.views.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opengis.feature.Feature;

import eu.esdihumboldt.hale.models.HaleServiceListener;

/**
 * Reference data sample service implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ReferenceSampleServiceImpl implements ReferenceSampleService {
	
	private final Set<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();
	
	private Collection<Feature> sample = new ArrayList<Feature>();

	/**
	 * @see ReferenceSampleService#addListener(HaleServiceListener)
	 */
	@Override
	public void addListener(HaleServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see ReferenceSampleService#getReferenceFeatures()
	 */
	@Override
	public Collection<Feature> getReferenceFeatures() {
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
	 * @see ReferenceSampleService#setReferenceFeatures(Collection)
	 */
	@Override
	public void setReferenceFeatures(Collection<Feature> features) {
		this.sample = features;
		
		for (HaleServiceListener listener : listeners) {
			listener.update(null);
		}
	}

}
