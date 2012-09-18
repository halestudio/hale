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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.geometry;

/**
 * Exception that is thrown by {@link GeometryHandler}s when they are dealing
 * with geometry types or instances that they don't recognize.
 * 
 * @author Simon Templer
 */
public class GeometryNotSupportedException extends Exception {

	private static final long serialVersionUID = -3898627398397429119L;

	/**
	 * @see Exception#Exception()
	 */
	public GeometryNotSupportedException() {
		super();
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public GeometryNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public GeometryNotSupportedException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public GeometryNotSupportedException(Throwable cause) {
		super(cause);
	}

}
