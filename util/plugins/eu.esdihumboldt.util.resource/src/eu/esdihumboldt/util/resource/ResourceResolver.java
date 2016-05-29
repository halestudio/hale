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

package eu.esdihumboldt.util.resource;

import java.io.InputStream;
import java.net.URI;

import eu.esdihumboldt.util.io.InputSupplier;

/**
 * Resolve URIs to an input supplier.
 * 
 * @author Simon Templer
 */
public interface ResourceResolver {

	/**
	 * Resolves an URI to an input supplier.
	 * 
	 * @param uri the URI
	 * @return the input supplier
	 * @throws ResourceNotFoundException if the resource was not found by the
	 *             resolver
	 */
	public InputSupplier<? extends InputStream> resolve(URI uri) throws ResourceNotFoundException;

}
