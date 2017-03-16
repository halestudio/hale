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

/**
 * Exception class for HaleConnectService
 * 
 * @author Florian Esser
 */
public class HaleConnectException extends Exception {

	private static final long serialVersionUID = -5273340965866912596L;

	/**
	 * @see Exception#Exception()
	 */
	public HaleConnectException() {
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public HaleConnectException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public HaleConnectException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public HaleConnectException(String message, Throwable cause) {
		super(message, cause);
	}

}
