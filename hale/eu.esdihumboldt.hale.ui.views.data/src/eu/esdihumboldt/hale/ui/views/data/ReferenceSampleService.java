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

import java.util.Collection;

import org.opengis.feature.Feature;

import eu.esdihumboldt.hale.ui.service.HaleServiceListener;


/**
 * Reference sample service
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface ReferenceSampleService {
	
	/**
	 * Set the reference feature sample
	 * 
	 * @param features the reference feature sample
	 */
	public void setReferenceFeatures(Collection<Feature> features);
	
	/**
	 * Get the reference feature sample
	 * 
	 * @return the reference feature sample
	 */
	public Collection<Feature> getReferenceFeatures();
	
	/**
	 * Adds a listener
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(HaleServiceListener listener);
	
	/**
	 * Removes a listener
	 * 
	 * @param listener the listener to be removed
	 */
	public void removeListener(HaleServiceListener listener);

}
