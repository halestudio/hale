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

package eu.esdihumboldt.hale.common.headless.report;

import java.io.File;
import java.io.IOException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.core.report.writer.ReportWriter;

/**
 * Represents a report file.
 * 
 * @author Simon Templer
 */
public class ReportFile implements ReportHandler {

	private static final ALogger log = ALoggerFactory.getLogger(ReportFile.class);

	private final File file;

	/**
	 * Create a report file representation based on the given file.
	 * 
	 * @param file the report file, may already contain reports
	 */
	public ReportFile(File file) {
		super();
		this.file = file;
	}

	/**
	 * @see ReportHandler#publishReport(Report)
	 */
	@Override
	public void publishReport(Report<?> report) {
		try {
			ReportWriter.write(file, true, report);
		} catch (IOException e) {
			log.error("Error writing report file: " + file, e);
		}
	}

}
