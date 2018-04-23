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

package eu.esdihumboldt.hale.common.align.transformation.report.impl;

import java.util.Properties;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition;

/**
 * Object definition for {@link TransformationReport}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TransformationReportImplDefinition
		extends AbstractReportDefinition<TransformationReport, TransformationReporter> {

	private static final ALogger _log = ALoggerFactory
			.getLogger(TransformationReportImplDefinition.class);

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
		return new PreparedTransformationReporter(props.getProperty(KEY_REPORT_TASKNAME),
				props.getProperty(KEY_REPORT_TASKTYPE), false);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition#configureReport(eu.esdihumboldt.hale.common.core.report.Report,
	 *      java.util.Properties)
	 */
	@Override
	protected TransformationReport configureReport(TransformationReporter reporter,
			Properties props) {
		try {
			AbstractReportDefinition.configureBasicReporter(reporter, props);
		} catch (Exception e) {
			_log.error("Error while parsing a report", e);
		}

		return reporter;
	}

}
