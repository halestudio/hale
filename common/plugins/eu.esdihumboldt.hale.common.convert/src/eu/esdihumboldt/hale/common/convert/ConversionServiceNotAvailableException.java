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
