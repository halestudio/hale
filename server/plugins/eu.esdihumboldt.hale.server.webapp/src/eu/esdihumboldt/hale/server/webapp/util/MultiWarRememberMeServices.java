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

package eu.esdihumboldt.hale.server.webapp.util;

import java.lang.reflect.Method;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.ReflectionUtils;

/**
 * Sets the remember me cookie always for the root context.
 * 
 * @author Simon Templer
 */
public class MultiWarRememberMeServices extends TokenBasedRememberMeServices {

	private final Method setHttpOnlyMethod;

	/**
	 * @see TokenBasedRememberMeServices#TokenBasedRememberMeServices(String,
	 *      UserDetailsService)
	 */
	public MultiWarRememberMeServices(String key, UserDetailsService userDetailsService) {
		super(key, userDetailsService);

		this.setHttpOnlyMethod = ReflectionUtils.findMethod(Cookie.class, "setHttpOnly",
				boolean.class);
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
		cookie.setMaxAge(maxAge);
		cookie.setPath(getCookiePath(request));

		cookie.setSecure(request.isSecure());

		if (setHttpOnlyMethod != null) {
			ReflectionUtils.invokeMethod(setHttpOnlyMethod, cookie, Boolean.TRUE);
		}

		response.addCookie(cookie);
	}

}
