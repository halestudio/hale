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
 * 
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
	 * States if the report represents a successful task
	 * 
	 * @return if the task was successful
	 */
	public boolean isSuccess();

	/**
	 * Get a short message stating a summary of the report
	 * 
	 * @return a short summary message
	 */
	public String getSummary();

	/**
	 * Get the report time
	 * 
	 * @return the report time
	 */
	public Date getTimestamp();

	/**
	 * Get the start time of the report. This is optional. If a start time is
	 * present the {@link #getTimestamp()} can be seen as an end time and used
	 * to compute a duration.
	 * 
	 * @return the start time or <code>null</code>
	 */
	public Date getStartTime();

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
}
