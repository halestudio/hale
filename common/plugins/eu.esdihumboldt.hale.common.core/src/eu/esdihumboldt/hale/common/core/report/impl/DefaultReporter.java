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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.Reporter;

/**
 * Default report implementation
 * 
 * @param <T> the message type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class DefaultReporter<T extends Message> implements Reporter<T> {

	/**
	 * The logger
	 */
	public static final ALogger log = ALoggerFactory.getMaskingLogger(DefaultReporter.class, null);

	private boolean success = false;

	private final Class<T> messageType;

	private final List<T> errors = new ArrayList<T>();

	private final List<T> warnings = new ArrayList<T>();

	private final List<T> infos = new ArrayList<T>();

	private Date startTime;

	private Date timestamp;

	private final boolean doLog;

	private final String taskName;

	private String summary;

	/**
	 * Create an empty report. It is set to not successful by default. But you
	 * should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @param taskName the name of the task the report is related to
	 * @param messageType the message type
	 * @param doLog if added messages shall also be logged using {@link ALogger}
	 */
	public DefaultReporter(String taskName, Class<T> messageType, boolean doLog) {
		super();
		this.messageType = messageType;
		this.doLog = doLog;
		this.taskName = taskName;

		timestamp = new Date();
	}

	/**
	 * Adds a warning to the report. If configured accordingly a log message
	 * will also be created.
	 * 
	 * @param message the warning message
	 */
	@Override
	public void warn(T message) {
		warnings.add(message);

		if (doLog) {
			log.warn(message.getMessage(), message.getThrowable());
		}
	}

	/**
	 * Adds an error to the report. If configured accordingly a log message will
	 * also be created.
	 * 
	 * @param message the error message
	 */
	@Override
	public void error(T message) {
		errors.add(message);

		if (doLog) {
			log.error(message.getMessage(), message.getThrowable());
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.ReportLog#info(eu.esdihumboldt.hale.common.core.report.Message)
	 */
	@Override
	public void info(T message) {
		infos.add(message);

		if (doLog) {
			log.info(message.getMessage(), message.getThrowable());
		}
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
	 * @see Report#getErrors()
	 */
	@Override
	public Collection<T> getErrors() {
		return Collections.unmodifiableList(errors);
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
		if (errors.isEmpty()) {
			if (warnings.isEmpty()) {
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
	 * @see Report#getWarnings()
	 */
	@Override
	public Collection<T> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

	/**
	 * @see Report#getInfos()
	 */
	@Override
	public Collection<T> getInfos() {
		return Collections.unmodifiableList(infos);
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

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.Report#getDuration()
	 */
	@Override
	public long getDuration() {
		if (getStartTime() != null) {
			return (getTimestamp().getTime() - getStartTime().getTime());
		}
		return 0;
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
