/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.app.transform;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.core.report.util.StatisticsHelper;
import eu.esdihumboldt.hale.common.headless.report.ReportFile;
import eu.esdihumboldt.util.groovy.collector.StatsCollector;

/**
 * Transformation report handler.
 * 
 * @author Simon Templer
 */
public class TransformationReports implements ReportHandler {

	private final ReportHandler delegateTo;

	private final List<Report<?>> reports = new ArrayList<>();

	private boolean printSummary = true;

	/**
	 * Create a report handler. By default also prints report summaries to the
	 * console.
	 * 
	 * @param reportFile the optional report file to write reports to
	 */
	public TransformationReports(@Nullable File reportFile) {
		if (reportFile != null) {
			delegateTo = new ReportFile(reportFile);
		}
		else {
			delegateTo = null;
		}
	}

	/**
	 * Create a report handler that collects the reports and in addition
	 * delegates to another report handler.
	 * 
	 * @param delegate the delegate report handler
	 */
	public TransformationReports(@Nullable ReportHandler delegate) {
		delegateTo = delegate;
		printSummary = false;
	}

	/**
	 * Create a report handler that collects the reports.
	 */
	public TransformationReports() {
		delegateTo = null;
		printSummary = false;
	}

	@Override
	public void publishReport(Report<?> report) {
		synchronized (reports) {
			reports.add(report);
		}
		if (isPrintSummary()) {
			ExecUtil.printSummary(report);
		}
		if (delegateTo != null) {
			delegateTo.publishReport(report);
		}
	}

	/**
	 * Get the transformation statistics.
	 * 
	 * @return the transformation statistics
	 */
	public StatsCollector getStatistics() {
		return new StatisticsHelper().getStatistics(reports);
	}

	/**
	 * @return the collected reports
	 */
	public List<Report<?>> getReports() {
		return Collections.unmodifiableList(reports);
	}

	/**
	 * @return the printSummary
	 */
	public boolean isPrintSummary() {
		return printSummary;
	}

	/**
	 * @param printSummary the printSummary to set
	 */
	public void setPrintSummary(boolean printSummary) {
		this.printSummary = printSummary;
	}

}
