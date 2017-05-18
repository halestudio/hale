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

package eu.esdihumboldt.hale.common.core.io.report;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.report.Reporter;

/**
 * Reporter for I/O providers
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface IOReporter extends Reporter<IOMessage>, IOReport {

	// convenience methods

	// error

	/**
	 * Log an error.
	 * 
	 * @param message the error message
	 * @param throwable a throwable describing the error
	 * @param lineNumber the line number, <code>-1</code> for none
	 * @param column the column, <code>-1</code> for none
	 */
	default void error(String message, @Nullable Throwable throwable, int lineNumber, int column) {
		error(new IOMessageImpl(message, throwable, lineNumber, column));
	}

	/**
	 * Log an error.
	 * 
	 * @param message the error message
	 * @param throwable a throwable describing the error
	 */
	default void error(String message, @Nullable Throwable throwable) {
		error(message, throwable, -1, -1);
	}

	/**
	 * Log an error.
	 * 
	 * @param message the error message
	 */
	default void error(String message) {
		error(message, null, -1, -1);
	}

	// warn

	/**
	 * Log a warning.
	 * 
	 * @param message the warning message
	 * @param throwable a throwable describing the warning
	 * @param lineNumber the line number, <code>-1</code> for none
	 * @param column the column, <code>-1</code> for none
	 */
	default void warn(String message, @Nullable Throwable throwable, int lineNumber, int column) {
		warn(new IOMessageImpl(message, throwable, lineNumber, column));
	}

	/**
	 * Log a warning.
	 * 
	 * @param message the warning message
	 * @param throwable a throwable describing the warning
	 */
	default void warn(String message, @Nullable Throwable throwable) {
		warn(message, throwable, -1, -1);
	}

	/**
	 * Log a warning.
	 * 
	 * @param message the warning message
	 */
	default void warn(String message) {
		warn(message, null, -1, -1);
	}

	// info

	/**
	 * Log an info message.
	 * 
	 * @param message the info message
	 * @param throwable a throwable
	 * @param lineNumber the line number, <code>-1</code> for none
	 * @param column the column, <code>-1</code> for none
	 */
	default void info(String message, @Nullable Throwable throwable, int lineNumber, int column) {
		info(new IOMessageImpl(message, throwable, lineNumber, column));
	}

	/**
	 * Log an info message.
	 * 
	 * @param message the info message
	 * @param throwable a throwable
	 */
	default void info(String message, @Nullable Throwable throwable) {
		info(message, throwable, -1, -1);
	}

	/**
	 * Log an info message.
	 * 
	 * @param message the info message
	 */
	default void info(String message) {
		info(message, null, -1, -1);
	}

}
