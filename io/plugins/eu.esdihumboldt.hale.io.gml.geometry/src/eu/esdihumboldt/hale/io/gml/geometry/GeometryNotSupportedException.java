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
