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

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

/**
 * Represents information on how to handle a specific exception type.
 * 
 * @author Simon Templer
 * @param <T> the exception type for which this info applies
 */
public interface ExceptionInfo<T extends Exception> extends Serializable {

	/**
	 * Get the exception type for which this info applies.
	 * 
	 * @return the exception type
	 */
	public Class<T> getExceptionType();

	/**
	 * Get the error title to display.
	 * 
	 * @param exception the exception representing the error
	 * @return the error title, may not be <code>null</code>
	 */
	public String getErrorTitle(T exception);

	/**
	 * Get the error message to display.
	 * 
	 * @param exception the exception representing the error
	 * @return the error message, may not be <code>null</code>
	 */
	public String getErrorMessage(T exception);

	/**
	 * Get the HTTP status code for this error, e.g.
	 * {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR}.
	 * 
	 * @param exception the exception representing the error
	 * 
	 * @return the HTTP status code
	 */
	public int getHttpErrorCode(T exception);

}
