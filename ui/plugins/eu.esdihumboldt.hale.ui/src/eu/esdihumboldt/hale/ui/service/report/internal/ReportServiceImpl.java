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

package eu.esdihumboldt.hale.ui.service.report.internal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Multimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportSession;
import eu.esdihumboldt.hale.common.core.report.writer.ReportReader;
import eu.esdihumboldt.hale.common.core.report.writer.ReportWriter;
import eu.esdihumboldt.hale.ui.service.report.ReportListener;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * Report service implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class ReportServiceImpl implements ReportService {

	private final CopyOnWriteArraySet<ReportListener<?, ?>> listeners = new CopyOnWriteArraySet<ReportListener<?, ?>>();

	/**
	 * Map containing {@link ReportSession}s.
	 */
	private final Map<Long, ReportSession> reps = new HashMap<Long, ReportSession>();

	/**
	 * Contains the current session description.
	 */
	private long description = 0;

	private static final ALogger _log = ALoggerFactory.getLogger(ReportService.class);

	private ReportSession getCurrentSession() {
		// check if a current session exists
		if (this.getCurrentSessionDescription() == 0) {
			this.updateCurrentSessionDescription();
		}

		long time = this.description;

		ReportSession session = reps.get(time);
		if (session == null) {
			session = new ReportSession(time);
			reps.put(time, session);
		}

		return session;
	}

	/**
	 * @see ReportService#addReport(Report)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message, R extends Report<M>> void addReport(R report) {
		ReportSession session = this.getCurrentSession();
		session.addReport(report);

		// open ReportList
		openView();

		// notify listeners
		notifyReportAdded(report.getClass(), report.getMessageType(), report);
	}

	/**
	 * Notify listeners that a report has been added
	 * 
	 * @param <M> the message type
	 * @param <R> the report type
	 * 
	 * @param reportType the report type
	 * @param messageType the message type
	 * @param report the report
	 */
	@SuppressWarnings("unchecked")
	protected <M extends Message, R extends Report<M>> void notifyReportAdded(
			Class<? extends R> reportType, Class<M> messageType, R report) {
		for (ReportListener<?, ?> listener : listeners) {
			if (listener.getReportType().isAssignableFrom(reportType)
					&& listener.getMessageType().isAssignableFrom(messageType)) {
				((ReportListener<R, M>) listener).reportAdded(report);
			}
		}
	}

	/**
	 * Notify listeners that all reports have been deleted
	 */
	protected void notifyReportsDeleted() {
		for (ReportListener<?, ?> listener : listeners) {
			listener.reportsDeleted();
		}
	}

	/**
	 * Open the ReportList view.
	 */
	private void openView() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				try {
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
					if (window == null) {
						if (windows.length > 0) {
							window = windows[0];
						}
						else {
							/*
							 * we have no active window and no other window...
							 * so we better exit here
							 */
							_log.error("Could not open report view! No window available.");
							return;
						}
					}

					IWorkbenchPage page = window.getActivePage();
					page.showView("eu.esdihumboldt.hale.ui.views.report.ReportList");
				} catch (Exception e) {
					_log.error("Could not open report view!", e.getStackTrace());
				}
			}
		});
	}

	/**
	 * Get all reports matching the given message type
	 * 
	 * @param messageType the message type
	 * @return report types mapped to reports
	 */
	@Override
	public Multimap<Class<? extends Report<?>>, Report<?>> getReports(
			Class<? extends Message> messageType) {
		return this.getCurrentSession().getReports(messageType);
	}

	/**
	 * Get all reports.
	 * 
	 * @return all reports
	 */
	@Override
	public Multimap<Class<? extends Report<?>>, Report<?>> getCurrentReports() {
		return this.getCurrentSession().getAllReports();
	}

	/**
	 * @see ReportService#addReportListener(ReportListener)
	 */
	@Override
	public void addReportListener(ReportListener<?, ?> listener) {
		listeners.add(listener);
	}

	/**
	 * @see ReportService#removeReportListener(ReportListener)
	 */
	@Override
	public void removeReportListener(ReportListener<?, ?> listener) {
		listeners.remove(listener);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportService#deleteAllReports()
	 */
	@Override
	public void deleteAllReports() {
		// clear the list
		this.reps.clear();

		// clear storage folder
		File folder = new File(Platform.getLocation().toString() + "/reports/");
		if (folder.exists()) {
			for (File f : folder.listFiles()) {
				if (!f.delete()) {
					_log.error("Could not delete file: " + f.toString());
				}
			}

			if (!folder.delete()) {
				_log.error("Could not delete saved reports.");
			}
		}

		// notify listeners
		this.notifyReportsDeleted();
	}

	/**
	 * @see ReportService#saveCurrentReports(File)
	 */
	@Override
	public boolean saveCurrentReports(File file) throws IOException {
		return ReportWriter.write(file, this.getCurrentReports().values(), false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportService#loadReportsOnStartup()
	 */
	@Override
	public void loadReportsOnStartup() {
		// folder where the reports shall be stored
		File folder = new File(Platform.getLocation().toString() + "/reports/");

		// create a ReportReader
		ReportReader rr = new ReportReader();

		// read all sessions from log folder
		List<ReportSession> list = rr.readDirectory(folder);

		// add them to internal storage
		for (ReportSession s : list) {
			this.reps.put(s.getId(), s);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportService#saveReportsOnShutdown()
	 */
	@Override
	public void saveReportsOnShutdown() {
		// folder where the reports shall be stored
		File folder = new File(Platform.getLocation().toString() + "/reports/");

		if (!folder.exists() && !folder.mkdirs()) {
			// folder does not exist and we cannot create it...
			_log.error("Folder for reports does not exist and cannot be created!");
			return;
		}

		// iterate through all sessions
		for (ReportSession s : this.reps.values()) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
			File file = new File(folder.getPath() + "/" + df.format(new Date(s.getId())) + "-"
					+ s.getId() + ".log");
			try {
				ReportWriter.write(file, s.getAllReports().values(), false);
			} catch (IOException e) {
				// error during saving
				_log.error("Cannot save report session.", e.getStackTrace());
			}
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportService#getAllSessions()
	 */
	@Override
	public Collection<ReportSession> getAllSessions() {
		return this.reps.values();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportService#loadReport(java.io.File)
	 */
	@Override
	public void loadReport(File file) throws org.eclipse.jface.bindings.keys.ParseException {
		// create a ReportReader
		ReportReader rr = new ReportReader();

		// read all sessions from log folder
		ReportSession s = rr.readFile(file);

		if (s == null) {
			throw new org.eclipse.jface.bindings.keys.ParseException("Log could not be read.");
		}

		// add them to internal storage
		this.reps.put(s.getId(), s);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportService#getCurrentSessionDescription()
	 */
	@Override
	public long getCurrentSessionDescription() {
		return this.description;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportService#updateCurrentSessionDescription()
	 */
	@Override
	public void updateCurrentSessionDescription() {
		// only update if the time differs for 5000ms
		if (this.description + 5000 < System.currentTimeMillis()) {
			this.description = System.currentTimeMillis();
		}
	}
}
