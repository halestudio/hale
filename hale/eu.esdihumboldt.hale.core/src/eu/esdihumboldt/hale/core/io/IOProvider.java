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

package eu.esdihumboldt.hale.core.io;

import java.util.Set;

/**
 * Interface for I/O providers
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface IOProvider {
	
	//TODO configuration stuff?
	
	/**
	 * Get the supported configuration parameters.
	 * 
	 * @return the supported parameters  
	 */
	public Set<String> getSupportedParameters();
	
	/**
	 * Set a parameter
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 */
	public void setParameter(String name, String value);
	
	/**
	 * Get the value for the given parameter name 
	 * 
	 * @param name the parameter name
	 * @return the parameter value or <code>null</code>
	 */
	public String getParameter(String name);
	
}
