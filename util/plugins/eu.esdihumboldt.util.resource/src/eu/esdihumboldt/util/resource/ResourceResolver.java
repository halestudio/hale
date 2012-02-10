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

package eu.esdihumboldt.util.resource;

import java.io.InputStream;
import java.net.URI;

import com.google.common.io.InputSupplier;

/**
 * Resolve URIs to an input supplier.
 * @author Simon Templer
 */
public interface ResourceResolver {
	
	/**
	 * Resolves an URI to an input supplier.
	 * @param uri the URI
	 * @return the input supplier
	 * @throws ResourceNotFoundException if the resource was not found by the
	 *   resolver
	 */
	public InputSupplier<? extends InputStream> resolve(URI uri) throws ResourceNotFoundException;

}
