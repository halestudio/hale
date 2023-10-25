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

import org.slf4j.Logger;

/**
 * Interface for providing a simple logging interface.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public interface SimpleLog {

	public static final SimpleLog NO_LOG = new SimpleLog() {

		@Override
		public void warn(String message, Throwable e) {
			// do nothing
		}

		@Override
		public void error(String message, Throwable e) {
			// do nothing
		}

		@Override
		public void info(String message, Throwable e) {
			// do nothing
		}

	};

	public static final SimpleLog CONSOLE_LOG = new SimpleLog() {

		@Override
		public void warn(String message, Throwable e) {
			System.out.println("WARN " + message);
			if (e != null) {
				e.printStackTrace(System.out);
			}
		}

		@Override
		public void error(String message, Throwable e) {
			System.err.println("ERROR " + message);
			if (e != null) {
				e.printStackTrace(System.err);
			}
		}

		@Override
		public void info(String message, Throwable e) {
			System.out.println("INFO " + message);
			if (e != null) {
				e.printStackTrace(System.out);
			}
		}

	};

	public static SimpleLog fromLogger(final Logger logger) {
		return new SimpleLog() {

			@Override
			public void warn(String message, Throwable e) {
				logger.warn(message, e);
			}

			@Override
			public void info(String message, Throwable e) {
				logger.info(message, e);
			}

			@Override
			public void error(String message, Throwable e) {
				logger.error(message, e);
			}
		};
	}

	public static SimpleLog all(final SimpleLog... logs) {
		return new SimpleLog() {

			@Override
			public void warn(String message, Throwable e) {
				for (SimpleLog log : logs) {
					log.warn(message, e);
				}
			}

			@Override
			public void info(String message, Throwable e) {
				for (SimpleLog log : logs) {
					log.info(message, e);
				}
			}

			@Override
			public void error(String message, Throwable e) {
				for (SimpleLog log : logs) {
					log.error(message, e);
				}
			}
		};
	}

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
		String message = MessageFormat.format(pattern, args);
		Throwable error = null;
		if (args != null && args[args.length - 1] instanceof Throwable) {
			error = (Throwable) args[args.length - 1];
		}
		warn(message, error);
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
		String message = MessageFormat.format(pattern, args);
		Throwable error = null;
		if (args != null && args[args.length - 1] instanceof Throwable) {
			error = (Throwable) args[args.length - 1];
		}
		error(message, error);
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
		String message = MessageFormat.format(pattern, args);
		Throwable error = null;
		if (args != null && args[args.length - 1] instanceof Throwable) {
			error = (Throwable) args[args.length - 1];
		}
		info(message, error);
	}

}
