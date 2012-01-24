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

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Report listener interface. Listens for {@link ReportService} events.
 * @param <R> the supported report type 
 * @param <M> the supported message type
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2 
 */
public interface ReportListener<R extends Report<M>, M extends Message> {
	
	/**
	 * Get the report type.
	 * 
	 * @return the report type
	 */
	public Class<R> getReportType();
	
	/**
	 * Get the message type
	 * 
	 * @return the message type
	 */
	public Class<M> getMessageType();
	
	/**
	 * Called when a report has been added
	 * 
	 * @param report the report that was added
	 */
	public void reportAdded(R report);

	/**
	 * Called when all reports have been deleted
	 */
	public void reportsDeleted();
}
