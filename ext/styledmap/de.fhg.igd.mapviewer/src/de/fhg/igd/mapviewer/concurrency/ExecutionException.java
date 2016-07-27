/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.concurrency;

/**
 * Exception that is thrown when a {@link Job} execution fails, the cause is the
 * exception that occurred while executing the job.
 * 
 * @author Simon Templer
 */
public class ExecutionException extends Exception {

	private static final long serialVersionUID = -2763066424592691156L;

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public ExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public ExecutionException(Throwable cause) {
		super(cause);
	}

}
