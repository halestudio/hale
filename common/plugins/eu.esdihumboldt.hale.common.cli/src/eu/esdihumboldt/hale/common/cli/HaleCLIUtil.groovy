/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.common.cli

import org.joda.time.Period
import org.joda.time.format.PeriodFormat

import eu.esdihumboldt.hale.common.core.report.Report
import eu.esdihumboldt.hale.common.core.report.ReportHandler
import eu.esdihumboldt.hale.common.headless.report.ReportFile
import groovy.cli.picocli.CliBuilder
import groovy.transform.CompileStatic

class HaleCLIUtil {

	@CompileStatic
	static void printSummary(Report report) {
		// print report summary
		println "Action summary: ${report.taskName}"
		println "   ${report.errors.size()} errors"
		println "   ${report.warnings.size()} warnings"

		// state success
		print(report.isSuccess() ?
				"   Completed" :
				"   Failed")

		// add duration if applicable
		if (report.startTime) {
			def duration = PeriodFormat.wordBased().print(
					new Period(report.startTime.time, report.timestamp.time))

			print(report.isSuccess() ? ' in ' : ' after ')
			print duration
		}
		// complete success line
		println ''
	}

	static void defaultOptions(CliBuilder cli, boolean logReports = false) {
		cli._(longOpt: 'log-out', args:1, argName:'file', 'Log output stream to a file')
		cli._(longOpt: 'log-err', args:1, argName:'file', 'Log error stream to a file')
		if (logReports) {
			cli._(longOpt: 'log-reports', args: 1, argName: 'file', 'Log reports to a file')
		}
	}

	static ReportHandler createReportHandler(OptionAccessor options) {
		def repFile = options.'log-reports'
		if (repFile) {
			createReportHandler(repFile as File)
		}
		else {
			createReportHandler()
		}
	}

	@CompileStatic
	static ReportHandler createReportHandler(File reportFile = null) {
		ReportHandler delegateTo
		if (reportFile) {
			delegateTo = new ReportFile(reportFile)
		}
		else {
			delegateTo = null
		}

		new ReportHandler() {
					@Override
					public void publishReport(Report report) {
						printSummary(report)
						if (delegateTo != null) {
							delegateTo.publishReport(report);
						}
					}
				}
	}
}
