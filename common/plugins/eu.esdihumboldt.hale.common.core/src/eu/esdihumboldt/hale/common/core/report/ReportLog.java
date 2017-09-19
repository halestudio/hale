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

/**
 * Report log interface for contributing to a report
 * 
 * @param <T> the message type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface ReportLog<T extends Message> {

	/**
	 * Adds a warning to the report. If configured accordingly a log message
	 * will also be created.
	 * 
	 * @param message the warning message
	 */
	public void warn(T message);

	/**
	 * Adds an error to the report. If configured accordingly a log message will
	 * also be created.
	 * 
	 * @param message the error message
	 */
	public void error(T message);

	/**
	 * Adds an info to the report. If configured accordingly a log message will
	 * also be created.
	 * 
	 * @param message the info message
	 */
	public void info(T message);

	/**
	 * Add all messages of the given report to this report.
	 * 
	 * @param report the report to add the messages from
	 */
	public void importMessages(Report<? extends T> report);

}
