/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.filter;

import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Filter for {@link Instance}s.
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
public interface Filter {
	
	/**
	 * Determines if an instance matches the filter.
	 * @param instance the instance to check the filter against
	 * @return <code>true</code> if the given instance matches the filter,
	 *   <code>false</code> otherwise
	 */
	public boolean match(Instance instance);
	
}