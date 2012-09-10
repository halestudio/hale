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

package eu.esdihumboldt.hale.common.instancevalidator.report.impl;

import java.util.Properties;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition;
import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationReport;
import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationReporter;

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
			log.error("Error while parsing a report", e.getStackTrace());
		}

		return reporter;
	}
}
