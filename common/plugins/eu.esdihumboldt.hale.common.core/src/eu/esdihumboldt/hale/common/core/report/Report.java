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

import java.util.Collection;
import java.util.Date;

/**
 * Report interface
 * @param <T> the message type 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2 
 */
public interface Report<T extends Message> {
	
	/**
	 * Get the name of the task the report relates to
	 * 
	 * @return the task name
	 */
	public String getTaskName();
	
	/**
	 * Get the name of the task the report relates to
	 * 
	 * @param taskname the taskname
	 */
	public void setTaskName(String taskname);
	
	/**
	 * States if the report represents a successful task
	 * 
	 * @return if the task was successful
	 */
	public boolean isSuccess();
	
	/**
	 * Set if the task was successful. Should set the timestamps
	 * if none are provided so far.
	 * Should be called when the task is finished.
	 * 
	 * @param success if the task was successful
	 */
	public void setSuccess(boolean success);
	
	/**
	 * Get a short message stating a summary of the report
	 * 
	 * @return a short summary message
	 */
	public String getSummary();
	
	/**
	 * Set the summary message of the report.
	 * 
	 * @param summary the summary to set, if <code>null</code> the report will
	 * revert to the default summary.
	 */
	public void setSummary(String summary);
	
	/**
	 * Get the report time
	 * 
	 * @return the report time
	 */
	public Date getTimestamp();
	
	/**
	 * Set the report time
	 * 
	 * @param timestamp the timestamp
	 */
	public void setTimestamp(Date timestamp);
	
	/**
	 * Get the start time of the report.
	 * This is optional. If a start time is present the {@link #getTimestamp()}
	 * can be seen as an end time and used to compute a duration.
	 * 
	 * @return the start time or <code>null</code>
	 */
	public Date getStartTime();
	
	/**
	 * Set the start time of the report.
	 * This is optional. If a start time is present the {@link #getTimestamp()}
	 * can be seen as an end time and used to compute a duration.
	 * 
	 * @param starttime the starttime
	 */
	public void setStartTime(Date starttime);
	
	/**
	 * Get the warning messages
	 * 
	 * @return the warning messages
	 */
	public Collection<T> getWarnings();
	
	/**
	 * Get the error messages
	 * 
	 * @return the error messages
	 */
	public Collection<T> getErrors();
	
	/**
	 * Get the info messages
	 * 
	 * @return the info messages
	 */
	public Collection<T> getInfos();
	
	
	/**
	 * Get the message type (for determining the message type at runtime).
	 * 
	 * @return the message type
	 */
	public Class<T> getMessageType();

	/**
	 * Set the message type (for determining the message type at runtime).
	 * 
	 * @param messageType the messagetype
	 */
	public void setMessageType(Class<T> messageType);
}
