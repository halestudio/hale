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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

/**
 * Exception that is thrown by a
 * {@link AbstractSingleTargetPropertyTransformation} if no result can be
 * determined for a transformation.
 * 
 * @author Simon Templer
 */
public class NoResultException extends Exception {

	private static final long serialVersionUID = -4936091404683206025L;

	/**
	 * @see Exception#Exception()
	 */
	public NoResultException() {
		super();
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public NoResultException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public NoResultException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public NoResultException(Throwable cause) {
		super(cause);
	}

}
