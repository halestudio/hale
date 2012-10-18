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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

/**
 * Session authentication strategy that in addition to the functionality of the
 * base class supports populating a {@link SessionLogoutService}.
 * 
 * @author Simon Templer
 */
public class MultiWarSessionAuthenticationStrategy extends SessionFixationProtectionStrategy {

	private SessionLogoutService logoutService;

	/**
	 * @param logoutService the logoutService to set
	 */
	public void setLogoutService(SessionLogoutService logoutService) {
		this.logoutService = logoutService;
	}

	@Override
	public void onAuthentication(Authentication authentication, HttpServletRequest request,
			HttpServletResponse response) {
		super.onAuthentication(authentication, request, response);

		if (logoutService != null) {
			logoutService.addLoginSession(request.getSession(), authentication);
		}
	}

}
