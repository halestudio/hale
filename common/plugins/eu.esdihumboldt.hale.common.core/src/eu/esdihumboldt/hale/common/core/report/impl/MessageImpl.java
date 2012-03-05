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

package eu.esdihumboldt.hale.common.core.report.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.core.report.Message;

/**
 * Default message implementation 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
@Immutable
public class MessageImpl implements Message {
	
	/**
	 * Get the stack trace from a given throwable
	 * @param throwable the throwable, may be <code>null</code> 
	 * @return the stack trace or <code>null</code>
	 */
	private static String getStackTrace(Throwable throwable) {
		if (throwable == null) {
			return null;
		}
		
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		try {
			throwable.printStackTrace(printWriter);
		} finally {
			printWriter.close();
		}
		
		return writer.toString();
	}

	private final String message;
	
	private final String stackTrace;
	
	private final Throwable throwable;

	/**
	 * Create a new message
	 * 
	 * @param message the message string
	 * @param throwable the associated throwable, may be <code>null</code>
	 */
	public MessageImpl(String message, Throwable throwable) {
		this(message, throwable, getStackTrace(throwable));
	}
	
	/**
	 * Create a new message
	 * 
	 * @param message the message string
	 * @param throwable the associated throwable, may be <code>null</code>
	 * @param stackTrace the associated stack trace, or <code>null</code>
	 */
	protected MessageImpl(String message, Throwable throwable, String stackTrace) {
		super();
		this.message = message;
		this.stackTrace = stackTrace;
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
	 * @see Message#getStackTrace()
	 */
	@Override
	public String getStackTrace() {
		return stackTrace;
	}

	/**
	 * @see Message#getThrowable()
	 */
	@Override
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.Message#getFormattedMessage()
	 */
	@Override
	public String getFormattedMessage() {
		return this.getMessage();
	}

}
