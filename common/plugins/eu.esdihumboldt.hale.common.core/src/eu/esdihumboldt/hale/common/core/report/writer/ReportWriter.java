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

package eu.esdihumboldt.hale.common.core.report.writer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

import com.google.common.collect.Lists;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.MessageFactory;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportFactory;

/**
 * Writes reports to a file.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class ReportWriter {

	private static final ALogger _log = ALoggerFactory.getLogger(ReportWriter.class);

	/**
	 * Constructor.
	 */
	private ReportWriter() {
		/* nothing */
	}

	/**
	 * Writes all {@link Report}s to a {@link File}.
	 * 
	 * @param file target file
	 * @param reports reports to be saved
	 * @param append if the reports should be appended to the file instead of
	 *            overwriting it
	 * 
	 * @return true on success
	 * 
	 * @throws IOException if IO fails
	 */
	public static boolean write(File file, boolean append, Report<?>... reports) throws IOException {
		return write(file, Lists.newArrayList(reports), append);
	}

	/**
	 * Writes all {@link Report}s to a {@link File}.
	 * 
	 * @param file target file
	 * @param reports reports to be saved
	 * @param append if the reports should be appended to the file instead of
	 *            overwriting it
	 * 
	 * @return true on success
	 * 
	 * @throws IOException if IO fails
	 */
	public static boolean write(File file, Collection<Report<?>> reports, boolean append)
			throws IOException {
		// check if the file exists
		if (!file.exists()) {
			// and create the file
			if (!file.createNewFile()) {
				_log.error("Logfile could not be created!");
				return false;
			}

			// make it writable
			file.setWritable(true);
		}

		// check if it's writable
		if (!file.canWrite()) {
			_log.error("Report could not be saved. No write permission!");
			return false;
		}

		// create PrintStream
		PrintStream p = new PrintStream(
				new BufferedOutputStream(new FileOutputStream(file, append)));
		try {
			// get an instance of ReportFactory
			ReportFactory rf = ReportFactory.getInstance();
			MessageFactory mf = MessageFactory.getInstance();

			// iterate through all reports
			for (Report<?> r : reports) {
				// write them to the file
				p.print(rf.asString(r));

				for (Message m : r.getErrors()) {
					p.println("!ERROR");
					p.print(mf.asString(m));
				}

				for (Message m : r.getWarnings()) {
					p.println("!WARN");
					p.print(mf.asString(m));
				}

				for (Message m : r.getInfos()) {
					p.println("!INFO");
					p.print(mf.asString(m));
				}
			}

			// new line at end of file to allow appending
			p.println();

			p.flush();
		} finally {
			// close stream
			p.close();
		}

		return true;
	}

}
