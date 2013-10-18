/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.util.blueprints.entities;

/**
 * Exception that is thrown when a unique result is requested but multiple
 * results are found.
 * 
 * @author Simon Templer
 */
public class NonUniqueResultException extends Exception {

	private static final long serialVersionUID = 8732874452859325377L;

	/**
	 * @see Exception#Exception()
	 */
	public NonUniqueResultException() {
		super();
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public NonUniqueResultException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public NonUniqueResultException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public NonUniqueResultException(Throwable cause) {
		super(cause);
	}

}
