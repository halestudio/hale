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

import java.util.Date;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.Reporter;

/**
 * Abstract report implementation
 * 
 * @param <T> the message type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.6
 */
public abstract class AbstractReporter<T extends Message> implements Reporter<T> {

	private boolean success = false;

	private final Class<T> messageType;

	private Date startTime;

	private Date timestamp;

	private final String taskName;

	private String summary;

	/**
	 * Create an empty report. It is set to not successful by default. But you
	 * should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @param taskName the name of the task the report is related to
	 * @param messageType the message type
	 */
	public AbstractReporter(String taskName, Class<T> messageType) {
		super();
		this.messageType = messageType;
		this.taskName = taskName;

		timestamp = new Date();
	}

	/**
	 * Set the summary message of the report.
	 * 
	 * @param summary the summary to set, if <code>null</code> the report will
	 *            revert to the default summary.
	 */
	@Override
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @see Report#getTaskName()
	 */
	@Override
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @see Report#getMessageType()
	 */
	@Override
	public Class<T> getMessageType() {
		return messageType;
	}

	/**
	 * @see Report#getSummary()
	 */
	@Override
	public String getSummary() {
		if (summary != null) {
			return summary;
		}

		if (success) {
			return getSuccessSummary();
		}
		else {
			return getFailSummary();
		}
	}

	/**
	 * Get the default report summary if it was not successful.
	 * 
	 * @return the report summary
	 */
	protected String getFailSummary() {
		return "Failed";
	}

	/**
	 * Get the default report summary if it was successful.
	 * 
	 * @return the report summary
	 */
	protected String getSuccessSummary() {
		if (!hasErrors()) {
			if (!hasWarnings()) {
				return "Finished successfully";
			}
			else {
				return "Finished successfully, but with warnings";
			}
		}
		else {
			return "Completed, but with errors";
		}
	}

	/**
	 * @see Report#getTimestamp()
	 */
	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.Reporter#setSuccess(boolean)
	 */
	@Override
	public void setSuccess(boolean success) {
		this.success = success;

		if (startTime == null) {
			startTime = timestamp;
		}
		timestamp = new Date();
	}

	/**
	 * @see Report#isSuccess()
	 */
	@Override
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @see Report#getStartTime()
	 */
	@Override
	public Date getStartTime() {
		return startTime;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NL = System.getProperty("line.separator");

		result.append("taskname = " + this.getTaskName() + NL);
		result.append("success = " + this.isSuccess() + NL);
		result.append("summary = " + this.getSummary() + NL);

		return result.toString();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.Reporter#setTimestamp(java.util.Date)
	 */
	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.Reporter#setStartTime(java.util.Date)
	 */
	@Override
	public void setStartTime(Date starttime) {
		this.startTime = starttime;
	}
}
