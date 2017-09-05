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

package eu.esdihumboldt.hale.common.align.transformation.report;

import eu.esdihumboldt.hale.common.core.report.ReportLog;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Report log for transformation functions. Messages for the report should be
 * created using {@link #createMessage(String, Throwable)}
 * 
 * @author Simon Templer
 * @since 2.5
 */
public interface TransformationLog extends ReportLog<TransformationMessage>, SimpleLog {

	/**
	 * Create a message configured with the current cell for use with the log
	 * 
	 * @param message the message string
	 * @param throwable a throwable associated to the message, may be
	 *            <code>null</code>
	 * @return the message
	 */
	TransformationMessage createMessage(String message, Throwable throwable);

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
