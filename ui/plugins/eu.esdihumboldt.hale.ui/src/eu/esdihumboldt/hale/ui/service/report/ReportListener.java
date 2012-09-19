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

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Report listener interface. Listens for {@link ReportService} events.
 * 
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
