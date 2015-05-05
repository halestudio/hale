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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.test.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import eu.esdihumboldt.cst.test.TransformationExampleImpl;

/**
 * Transformation example contained in the CST test bundle.
 * 
 * @author Simon Templer
 */
public class InternalExample extends TransformationExampleImpl {

	/**
	 * Create a transformation example. All provided locations are specific to
	 * this bundle. Relative locations refer to the {@link InternalExample}
	 * class, absolute locations start with a <code>/</code>.
	 * 
	 * @param sourceSchemaLocation the source schema location
	 * @param targetSchemaLocation the target schema location
	 * @param alignmentLocation the alignment location
	 * @param sourceDataLocation the source data location
	 * @param targetDataLocation the target data location
	 * @param targetContainerNamespace the target container namespace
	 * @param targetContainerName the target container name
	 * @throws URISyntaxException if a location was invalid
	 */
	public InternalExample(String sourceSchemaLocation, String targetSchemaLocation,
			String alignmentLocation, String sourceDataLocation, String targetDataLocation,
			String targetContainerNamespace, String targetContainerName) throws URISyntaxException {
		super(toLocalURI(sourceSchemaLocation, true), toLocalURI(targetSchemaLocation, true),
				toLocalURI(alignmentLocation, true), toLocalURI(sourceDataLocation, true),
				(targetDataLocation == null) ? (null) : (toLocalURI(targetDataLocation, false)),
				targetContainerNamespace, targetContainerName);
	}

	/**
	 * Returns an URI for the given location: <br>
	 * <code>getClass().getResource(location).toURI()</code>
	 * 
	 * @param location the location
	 * @param mustExist if the location must exist (i.e. if the result may not
	 *            be <code>null</code>)
	 * @return an URI for the location
	 * @throws URISyntaxException if toURI throws an exception
	 */
	private static URI toLocalURI(String location, boolean mustExist) throws URISyntaxException {
		URL loc = InternalExample.class.getResource(location);
		if (loc == null) {
			if (mustExist) {
				throw new IllegalStateException("Could not find required resource " + location);
			}
			else {
				return null;
			}
		}

		return loc.toURI();
	}

}
