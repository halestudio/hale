/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exception class for HaleConnectService
 * 
 * @author Florian Esser
 */
public class HaleConnectException extends Exception {

	private static final long serialVersionUID = -5273340965866912596L;

	private final int statusCode;

	private final Map<String, List<String>> responseHeaders = new HashMap<>();

	/**
	 * @see Exception#Exception()
	 */
	public HaleConnectException() {
		statusCode = -1;
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public HaleConnectException(String message) {
		super(message);
		statusCode = -1;
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public HaleConnectException(Throwable cause) {
		super(cause);
		statusCode = -1;
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public HaleConnectException(String message, Throwable cause) {
		super(message, cause);
		statusCode = -1;
	}

	/**
	 * Creates a HaleConnectException
	 * 
	 * @param message the detail message (which is saved for later retrieval by
	 *            the {@link #getMessage()} method).
	 * @param cause the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <code>null</code> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @param statusCode The status code returned by the hale connect service
	 * @param responseHeaders The response headers of the service response (may
	 *            be <code>null</code>)
	 */
	public HaleConnectException(String message, Throwable cause, int statusCode,
			Map<String, List<String>> responseHeaders) {
		super(message, cause);
		this.statusCode = statusCode;
		if (responseHeaders != null) {
			this.responseHeaders.putAll(responseHeaders);
		}
	}

	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @return the responseHeaders
	 */
	public Map<String, List<String>> getResponseHeaders() {
		return Collections.unmodifiableMap(responseHeaders);
	}

}
