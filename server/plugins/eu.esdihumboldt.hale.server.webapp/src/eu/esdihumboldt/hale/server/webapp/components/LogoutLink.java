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

package eu.esdihumboldt.hale.server.webapp.components;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

/**
 * Logout link.
 * 
 * @author Simon Templer
 */
public class LogoutLink extends Link<Void> {

	private static final long serialVersionUID = -350923220167324778L;

	/**
	 * @see Link#Link(String)
	 */
	public LogoutLink(String id) {
		super(id);
	}

	/**
	 * @see Link#onClick()
	 */
	@Override
	public void onClick() {
		WebRequest webRequest = (WebRequest) getRequest();
		WebResponse webResponse = (WebResponse) getResponse();

		LogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.logout(webRequest.getHttpServletRequest(), webResponse
				.getHttpServletResponse(), SecurityContextHolder.getContext().getAuthentication());

		// try routing to home page
		setResponsePage(getApplication().getHomePage());
	}

}
