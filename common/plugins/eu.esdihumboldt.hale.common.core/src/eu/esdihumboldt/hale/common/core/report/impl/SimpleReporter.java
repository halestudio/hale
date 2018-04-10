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

package eu.esdihumboldt.hale.common.core.report.impl;

import de.fhg.igd.slf4jplus.ALogger;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Simple reporter using the default message class.
 * 
 * @author Simon Templer
 */
public class SimpleReporter extends DefaultReporter<Message> implements SimpleLog {

	/**
	 * Create an empty report. It is set to not successful by default. But you
	 * should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @param taskName the name of the task the report is related to
	 * @param taskType the identifier of the task type
	 * @param doLog if added messages shall also be logged using {@link ALogger}
	 */
	public SimpleReporter(String taskName, String taskType, boolean doLog) {
		super(taskName, taskType, Message.class, doLog);
	}

	@Override
	public void warn(String message, Throwable e) {
		warn(new MessageImpl(message, e));
	}

	@Override
	public void error(String message, Throwable e) {
		error(new MessageImpl(message, e));
	}

	@Override
	public void info(String message, Throwable e) {
		info(new MessageImpl(message, e));
	}

}
