/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.server.api.wadl.doc;

/**
 * Defines constants for {@link WDoc} documentation scopes.
 * 
 * @author Simon Templer
 */
public enum DocScope {

	/**
	 * Resource documentation scope.
	 */
	RESOURCE,

	/**
	 * HTTP method documentation scope.
	 */
	METHOD,

	/**
	 * Request documentation scope.
	 */
	REQUEST,

	/**
	 * Response documentation scope.
	 */
	RESPONSE,

	/**
	 * Representation documentation scope.
	 */
//	REPRESENTATION,

	/**
	 * Parameter documentation scope.
	 */
	PARAM

}
