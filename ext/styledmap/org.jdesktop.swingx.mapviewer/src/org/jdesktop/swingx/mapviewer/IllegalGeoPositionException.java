/*+-------------+----------------------------------------------------------*
 *|  |  |_|_|_|_|   Fraunhofer-Institut fuer Graphische Datenverarbeitung  *
 *|__|__|_|_|_|_|     (Fraunhofer Institute for Computer Graphics)         *
 *|  |  |_|_|_|_|                                                          *
 *|__|__|_|_|_|_|                                                          *
 *|  __ |    ___|                                                          *
 *| /_  /_  / _ |     Fraunhoferstrasse 5                                  *
 *|/   / / /__/ |     D-64283 Darmstadt, Germany                           *
 *+-------------+----------------------------------------------------------*/
package org.jdesktop.swingx.mapviewer;

/**
 * Exception that is thrown when a GeoPosition is not valid in the current
 * context
 *
 * @author Simon Templer
 */
public class IllegalGeoPositionException extends Exception {

	private static final long serialVersionUID = 5290670443854010904L;

	/**
	 * @see Exception#Exception()
	 */
	public IllegalGeoPositionException() {
		super();
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public IllegalGeoPositionException(String message, Throwable error) {
		super(message, error);
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public IllegalGeoPositionException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public IllegalGeoPositionException(Throwable error) {
		super(error);
	}

}
