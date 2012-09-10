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

package eu.esdihumboldt.hale.common.convert;

import org.springframework.core.convert.ConversionException;

/**
 * Exception that should be thrown when the conversion service is not available.
 * 
 * @author Simon Templer
 */
public class ConversionServiceNotAvailableException extends ConversionException {

	private static final long serialVersionUID = -6073334239505337796L;

	/**
	 * Constructor using a default message.
	 */
	public ConversionServiceNotAvailableException() {
		this("Conversion service not available");
	}

	/**
	 * @see ConversionException#ConversionException(String, Throwable)
	 */
	public ConversionServiceNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see ConversionException#ConversionException(String)
	 */
	public ConversionServiceNotAvailableException(String message) {
		super(message);
	}

}
