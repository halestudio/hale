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

package eu.esdihumboldt.hale.ui.service.report.internal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

import com.google.common.collect.Multimap;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
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
	
	private final TypeSafeListenerList<ReportListener<?, ?>> listeners = new TypeSafeListenerList<ReportListener<?,?>>();

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
	protected <M extends Message, R extends Report<M>> void notifyReportAdded(Class<? extends R> reportType,
			Class<M> messageType, R report) {
		for (ReportListener<?, ?> listener : listeners) {
			if (listener.getReportType().isAssignableFrom(reportType) &&
					listener.getMessageType().isAssignableFrom(messageType)) {
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
		File folder = new File(Platform.getLocation().toString()+"/reports/");
		if (folder.exists()) {
			for (File f : folder.listFiles()) {
				if (!f.delete()) {
					_log.error("Could not delete file: "+f.toString());
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
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportService#saveCurrentReports(java.io.File)
	 */
	@Override
	public boolean saveCurrentReports(File file) throws IOException {
		ReportWriter rw = new ReportWriter();
		
		return rw.writeAll(file, this.getCurrentReports());
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportService#loadReportsOnStartup()
	 */
	@Override
	public void loadReportsOnStartup() {
		// folder where the reports shall be stored
		File folder = new File(Platform.getLocation().toString()+"/reports/");
		
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
		File folder = new File(Platform.getLocation().toString()+"/reports/");
		
		if (!folder.exists() && !folder.mkdirs()) {
			// folder does not exist and we cannot create it...
			_log.error("Folder for reports does not exist and cannot be created!");
			return;
		}
		
		// create a ReportWriter
		ReportWriter rw = new ReportWriter();

		// iterate through all sessions
		for (ReportSession s : this.reps.values()) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
			File file = new File(folder.getPath()+"/"+df.format(new Date(s.getId()))+"-"+s.getId()+".log");
			try {
				rw.writeAll(file, s.getAllReports());
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
		this.description = System.currentTimeMillis();
	}
}
