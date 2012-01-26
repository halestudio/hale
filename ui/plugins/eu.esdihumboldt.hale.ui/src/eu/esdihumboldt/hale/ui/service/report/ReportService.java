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

package eu.esdihumboldt.hale.ui.service.report;

import java.io.File;
import java.io.IOException;

import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;

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
	 * Saves all reports from {@link ReportService} to a
	 * specific file.
	 * 
	 * @param file the file to save
	 * 
	 * @return true on success
	 * 
	 * @throws IOException an exception if IO fails
	 */
	public boolean saveAllReports(File file) throws IOException;
}
