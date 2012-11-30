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
