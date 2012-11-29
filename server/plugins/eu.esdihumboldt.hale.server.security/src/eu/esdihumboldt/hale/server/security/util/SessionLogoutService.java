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

package eu.esdihumboldt.hale.server.security.util;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;

/**
 * Logout a specific user from multiple sessions/applications tracked by the
 * service.
 * 
 * @author Simon Templer
 */
public interface SessionLogoutService {

	/**
	 * Add a session after the user was authenticated.
	 * 
	 * @param session the HTTP session
	 * @param authentication the successful authentication
	 */
	public void addLoginSession(HttpSession session, Authentication authentication);

	/**
	 * Logout the user authenticated by the given authentication object.
	 * 
	 * @param authentication the user authentication
	 */
	public void logout(Authentication authentication);

}
