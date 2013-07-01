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
	 * 
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
		this(message, throwable, null);
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
		if (stackTrace == null && throwable != null) {
			return getStackTrace(throwable);
		}

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
