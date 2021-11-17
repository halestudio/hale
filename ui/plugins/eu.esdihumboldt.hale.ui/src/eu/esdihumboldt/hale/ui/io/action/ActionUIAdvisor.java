/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.io.action;

/**
 * Advisor for handling resources of a specific action.
 * 
 * @param <T> the resource representation type
 * @author Simon Templer
 */
public interface ActionUIAdvisor<T> {

	/**
	 * States if resource removal is supported.
	 * @param resourceId the resource identifier
	 * 
	 * @return if removal of a single resource based on its ID is supported
	 */
	public boolean supportsRemoval(String resourceId);

	/**
	 * Remove the resource with the given ID.
	 * 
	 * @param resourceId the identifier of the resource to remove
	 * @return if the resource could be removed
	 * @see #supportsRemoval()
	 */
	public boolean removeResource(String resourceId);

	/**
	 * States if clearing all action resources is supported.
	 * 
	 * @return if clear is supported
	 */
	public boolean supportsClear();

	/**
	 * Clear all resources of the action the advisor is associated to.
	 * 
	 * @return <code>true</code> if all resources could be removed,
	 *         <code>false</code> if none were removed
	 * @see #supportsClear()
	 */
	public boolean clear();

	/**
	 * States if retrieving a representation of the resource is possible.
	 * 
	 * @return if resource representation retrieval is possible
	 */
	public boolean supportsRetrieval();

	/**
	 * @return the type of object a resource is represented by, if
	 *         representation retrieval is supported
	 * @see #supportsRetrieval()
	 */
	public Class<T> getRepresentationType();

	/**
	 * Retrieve a resource representation for the resource with the given ID.
	 * 
	 * @param resourceId the resource identifier
	 * @return the resource representation or <code>null</code>
	 * @see #supportsRetrieval()
	 */
	public T retrieveResource(String resourceId);

	/**
	 * States if providing a custom name for a resource is supported.
	 * 
	 * @return if retrieving a custom name for a resource is supported
	 */
	default boolean supportsCustomName() {
		return false;
	}

	/**
	 * Determine the name for the resource with the given identifier.
	 * 
	 * @param resourceId the resource identifier
	 * @return the resource name or <code>null</code>
	 * @see #supportsCustomName()
	 */
	default String getCustomName(String resourceId) {
		return null;
	}

}
