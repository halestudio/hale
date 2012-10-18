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

import java.lang.reflect.Method;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.ReflectionUtils;

/**
 * Sets the remember me cookie always for the root context. Calls a
 * {@link SessionLogoutService} on logout if available, to also remove the
 * authentication information from sessions in other web applications. In
 * contrast to the usual remember me, the cookie is deleted when the browser is
 * closed.
 * 
 * @author Simon Templer
 */
public class MultiWarRememberMeServices extends TokenBasedRememberMeServices {

	private final Method setHttpOnlyMethod;

	private SessionLogoutService logoutService;

	/**
	 * @see TokenBasedRememberMeServices#TokenBasedRememberMeServices(String,
	 *      UserDetailsService)
	 */
	public MultiWarRememberMeServices(String key, UserDetailsService userDetailsService) {
		super(key, userDetailsService);

		this.setHttpOnlyMethod = ReflectionUtils.findMethod(Cookie.class, "setHttpOnly",
				boolean.class);
	}

	/**
	 * @param logoutService the logoutService to set
	 */
	public void setLogoutService(SessionLogoutService logoutService) {
		this.logoutService = logoutService;
	}

	@Override
	protected void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = new Cookie(getCookieName(), null);
		cookie.setMaxAge(0);
		cookie.setPath(getCookiePath(request));

		response.addCookie(cookie);
	}

	/**
	 * Get the cookie path. Always returns the root context <code>/</code>.
	 * 
	 * @param request the HTTP servlet request
	 * @return the cookie path
	 */
	protected String getCookiePath(HttpServletRequest request) {
		return "/"; // always return the root context
//		String contextPath = request.getContextPath();
//		return contextPath.length() > 0 ? contextPath : "/";
	}

	@Override
	protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request,
			HttpServletResponse response) {
		String cookieValue = encodeCookie(tokens);
		Cookie cookie = new Cookie(getCookieName(), cookieValue);
//		cookie.setMaxAge(maxAge);
		cookie.setMaxAge(-1); // only live through browser runtime
		cookie.setPath(getCookiePath(request));

		cookie.setSecure(request.isSecure());

		if (setHttpOnlyMethod != null) {
			ReflectionUtils.invokeMethod(setHttpOnlyMethod, cookie, Boolean.TRUE);
		}

		response.addCookie(cookie);
	}

	/**
	 * @see AbstractRememberMeServices#logout(HttpServletRequest,
	 *      HttpServletResponse, Authentication)
	 */
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		super.logout(request, response, authentication);

		if (logoutService != null) {
//			// try to determine remote address
//			String remoteAddr = null;
//			HttpSession session = request.getSession(false);
//			if (session != null) {
//				// prefer address stored in session by
//				// SpringSessionTrackerListener
//				remoteAddr = (String) session
//						.getAttribute(SpringSessionTrackerListener.SESSION_ATTRIBUTE_REMOTE_ADDR);
//			}
//			if (remoteAddr == null) {
//				remoteAddr = request.getRemoteAddr();
//			}

			// logout
			logoutService.logout(authentication);
		}
	}

}
