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

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
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
	 * Map using the MessageType(Class) as index and a Map containing Report(Class) -> Report
	 */
	private final Map<Class<? extends Message>, Multimap<Class<? extends Report<?>>, Report<?>>> reports = new HashMap<Class<? extends Message>, Multimap<Class<? extends Report<?>>,Report<?>>>();
	
	/**
	 * @see ReportService#addReport(Report)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message, R extends Report<M>> void addReport(R report) {
		// get all reports for this messageType
		Multimap<Class<? extends Report<?>>, Report<?>> reportMap = getReports(report.getMessageType());
		
		// add the report to temporary map
		reportMap.put((Class<? extends Report<?>>) report.getClass(), report);
		
		// add them to internal storage
		this.reports.put(report.getMessageType(), reportMap);
		
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
		Multimap<Class<? extends Report<?>>, Report<?>> map = reports.get(messageType);
		if (map == null) {
			map = HashMultimap.create();
		}
		return map;
	}
	
	/**
	 * Get all reports.
	 * 
	 * @return all reports
	 */
	@Override
	public Multimap<Class<? extends Report<?>>, Report<?>> getAllReports() {
//		return this.getReports(Message.class);
		Multimap<Class<? extends Report<?>>, Report<?>> reportMap = HashMultimap.create();
		
		for (Multimap<Class<? extends Report<?>>, Report<?>> map : this.reports.values()) {
			reportMap.putAll(map);
		}
		
		return reportMap;
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
		this.reports.clear();
		
		// notify listeners
		this.notifyReportsDeleted();
	}

}
