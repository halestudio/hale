/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.core.report;

import java.text.MessageFormat;

/**
 * Interface for providing a simple logging interface.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public interface SimpleLog {

	void warn(String message, Throwable e);

	default void warn(String message) {
		warn(message, (Throwable) null);
	}

	/**
	 * Log a warning. The given pattern is formatted with {@link MessageFormat}.
	 * 
	 * @param pattern the message pattern
	 * @param args the pattern arguments
	 */
	default void warn(String pattern, Object... args) {
		warn(MessageFormat.format(pattern, args));
	}

	void error(String message, Throwable e);

	default void error(String message) {
		error(message, (Throwable) null);
	}

	/**
	 * Log an error. The given pattern is formatted with {@link MessageFormat}.
	 * 
	 * @param pattern the message pattern
	 * @param args the pattern arguments
	 */
	default void error(String pattern, Object... args) {
		error(MessageFormat.format(pattern, args));
	}

	void info(String message, Throwable e);

	default void info(String message) {
		info(message, (Throwable) null);
	}

	/**
	 * Log an info message. The given pattern is formatted with
	 * {@link MessageFormat}.
	 * 
	 * @param pattern the message pattern
	 * @param args the pattern arguments
	 */
	default void info(String pattern, Object... args) {
		info(MessageFormat.format(pattern, args));
	}

}
