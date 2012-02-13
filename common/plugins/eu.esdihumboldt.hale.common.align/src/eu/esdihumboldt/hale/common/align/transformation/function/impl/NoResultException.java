/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

/**
 * Exception that is thrown by a {@link AbstractSingleTargetPropertyTransformation}
 * if no result can be determined for a transformation. 
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
