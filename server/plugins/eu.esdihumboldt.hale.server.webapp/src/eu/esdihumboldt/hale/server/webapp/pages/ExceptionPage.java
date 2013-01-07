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

package eu.esdihumboldt.hale.server.webapp.pages;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.springframework.security.access.AccessDeniedException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * Page for informing the user about runtime exceptions.
 * 
 * @author Simon Templer
 */
@PageDescription(title = "Error")
public class ExceptionPage extends BasePage {

	private static final ALogger log = ALoggerFactory.getLogger(ExceptionPage.class);

	private static final long serialVersionUID = 6090567597067611331L;

	private final Map<Class<? extends Exception>, ExceptionInfo<?>> infos = new LinkedHashMap<Class<? extends Exception>, ExceptionInfo<?>>();

	private Exception exception;

	@SuppressWarnings("rawtypes")
	private final ExceptionInfo info;

	private static final ExceptionInfo<Exception> DEFAULT = new ExceptionInfo<Exception>() {

		private static final long serialVersionUID = 6127547839449215497L;

		@Override
		public int getHttpErrorCode(Exception exception) {
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}

		@Override
		public Class<Exception> getExceptionType() {
			return Exception.class;
		}

		@Override
		public String getErrorTitle(Exception exception) {
			return "Internal Error";
		}

		@Override
		public String getErrorMessage(Exception exception) {
			return "An internal error occurred during this operation.";
		}
	};

	/**
	 * Create an error page based on the given exception.
	 * 
	 * @param e the exception
	 */
	@SuppressWarnings("unchecked")
	public ExceptionPage(Exception e) {
		this.exception = e;

		// log exception
		log.error("Wicket internal error: " + e.getMessage(), e);

		// custom exception handling
		// TODO make this extendable/configurable?

		// Spring Access Denied
		ExceptionInfo<?> accessDenied = new ExceptionInfo<AccessDeniedException>() {

			private static final long serialVersionUID = -3440057330226741496L;

			@Override
			public Class<AccessDeniedException> getExceptionType() {
				return AccessDeniedException.class;
			}

			@Override
			public String getErrorTitle(AccessDeniedException exception) {
				return "Access Denied";
			}

			@Override
			public String getErrorMessage(AccessDeniedException exception) {
				return "You don't have the necessary permissions to perform this operation.";
			}

			@Override
			public int getHttpErrorCode(AccessDeniedException exception) {
				return HttpServletResponse.SC_FORBIDDEN;
			}
		};
		infos.put(accessDenied.getExceptionType(), accessDenied);

		ExceptionInfo<?> abortWithErrorCode = new ExceptionInfo<AbortWithHttpErrorCodeException>() {

			private static final long serialVersionUID = -3440057330226741496L;

			@Override
			public Class<AbortWithHttpErrorCodeException> getExceptionType() {
				return AbortWithHttpErrorCodeException.class;
			}

			@Override
			public String getErrorTitle(AbortWithHttpErrorCodeException exception) {
				return "Error " + exception.getErrorCode();
			}

			@Override
			public String getErrorMessage(AbortWithHttpErrorCodeException exception) {
				return exception.getMessage();
			}

			@Override
			public int getHttpErrorCode(AbortWithHttpErrorCodeException exception) {
				return exception.getErrorCode();
			}
		};
		infos.put(abortWithErrorCode.getExceptionType(), abortWithErrorCode);

		// determine exception info and assign exception variable
		info = determineExceptionInfo(e);

		add(new Label("title", info.getErrorTitle(exception)));
		add(new Label("message", info.getErrorMessage(exception)));
	}

	private ExceptionInfo<?> determineExceptionInfo(Exception e) {
		// first, try map key
		ExceptionInfo<?> info = infos.get(e.getClass());
		if (info != null) {
			this.exception = e;
			return info;
		}

		// then, try in order (test compatibility)
		for (ExceptionInfo<?> candidate : infos.values()) {
			if (candidate.getExceptionType().isAssignableFrom(e.getClass())) {
				this.exception = e;
				return candidate;
			}
		}

		// then try cause
		Throwable cause = e.getCause();
		if (cause != null && cause != e && cause instanceof Exception) {
			return determineExceptionInfo((Exception) cause);
		}

		this.exception = e;
		return DEFAULT;
	}

	/**
	 * Get the HTTP status code for this error. Default is
	 * {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR}.
	 * 
	 * @param exception the exception
	 * @return the status code
	 */
	@SuppressWarnings("unchecked")
	protected int getStatus(Exception exception) {
		return info.getHttpErrorCode(exception);
	}

	/**
	 * @see org.apache.wicket.markup.html.WebPage#configureResponse(WebResponse)
	 */
	@Override
	protected void configureResponse(WebResponse webResponse) {
		super.configureResponse(webResponse);
		webResponse.setStatus(getStatus(exception));
	}

	/**
	 * @see org.apache.wicket.Page#isErrorPage()
	 */
	@Override
	public boolean isErrorPage() {
		return true;
	}

}
