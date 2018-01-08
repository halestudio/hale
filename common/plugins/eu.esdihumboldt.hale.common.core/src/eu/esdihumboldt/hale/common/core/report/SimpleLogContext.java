/*
 * Copyright (c) 2018 wetransform GmbH
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

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

/**
 * Thread context for {@link SimpleLog}. Allows associating a log to a thread.
 * 
 * @author Simon Templer
 */
public class SimpleLogContext {

	private static ThreadLocal<Deque<SimpleLog>> currentLogs = new ThreadLocal<Deque<SimpleLog>>() {

		@Override
		protected Deque<SimpleLog> initialValue() {
			return new LinkedList<SimpleLog>();
		}

	};

	/**
	 * Get the log associated to the current thread. If there is none, a logger
	 * is returned that does not log any messages.
	 * 
	 * @return the log associated to the current thread or
	 *         {@link SimpleLog#NO_LOG}
	 */
	public static SimpleLog getLog() {
		return getLog(SimpleLog.NO_LOG);
	}

	/**
	 * Get the log associated to the current thread. If there is none, returns
	 * the provided default logger.
	 * 
	 * @param def the default logger
	 * @return the log associated to the current thread or the default log
	 */
	public static SimpleLog getLog(SimpleLog def) {
		SimpleLog result = currentLogs.get().peek();
		if (result == null) {
			result = def;
		}
		return result;
	}

	/**
	 * Get the log associated to the current thread. If there is none, returns
	 * the provided default logger.
	 * 
	 * @param def the default logger
	 * @return the log associated to the current thread or the default log
	 */
	public static SimpleLog getLog(Supplier<SimpleLog> def) {
		SimpleLog result = currentLogs.get().peek();
		if (result == null) {
			result = def.get();
		}
		return result;
	}

	/**
	 * Associate the given log with the current thread while the given function
	 * is called.
	 * 
	 * @param log the log to associated to the thread
	 * @param fun the function to execute
	 * @return the function result
	 */
	public static <X> X withLog(final SimpleLog log, Supplier<X> fun) {
		if (log != null) {
			currentLogs.get().push(log);
		}
		try {
			return fun.get();
		} finally {
			if (log != null) {
				currentLogs.get().pop();
			}
		}
	}

	/**
	 * Associate the given log with the current thread while the given function
	 * is called.
	 * 
	 * @param log the log to associated to the thread
	 * @param fun the function to execute
	 */
	public static void withLog(final SimpleLog log, Runnable fun) {
		if (log != null) {
			currentLogs.get().push(log);
		}
		try {
			fun.run();
		} finally {
			if (log != null) {
				currentLogs.get().pop();
			}
		}
	}

}
