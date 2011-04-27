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

package eu.esdihumboldt.hale.core.report.impl;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.core.report.Message;

/**
 * Default message implementation 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
@Immutable
public class MessageImpl implements Message {

	private final String message;
	
	private final Throwable throwable;

	/**
	 * Create a new message
	 * 
	 * @param message the message string
	 * @param throwable the associated throwable, may be <code>null</code>
	 */
	public MessageImpl(String message, Throwable throwable) {
		super();
		this.message = message;
		this.throwable = throwable;
	}

	/**
	 * @see Message#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * @see Message#getThrowable()
	 */
	@Override
	public Throwable getThrowable() {
		return throwable;
	}

}
