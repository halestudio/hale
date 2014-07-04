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

package eu.esdihumboldt.hale.server.api.base

import javax.servlet.http.HttpServletRequest



/**
 * Rest API utility methods.
 * 
 * @author Simon Templer
 */
class APIUtil {

	/**
	 * Name of the system property specifying the base server URL to be used
	 * for accessing the API. Useful in case the API runs behind a proxy. 
	 */
	public static final String SYSTEM_PROPERTY_SERVER_URL = 'hale.api.base-url'

	/**
	 * Get the API base URL.
	 *
	 * @param request the HTTP servlet request
	 * @return the base URL w/o trailing slash
	 */
	static String getBaseUrl(HttpServletRequest request) {
		String overrideBaseUrl = System.getProperty(SYSTEM_PROPERTY_SERVER_URL)

		StringBuilder builder = new StringBuilder()
		if (overrideBaseUrl) {
			builder << overrideBaseUrl
		}
		else {
			builder << request.scheme
			builder << '://'
			builder << request.serverName
			builder << ':'
			builder << request.serverPort
		}
		builder << request.contextPath
		String servPath = request.servletPath
		if (servPath) {
			builder << servPath
		}
		builder.toString();
	}
}
