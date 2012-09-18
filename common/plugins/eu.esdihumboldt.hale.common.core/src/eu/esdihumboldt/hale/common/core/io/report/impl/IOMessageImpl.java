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

package eu.esdihumboldt.hale.common.core.io.report.impl;

import java.text.MessageFormat;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.core.io.report.IOMessage;
import eu.esdihumboldt.hale.common.core.report.impl.MessageImpl;

/**
 * Default {@link IOMessage} implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
@Immutable
public class IOMessageImpl extends MessageImpl implements IOMessage {

	private final int column;

	private final int lineNumber;

	/**
	 * Create a new message
	 * 
	 * @param message the message string
	 * @param throwable the associated throwable, may be <code>null</code>
	 */
	public IOMessageImpl(String message, Throwable throwable) {
		this(message, throwable, -1, -1);
	}

	/**
	 * Create a new message
	 * 
	 * @param message the message string
	 * @param throwable the associated throwable, may be <code>null</code>
	 * @param lineNumber the line number in the file, <code>-1</code> for none
	 * @param column the column in the line, <code>-1</code> for none
	 */
	public IOMessageImpl(String message, Throwable throwable, int lineNumber, int column) {
		super(message, throwable);
		this.column = column;
		this.lineNumber = lineNumber;
	}

	/**
	 * Create a new message
	 * 
	 * @param message the message string
	 * @param throwable the associated throwable, may be <code>null</code>
	 * @param stackTrace the associated stack trace, or <code>null</code>
	 * @param lineNumber the line number in the file, <code>-1</code> for none
	 * @param column the column in the line, <code>-1</code> for none
	 */
	protected IOMessageImpl(String message, Throwable throwable, String stackTrace, int lineNumber,
			int column) {
		super(message, throwable, stackTrace);
		this.column = column;
		this.lineNumber = lineNumber;
	}

	/**
	 * Create a new message and format it using {@link MessageFormat}
	 * 
	 * @param pattern the message format pattern
	 * @param throwable the associated throwable, may be <code>null</code>
	 * @param lineNumber the line number in the file, <code>-1</code> for none
	 * @param column the column in the line, <code>-1</code> for none
	 * @param arguments the arguments for the message format
	 */
	public IOMessageImpl(String pattern, Throwable throwable, int lineNumber, int column,
			Object... arguments) {
		super(MessageFormat.format(pattern, arguments), throwable);
		this.column = column;
		this.lineNumber = lineNumber;
	}

	/**
	 * @see IOMessage#getColumn()
	 */
	@Override
	public int getColumn() {
		return column;
	}

	/**
	 * @see IOMessage#getLineNumber()
	 */
	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public String getFormattedMessage() {
		if (getLineNumber() <= 0) {
			return this.getMessage();
		}
		else {
			return String.format("%s, on line %d, column %d", getMessage(), getLineNumber(),
					getColumn());
		}
	}
}
