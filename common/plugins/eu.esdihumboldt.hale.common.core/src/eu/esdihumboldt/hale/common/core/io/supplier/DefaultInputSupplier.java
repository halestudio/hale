/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.supplier;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import eu.esdihumboldt.hale.common.cache.Request;
import eu.esdihumboldt.util.io.InputSupplier;
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
	 * Name of the scheme where resolving locally through {@link Resources} is
	 * preferred.
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
		InputStream in = resolve(location);
		return new BufferedInputStream(in);
	}

	/**
	 * Resolve the given location and open an input stream.
	 * 
	 * @param location the location
	 * @return the input stream
	 * @throws IOException if an error occurs opening the stream
	 */
	protected InputStream resolve(URI location) throws IOException {
		// try resolving using resources
		boolean triedLocal = false;
		if (location.getScheme().equals(SCHEME_LOCAL)) { // prefer local
			InputSupplier<? extends InputStream> localSupplier = Resources.tryResolve(location,
					null);
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
			// ignore
		}

		try {
			return location.toURL().openStream();
		} catch (IOException ioe) {
			// try to resolve locally
			if (!triedLocal) {
				InputSupplier<? extends InputStream> localSupplier = Resources.tryResolve(location,
						null);
				if (localSupplier != null) {
					return localSupplier.getInput();
				}
			}
			throw ioe;
		}
	}

	/**
	 * @see Locatable#getLocation()
	 */
	@Override
	public URI getLocation() {
		return location;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultInputSupplier other = (DefaultInputSupplier) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		}
		else if (!location.equals(other.location))
			return false;
		return true;
	}

	@Override
	public URI getUsedLocation() {
		return getLocation();
	}

}
