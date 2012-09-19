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

package eu.esdihumboldt.util.resource;

/**
 * Exception that is thrown when a resource was not found.
 * 
 * @author Simon Templer
 */
public class ResourceNotFoundException extends Exception {

	private static final long serialVersionUID = 5339008148676744870L;

	/**
	 * @see Exception#Exception()
	 */
	public ResourceNotFoundException() {
		super();
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public ResourceNotFoundException(String message, Throwable e) {
		super(message, e);
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public ResourceNotFoundException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public ResourceNotFoundException(Throwable e) {
		super(e);
	}

}
