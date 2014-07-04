/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.app.transform

import org.joda.time.Period
import org.joda.time.format.PeriodFormat

import eu.esdihumboldt.hale.common.core.report.Report
import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;
import groovy.transform.TypeCheckingMode;

/**
 * Utilities for console output.
 * 
 * @author Simon Templer
 */
@TypeChecked
class ExecUtil implements ConsoleConstants {
	
	static void printSummary(Report report) {
		// print report summary
		println "${MSG_PREFIX}Action summary: ${report.taskName}"
		println "${report.errors.empty ? MSG_PREFIX : WARN_PREFIX}   ${report.errors.size()} errors"
		println "${report.warnings.empty ? MSG_PREFIX : WARN_PREFIX}   ${report.warnings.size()} warnings"

		// state success
		print(report.isSuccess() ?
			"${MSG_PREFIX}   Completed" : 
			"${ERROR_PREFIX}   Failed")
		
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
	
	static void info(String msg) {
		println "${MSG_PREFIX}$msg"
	}
	
	static void warn(String msg) {
		println "${WARN_PREFIX}$msg"
	}
	
	static void error(String msg) {
		println "${ERROR_PREFIX}$msg"
	}
	
	static void status(String msg) {
		println "${STATUS_PREFIX}$msg"
	}
	
	static IllegalStateException fail(String msg) {
		error(msg)
		throw new IllegalStateException(msg)
	}

}
