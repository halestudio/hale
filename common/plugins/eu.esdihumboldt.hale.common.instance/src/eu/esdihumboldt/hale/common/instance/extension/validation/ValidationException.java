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
