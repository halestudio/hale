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
 * Base interface I/O provider factories
 * @param <T> the concrete provider type 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface IOProviderFactory<T extends IOProvider> {
	
	/**
	 * Get the provider factory identifier
	 * 
	 * @return the factory identifier
	 */
	public String getIdentifier();
	
	/**
	 * Get the display name of the provider factory
	 * 
	 * @return the display name of the factory
	 */
	public String getDisplayName();
	
	/**
	 * Get the supported content types
	 * 
	 * @return the set of supported content types
	 */
	public Set<ContentType> getSupportedTypes();
	
	/**
	 * Create an I/O provider instance
	 * 
	 * @return the provider instance
	 */
	public T createProvider();

}
