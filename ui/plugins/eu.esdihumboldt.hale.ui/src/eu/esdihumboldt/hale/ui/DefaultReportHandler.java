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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import net.jcip.annotations.Immutable;

/**
 * Report handler publishing reports to the {@link ReportService}.
 * 
 * @author Simon Templer
 */
@Immutable
public class DefaultReportHandler implements ReportHandler {

	private static DefaultReportHandler instance;

	/**
	 * Get the report handler instance.
	 * 
	 * @return the singleton instance
	 */
	public static DefaultReportHandler getInstance() {
		synchronized (DefaultReportHandler.class) {
			if (instance == null) {
				instance = new DefaultReportHandler();
			}
			return instance;
		}
	}

	/**
	 * @see ReportHandler#publishReport(Report)
	 */
	@Override
	public void publishReport(Report<?> report) {
		ReportService repService = PlatformUI.getWorkbench().getService(ReportService.class);
		repService.addReport(report);
	}

}
