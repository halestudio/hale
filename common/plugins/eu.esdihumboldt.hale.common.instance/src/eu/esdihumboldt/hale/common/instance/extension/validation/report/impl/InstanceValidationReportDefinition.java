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

package eu.esdihumboldt.hale.common.instance.extension.validation.report.impl;

import java.util.Properties;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReport;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReporter;

/**
 * Definition for {@link InstanceValidationReport}s.
 * 
 * @author Kai Schwierczek
 */
public class InstanceValidationReportDefinition extends
		AbstractReportDefinition<InstanceValidationReport, InstanceValidationReporter> {

	private static final ALogger log = ALoggerFactory
			.getLogger(InstanceValidationReportDefinition.class);

	/**
	 * Constructor.
	 */
	public InstanceValidationReportDefinition() {
		super(InstanceValidationReport.class, "instance_validation_report");
	}

	/**
	 * @see AbstractReportDefinition#createReport(Properties)
	 */
	@Override
	protected InstanceValidationReporter createReport(Properties props) {
		return new DefaultInstanceValidationReporter(false);
	}

	/**
	 * @see AbstractReportDefinition#configureReport(Report, Properties)
	 */
	@Override
	protected InstanceValidationReport configureReport(InstanceValidationReporter reporter,
			Properties props) {
		try {
			AbstractReportDefinition.configureBasicReporter(reporter, props);
		} catch (Exception e) {
			log.error("Error while parsing a report", e);
		}

		return reporter;
	}
}
