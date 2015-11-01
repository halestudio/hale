/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.geoserver.rest;

/**
 * Exception thrown by resource managers when an operation on the managed
 * resource fails.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class ResourceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3438214651953101857L;

	/**
	 * @see Exception#Exception(String)
	 */
	public ResourceException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public ResourceException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public ResourceException(String message, Throwable cause) {
		super(message, cause);
	}

}
