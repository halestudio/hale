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

package eu.esdihumboldt.hale.common.align.transformation.report.impl;

import java.util.Properties;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition;

/**
* Object definition for {@link TransformationReport}.
* @author Andreas Burchert
* @partner 01 / Fraunhofer Institute for Computer Graphics Research
*/
public class TransformationReportImplDefinition extends
		AbstractReportDefinition<TransformationReport, TransformationReporter> {

	private static final ALogger _log = ALoggerFactory.getLogger(TransformationReportImplDefinition.class);
	
	/**
	 * Constructor.
	 */
	public TransformationReportImplDefinition() {
		super(TransformationReport.class, "transformation");
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition#createReport(java.util.Properties)
	 */
	@Override
	protected TransformationReporter createReport(Properties props) {
		return new DefaultTransformationReporter(props.getProperty(KEY_REPORT_TASKNAME), false);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition#configureReport(eu.esdihumboldt.hale.common.core.report.Report, java.util.Properties)
	 */
	@Override
	protected TransformationReport configureReport(
			TransformationReporter reporter, Properties props) {
		try {
			AbstractReportDefinition.configureBasicReporter(reporter, props);
		} catch (Exception e) {
			_log.error("Error while parsing a report", e.getStackTrace());
		}
		
		return reporter;
	}

}
