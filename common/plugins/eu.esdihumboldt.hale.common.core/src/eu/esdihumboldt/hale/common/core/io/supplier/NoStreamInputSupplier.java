/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.supplier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Input supplier based on a URI that does not support acquiring a stream.
 * 
 * @author Simon Templer
 */
public class NoStreamInputSupplier implements LocatableInputSupplier<InputStream> {

	private final URI location;

	/**
	 * Create a supplier with the given location.
	 * 
	 * @param location the location URI
	 */
	public NoStreamInputSupplier(URI location) {
		super();
		this.location = location;
	}

	@Override
	public InputStream getInput() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public URI getLocation() {
		return location;
	}

	@Override
	public URI getUsedLocation() {
		return location;
	}

}
