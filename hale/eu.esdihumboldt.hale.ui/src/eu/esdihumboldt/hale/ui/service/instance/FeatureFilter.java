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
package eu.esdihumboldt.hale.ui.service.instance;

import org.opengis.feature.Feature;

/**
 * This interface represents a strategy pattern that can be used to only
 * selectively use {@link Feature}s in the {@link InstanceService}.
 * 
 * @author Thorsten Reitz
 */
public interface FeatureFilter {

	/**
	 * @param feature the {@link Feature} to test.
	 * @return true if the {@link Feature} fulfills the filtering role set for 
	 * this {@link FeatureFilter}.
	 */
	public boolean filter(Feature feature);
	
}
