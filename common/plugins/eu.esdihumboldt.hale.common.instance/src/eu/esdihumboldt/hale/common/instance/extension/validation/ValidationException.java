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

package eu.esdihumboldt.hale.common.instance.extension.validation;

/**
 * Exception for validatable constraints.
 * 
 * @author Kai Schwierczek
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = 5262806141462302316L;

	/**
	 * @see Exception#Exception()
	 */
	public ValidationException() {
		super();
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public ValidationException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public ValidationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String,Throwable)
	 */
	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
