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

package eu.esdihumboldt.util.resource.scavenger;

import java.io.File;
import java.util.Set;

/**
 * Service that scans for specific resources in a location and keeps references
 * to them. Each resource has a unique identifier and usually occupies its own
 * directory and the scavenger may allow to create a new directory for creating
 * a new resource.
 * 
 * @param <R> the resource reference type
 * @author Simon Templer
 */
public interface ResourceScavenger<R> {

	/**
	 * Check if there are any new projects available.
	 */
	public void triggerScan();

	/**
	 * Get the identifiers of the available resources.
	 * 
	 * @return the set of identifiers of all available resources
	 */
	public Set<String> getResources();

	/**
	 * Reserve a resource identifier, e.g. if a new resource should be created
	 * 
	 * @param resourceId the resource identifier
	 * @return the resource directory
	 * @throws ScavengerException if the resourceId is already taken/reserved or
	 *             adding new resources is not possible
	 */
	public File reserveResourceId(String resourceId) throws ScavengerException;

	/**
	 * Release a previously reserved resource identifier. Also removes the
	 * resource folder.
	 * 
	 * @param resourceId the resource identifier
	 */
	public void releaseResourceId(String resourceId);

	/**
	 * Determines if adding a resource generally is allowed/possible.
	 * 
	 * @return if adding new resources is allowed
	 */
	public boolean allowAddResource();

	/**
	 * Get the resource reference with the given identifier.
	 * 
	 * @param resourceId the resource identifier
	 * @return the resource reference or <code>null</code> if it does not exist
	 */
	public R getReference(String resourceId);

}
