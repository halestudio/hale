/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.io.groovy.snippets;

import java.util.Optional;

/**
 * Service interface for managing snippet resources.
 * 
 * @author Simon Templer
 */
public interface SnippetService {

	/**
	 * Add a snippet.
	 * 
	 * @param resourceId the resource identifier
	 * @param snippet the snippet
	 */
	void addSnippet(String resourceId, Snippet snippet);

	/**
	 * Remove the snippet with the given resource identifier.
	 * 
	 * @param resourceId the resource identifier
	 */
	void removeSnippet(String resourceId);

	/**
	 * Remove all snippets.
	 */
	void clearSnippets();

	/**
	 * Get the snippet with the given resource identifier.
	 * 
	 * @param resourceId the resource identifier
	 * @return the snippet if available
	 */
	Optional<Snippet> getResourceSnippet(String resourceId);

	/**
	 * Get the snippet with the given snippet identifier.
	 * 
	 * @param identifier the snippet identifier
	 * @return the snippet if available
	 */
	Optional<Snippet> getSnippet(String identifier);

}
