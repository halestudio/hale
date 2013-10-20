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

package eu.esdihumboldt.hale.ui.service.project;

import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.content.IContentType;

import com.google.common.base.Predicate;

import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.util.Pair;

/**
 * Manages information on recently loaded resources.
 * 
 * @author Simon Templer
 */
public interface RecentResources {

	/**
	 * Add a resource w/o load configuration.
	 * 
	 * @param contentTypeId the identifier of the content type of the resource
	 * @param uri the resource location
	 */
	public void addResource(String contentTypeId, URI uri);

	/**
	 * Add a resource.
	 * 
	 * @param resource the resource to add
	 */
	public void addResource(Resource resource);

	/**
	 * Get the recent resource location based on the given content types
	 * (independent of actions).
	 * 
	 * @param contentTypes the content types
	 * @param restrictToFiles if the resources should be restricted to files
	 * @return the list of recent resource locations for the given content types
	 */
	public List<Pair<URI, IContentType>> getRecent(Iterable<? extends IContentType> contentTypes,
			boolean restrictToFiles);

	/**
	 * Get the recent resource location based on the given content types
	 * (independent of actions).
	 * 
	 * @param contentTypes the content types
	 * @param accept the predicate specifying which resources to accept,
	 *            <code>null</code> to accept all
	 * @return the list of recent resource locations for the given content types
	 */
	public List<Pair<URI, IContentType>> getRecent(Iterable<? extends IContentType> contentTypes,
			Predicate<URI> accept);

	/**
	 * Get the recent resource for a given action.
	 * 
	 * @param actionId the action identifier
	 * @return the list of recent resources for the given action
	 */
	public List<Resource> getRecent(String actionId);

}
