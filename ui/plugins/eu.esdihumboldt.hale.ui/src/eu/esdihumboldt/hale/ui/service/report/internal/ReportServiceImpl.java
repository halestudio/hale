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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Multimap;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
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
	
	private ReportSession getCurrentSession() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String yyyymmdd = df.format(new Date(System.currentTimeMillis()));
		long time;
		try {
			time = df.parse(yyyymmdd).getTime();
		} catch (ParseException e) {
			return null;
		}
		
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
}
