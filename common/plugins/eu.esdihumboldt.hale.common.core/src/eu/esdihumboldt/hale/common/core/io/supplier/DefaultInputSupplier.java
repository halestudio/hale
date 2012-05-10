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

package eu.esdihumboldt.hale.common.core.io.supplier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.google.common.io.InputSupplier;

import eu.esdihumboldt.hale.common.cache.Request;
import eu.esdihumboldt.util.resource.Resources;

/**
 * Default I/O supplier based on an URI
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2 
 */
public class DefaultInputSupplier implements LocatableInputSupplier<InputStream> {

	/**
	 * Name of the scheme where resolving locally through {@link Resources} 
	 * is preferred. 
	 */
	public static final String SCHEME_LOCAL = "resource";
	
	private final URI location;
	
	/**
	 * Create a default I/O supplier based on the given URI
	 * 
	 * @param location the location URI
	 */
	public DefaultInputSupplier(URI location) {
		super();
		this.location = location;
	}

	/**
	 * @see InputSupplier#getInput()
	 */
	@Override
	public InputStream getInput() throws IOException {
		// try resolving using resources
		boolean triedLocal = false;
		if (location.getScheme().equals(SCHEME_LOCAL)) { // prefer local
			InputSupplier<? extends InputStream> localSupplier = Resources.tryResolve(location, null);
			if (localSupplier != null) {
				try {
					triedLocal = true;
					return localSupplier.getInput();
				} catch (Throwable e) {
					// ignore
				}
			}
		}
		
		try {
			return Request.getInstance().get(location);
		} catch (Exception e) {
			try {
				return location.toURL().openStream();
			} catch (IOException ioe) {
				// try to resolve locally
				if (!triedLocal) {
					InputSupplier<? extends InputStream> localSupplier = Resources.tryResolve(location, null);
					if (localSupplier != null) {
						return localSupplier.getInput();
					}
				}
				throw ioe;
			}
		}
	}

	/**
	 * @see Locatable#getLocation()
	 */
	@Override
	public URI getLocation() {
		return location;
	}

}
