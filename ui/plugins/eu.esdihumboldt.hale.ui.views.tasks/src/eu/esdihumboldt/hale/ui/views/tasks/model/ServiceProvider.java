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

package eu.esdihumboldt.hale.ui.views.tasks.model;

/**
 * Provides services
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface ServiceProvider {
	
	/**
	 * Get a service instance
	 * 
	 * @param <T> the service type
	 * @param serviceType the service type
	 * 
	 * @return the service instance or <code>null</code> if none could be retrieved
	 */
	public <T> T getService(Class<T> serviceType); 

}
