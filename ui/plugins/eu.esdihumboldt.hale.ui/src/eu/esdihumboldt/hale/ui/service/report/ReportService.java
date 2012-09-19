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

package eu.esdihumboldt.hale.ui.service.report;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportSession;

/**
 * Report service interface
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface ReportService {

	/**
	 * Adds a report
	 * 
	 * @param <M> the message type
	 * @param <R> the report type
	 * 
	 * @param report the report to add
	 */
	public <M extends Message, R extends Report<M>> void addReport(R report);

	/**
	 * Adds a report listener
	 * 
	 * @param listener the report listener to add
	 */
	public void addReportListener(ReportListener<?, ?> listener);

	/**
	 * Removes a report listener
	 * 
	 * @param listener the report listener to remove
	 */
	public void removeReportListener(ReportListener<?, ?> listener);

	/**
	 * Get all reports matching the given message type
	 * 
	 * @param messageType the message type
	 * @return report types mapped to reports
	 */
	public Multimap<Class<? extends Report<?>>, Report<?>> getReports(
			Class<? extends Message> messageType);

	/**
	 * Get all current reports.
	 * 
	 * @return all reports
	 */
	public Multimap<Class<? extends Report<?>>, Report<?>> getCurrentReports();

	/**
	 * Deletes all reports.
	 */
	public void deleteAllReports();

	/**
	 * Saves all reports from {@link ReportService} to a specific file.
	 * 
	 * @param file the file to save
	 * 
	 * @return true on success
	 * 
	 * @throws IOException an exception if IO fails
	 */
	public boolean saveCurrentReports(File file) throws IOException;

	/**
	 * Get all saved sessions.
	 * 
	 * @return all sessions
	 */
	public Collection<ReportSession> getAllSessions();

	/**
	 * Try to reload previous saved reports and their session at program
	 * startup.
	 */
	public void loadReportsOnStartup();

	/**
	 * Saves all reports with there corresponding session on program shutdown.
	 */
	public void saveReportsOnShutdown();

	/**
	 * Load a specific report log file.
	 * 
	 * @param file report log file
	 * 
	 * @throws org.eclipse.jface.bindings.keys.ParseException if a parse error
	 *             occurred
	 */
	public void loadReport(File file) throws org.eclipse.jface.bindings.keys.ParseException;

	/**
	 * Get the current session description.
	 * 
	 * @return session description
	 */
	public long getCurrentSessionDescription();

	/**
	 * Update the session description.
	 */
	public void updateCurrentSessionDescription();
}
