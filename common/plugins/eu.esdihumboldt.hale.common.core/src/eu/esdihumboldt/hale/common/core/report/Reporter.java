/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
public interface Reporter<T extends Message> extends Report<T>, ReportLog<T> {

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
}
