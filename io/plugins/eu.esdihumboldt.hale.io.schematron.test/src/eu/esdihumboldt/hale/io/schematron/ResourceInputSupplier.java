/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.schematron;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Input supplier for a resource loaded from the classpath.
 * 
 * @author Simon Templer
 */
public class ResourceInputSupplier implements LocatableInputSupplier<InputStream> {

	private final Class<?> clazz;
	private final String resource;

	/**
	 * Create a new input supplier for a given resource residing next to a given
	 * class.
	 * 
	 * @param clazz the class for locating the resource
	 * @param resource the resource path
	 */
	public ResourceInputSupplier(Class<?> clazz, String resource) {
		this.clazz = clazz;
		this.resource = resource;
	}

	@Override
	public InputStream getInput() throws IOException {
		return clazz.getResourceAsStream(resource);
	}

	@Override
	public URI getLocation() {
		try {
			return clazz.getResource(resource).toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public URI getUsedLocation() {
		return getLocation();
	}

}
