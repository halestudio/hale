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

import javax.annotation.Nullable;

/**
 * Interface that adds {@link SimpleLog} support to a {@link ReportLog}.
 * 
 * @author Simon Templer
 * @param <T> the message type
 */
public interface ReportSimpleLogSupport<T extends Message> extends ReportLog<T>, SimpleLog {

	/**
	 * Create a message w/ only message and throwable.
	 * 
	 * @param message the message
	 * @param e the throwable, may be <code>null</code>
	 * @return the created message
	 */
	T createMessage(String message, @Nullable Throwable e);

	@Override
	default void warn(String message, Throwable e) {
		warn(createMessage(message, e));
	}

	@Override
	default void error(String message, Throwable e) {
		error(createMessage(message, e));
	}

	@Override
	default void info(String message, Throwable e) {
		info(createMessage(message, e));
	}

}
