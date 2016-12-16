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

package eu.esdihumboldt.hale.io.schematron.util

import java.text.MessageFormat

import eu.esdihumboldt.hale.common.core.io.report.IOReporter
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl
import groovy.transform.CompileDynamic
import groovy.util.slurpersupport.GPathResult

/**
 * TODO Type description
 * @author Florian Esser
 */
@CompileDynamic
class SchematronReportParser {

	def String report;

	SchematronReportParser(String report) {
		this.report = report;
	}

	void reportFailedAssertions(IOReporter reporter) {
		def GPathResult parsedReport = new XmlSlurper().parseText(report)

		for (failedAssert in parsedReport.depthFirst().findAll { it.name() == "failed-assert" }) {
			def args = [
				failedAssert.text.toString(),
				failedAssert.@test.toString(),
				failedAssert.@location.toString()
			].toArray()
			def msg = MessageFormat.format("{0} (test = \"{1}\" | location = \"{2}\")", args)
			reporter.error(new IOMessageImpl(msg, null))
		}
	}
}
