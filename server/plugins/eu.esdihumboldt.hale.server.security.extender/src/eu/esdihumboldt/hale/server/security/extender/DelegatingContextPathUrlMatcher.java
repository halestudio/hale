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

package eu.esdihumboldt.hale.server.security.extender;

import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.RegexRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.web.context.WebApplicationContext;

import eu.esdihumboldt.util.reflection.ReflectionHelper;

/**
 * <p>
 * A {@link RequestMatcher} that is also aware of the current context path and
 * is able to use it for matching. After evaluating the context path this
 * matcher delegates the call to another request matcher.
 * </p>
 * <p>
 * Context paths begin with a double slash and end with a single one. For
 * example
 * 
 * <pre>
 * <code>//hale/version</code>
 * </pre>
 * 
 * yields to the URL <code>/version</code> in the context path
 * <code>/hale</code>.
 * </p>
 * <p>
 * Currently, this class only handles delegates of type
 * {@link AntPathRequestMatcher} and {@link RegexRequestMatcher}, because these
 * are the only ones that match against the servlet path. All others currently
 * implemented in Spring match against some other attribute (such as IP address
 * for example).
 * </p>
 * 
 * @author Michel Kraemer
 */
public class DelegatingContextPathUrlMatcher implements RequestMatcher {

	/**
	 * The delegate object
	 */
	private final RequestMatcher _delegate;

	/**
	 * The context path of the current web application
	 */
	private final String _contextPath;

	/**
	 * Wraps arounds {@link HttpServletRequest} and returns a servlet path that
	 * is augmented with the current application's context path
	 */
	private class PathRequestWrapper extends HttpServletRequestWrapper {

		/**
		 * @see HttpServletRequestWrapper#HttpServletRequestWrapper(HttpServletRequest)
		 */
		public PathRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getServletPath() {
			String p = super.getServletPath();
			if (!p.isEmpty() && !p.startsWith("/")) {
				p = "/" + p;
			}
			return "//" + _contextPath + p;
		}
	}

	/**
	 * Constructs a new request matcher that delegates to the given one
	 * 
	 * @param delegate the delegate matcher
	 * @param contextPath the context path of the current web application
	 */
	private DelegatingContextPathUrlMatcher(RequestMatcher delegate, String contextPath) {
		_delegate = delegate;
		_contextPath = contextPath;
	}

	/**
	 * Checks if this class should be used to intercept calls to the given
	 * request matcher and if so returns the wrapped object. Otherwise returns
	 * the original object.
	 * 
	 * @param delegate the request matcher to wrap
	 * @param ctx the current web application context
	 * @return a new request matcher if this class supports the given one and if
	 *         calls to it should be intercepted, the original request matcher
	 *         otherwise
	 */
	public static RequestMatcher wrapIfNecessary(RequestMatcher delegate, WebApplicationContext ctx) {
		String pattern = getPattern(delegate);

		if (pattern == null) {
			// request matcher is unsupported
			return delegate;
		}

		if (!pattern.startsWith("//")) {
			// pattern does not start with a context path
			return delegate;
		}

		// find context path in pattern
		int nextSlash = pattern.indexOf('/', 2);
		String patternPath;
		if (nextSlash != -1) {
			patternPath = pattern.substring(2, nextSlash);
		}
		else {
			patternPath = pattern.substring(2);
		}

		// get current context path
		ServletContext sc = ctx.getServletContext();
		String currentPath = sc.getContextPath();
		if (!currentPath.isEmpty() && currentPath.charAt(0) == '/') {
			currentPath = currentPath.substring(1);
		}

		if (!patternPath.equals(currentPath)) {
			// pattern points to another context path
			return delegate;
		}

		// wrap it!
		return new DelegatingContextPathUrlMatcher(delegate, currentPath);
	}

	/**
	 * Retrieves the pattern from a supported request matcher
	 * 
	 * @param delegate the request matcher
	 * @return the pattern the request matcher uses to match against request
	 *         paths or null if the request matcher is unsupported
	 */
	private static String getPattern(RequestMatcher delegate) {
		if (delegate instanceof AntPathRequestMatcher) {
			AntPathRequestMatcher aprm = (AntPathRequestMatcher) delegate;
			return aprm.getPattern();
		}
		else if (delegate instanceof RegexRequestMatcher) {
			try {
				Pattern p = ReflectionHelper.getDeepPropertyOrField(delegate, "pattern",
						Pattern.class);
				if (p != null) {
					return p.pattern();
				}
				return null;
			} catch (Exception e) {
				// unsupported request matcher
				throw new RuntimeException("Request matcher is of type RegexRequestMatcher "
						+ "but its pattern could not be retrieved", e);
			}
		}
		return null;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		HttpServletRequestWrapper wrapper = new PathRequestWrapper(request);
		return _delegate.matches(wrapper);
	}
}
