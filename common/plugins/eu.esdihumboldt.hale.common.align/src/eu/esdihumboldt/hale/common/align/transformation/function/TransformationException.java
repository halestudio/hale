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

package eu.esdihumboldt.hale.common.align.transformation.function;

/**
 * Exception on transformation execution.
 * 
 * @author Simon Templer
 */
public class TransformationException extends Exception {

	private static final long serialVersionUID = -4257242606733273937L;

	/**
	 * @see Exception#Exception()
	 */
	public TransformationException() {
		super();
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public TransformationException(String message, Throwable e) {
		super(message, e);
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public TransformationException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public TransformationException(Throwable e) {
		super(e);
	}

}
