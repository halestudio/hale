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
package eu.esdihumboldt.hale.models.filterimpl;

import org.opengis.feature.Feature;

import eu.esdihumboldt.hale.models.FeatureFilter;

/**
 * Out of a given set of {@link Feature}s, this {@link FeatureFilter} 
 * implementation will only return those with minimum or maximum attributive 
 * values.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class MinMaxFeatureFilter 
	implements FeatureFilter {

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.models.FeatureFilter#filter(org.opengis.feature.Feature)
	 */
	public boolean filter(Feature feature) {
		// TODO Auto-generated method stub
		return false;
	}

}
