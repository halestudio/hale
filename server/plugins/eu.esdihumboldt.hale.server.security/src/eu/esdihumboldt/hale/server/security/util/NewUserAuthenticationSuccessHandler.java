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

package eu.esdihumboldt.hale.server.security.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import eu.esdihumboldt.hale.server.security.ExtendedUserDetails;

/**
 * Authentication success handler that can redirect new users to a specific URL.
 * 
 * @author Simon Templer
 */
public class NewUserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private String newUserRedirectUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		if (newUserRedirectUrl != null
				&& authentication.getPrincipal() instanceof ExtendedUserDetails
				&& ((ExtendedUserDetails) authentication.getPrincipal()).isNewUser()) {
			response.sendRedirect(newUserRedirectUrl);
			clearAuthenticationAttributes(request);
			return;
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}

	/**
	 * @return the URL a new user should be redirected to
	 */
	public String getNewUserRedirectUrl() {
		return newUserRedirectUrl;
	}

	/**
	 * Set the URL a new user should be redirected to.
	 * 
	 * @param newUserRedirectUrl the URL to redirect new users to
	 */
	public void setNewUserRedirectUrl(String newUserRedirectUrl) {
		this.newUserRedirectUrl = newUserRedirectUrl;
	}

}
