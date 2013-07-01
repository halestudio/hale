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

package eu.esdihumboldt.hale.common.core.io.report.impl;

import java.net.URI;
import java.util.Properties;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition;

/**
 * Object definition for {@link IOReporter}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class IOReportImplDefinition extends AbstractReportDefinition<IOReport, IOReporter> {

	private static final ALogger _log = ALoggerFactory.getLogger(IOReportImplDefinition.class);

	/**
	 * Key for target
	 */
	public static final String KEY_IOREPORT_TARGET = "target";

	/**
	 * Constructor
	 */
	public IOReportImplDefinition() {
		super(IOReport.class, "io");
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition#createReport(java.util.Properties)
	 */
	@Override
	protected IOReporter createReport(Properties props) {
		return new DefaultIOReporter(new DefaultInputSupplier(URI.create(props
				.getProperty(KEY_IOREPORT_TARGET))), props.getProperty(KEY_REPORT_TASKNAME), false);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition#configureReport(eu.esdihumboldt.hale.common.core.report.Report,
	 *      java.util.Properties)
	 */
	@Override
	protected IOReport configureReport(IOReporter reporter, Properties props) {
		try {
			AbstractReportDefinition.configureBasicReporter(reporter, props);
		} catch (Exception e) {
			_log.error("Error while parsing a report", e.getStackTrace());
		}

		return reporter;
	}

	@Override
	protected Properties asProperties(IOReport report) {
		Properties props = super.asProperties(report);

		if (report.getTarget() != null && report.getTarget().getLocation() != null) {
			props.setProperty(KEY_IOREPORT_TARGET, report.getTarget().getLocation().toString());
		}

		return props;
	}
}
