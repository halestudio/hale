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

package eu.esdihumboldt.hale.common.core.report;

import java.util.Date;

/**
 * Reporter interface
 * 
 * @param <T> the message type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public interface Reporter<T extends Message> extends Report<T>, ReportLog<T>, Statistics {

	/**
	 * Set if the task was successful. Should set the timestamps if none are
	 * provided so far. Should be called when the task is finished.
	 * 
	 * @param success if the task was successful
	 */
	public void setSuccess(boolean success);

	/**
	 * Set the summary message of the report.
	 * 
	 * @param summary the summary to set, if <code>null</code> the report will
	 *            revert to the default summary.
	 */
	public void setSummary(String summary);

	/**
	 * Set the report time
	 * 
	 * @param timestamp the timestamp
	 */
	public void setTimestamp(Date timestamp);

	/**
	 * Set the start time of the report. This is optional. If a start time is
	 * present the {@link #getTimestamp()} can be seen as an end time and used
	 * to compute a duration.
	 * 
	 * @param starttime the starttime
	 */
	public void setStartTime(Date starttime);

	// add counts of messages - these methods are mainly intended for restoring
	// report information and not for actual reporting

	/**
	 * Add a number of errors w/o message, e.g. if no message is available.
	 * 
	 * @param number the number of errors
	 */
	void countError(int number);

	/**
	 * Add an error w/o a message, e.g. if no message is available or the reason
	 * is not known.
	 */
	default void countError() {
		countError(1);
	}

	/**
	 * Add a number of warnings w/o message, e.g. if no message is available.
	 * 
	 * @param number the number of warnings
	 */
	void countWarning(int number);

	/**
	 * Add a warning w/o a message, e.g. if no message is available or the
	 * reason is not known.
	 */
	default void countWarning() {
		countError(1);
	}

	/**
	 * Add a number of info messages w/o message, e.g. if no message is
	 * available.
	 * 
	 * @param number the number of info messages
	 */
	void countInfo(int number);

	/**
	 * Add an info message w/o a message, e.g. if the message is not available.
	 */
	default void countInfo() {
		countError(1);
	}

}
