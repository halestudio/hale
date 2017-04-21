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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.MessageFactory;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportFactory;
import eu.esdihumboldt.hale.common.core.report.ReportLog;
import eu.esdihumboldt.hale.common.core.report.ReportSession;

/**
 * This is the ReportReader which extracts {@link Report}s and their
 * {@link Message}s from a previous saved file.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportReader {

	/**
	 * Identifier for unknown report sessions.
	 */
	public static final long UNKNOWN_SESSION = 0;

	private final ReportFactory rf = ReportFactory.getInstance();
	private final MessageFactory mf = MessageFactory.getInstance();
	private static final ALogger _log = ALoggerFactory.getLogger(ReportReader.class);

	/**
	 * Extracts all {@link ReportSession}s from a given directory.
	 * 
	 * @param dir directory containing all log files
	 * 
	 * @return array of old {@link ReportSession}s
	 */
	public List<ReportSession> readDirectory(File dir) {
		if (!dir.exists() && dir.mkdirs()) {
			// folder does not exist so there are no reports
			return new ArrayList<ReportSession>();
		}

		// create a list containing the result
		List<ReportSession> list = new ArrayList<ReportSession>();

		List<Long> ids = new ArrayList<Long>();

		// iterate through all files from the directory
		for (File f : dir.listFiles()) {
			// extract the id from filename
			long id = this.getIdentifier(f);

			// add the id
			ids.add(id);
		}

		// sort the ids
		Collections.sort(ids);
		Collections.reverse(ids);

		// and load the latest 3
		for (File f : dir.listFiles()) {
			// extract the id from filename
			long id = this.getIdentifier(f);
			boolean skip = true;

			for (int i = 0; i < 3; i++) {
				if (ids.get(i) == id) {
					skip = false;
					break;
				}
			}

			if (skip) {
				continue;
			}

			// parse the session
			ReportSession session = this.parse(f, id);

			// add it to result
			list.add(session);
		}

		return list;
	}

	/**
	 * Creates a {@link ReportSession} from a report log file.
	 * 
	 * @param file the file to parse
	 * 
	 * @return {@link ReportSession} from the file
	 */
	public ReportSession readFile(File file) {
		if (file.exists()) {
			// extract the id from filename
			long id = this.getIdentifier(file);

			// parse the session
			ReportSession session = this.parse(file, id);

			return session;
		}

		return null;
	}

	/**
	 * Parse a file and creates a {@link ReportSession}.
	 * 
	 * @param file file to parse
	 * @param id identifier for {@link ReportSession}
	 * 
	 * @return the {@link ReportSession}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ReportSession parse(File file, long id) {
		// create the new session
		ReportSession session = new ReportSession(id);

		// get content
		StringBuilder sw = new StringBuilder();
		BufferedReader reader = null;
		String nl = System.getProperty("line.separator");
		try {
			// try to get a BufferedReader from FileReader
			reader = new BufferedReader(new FileReader(file));
			String temp;
			Report lastReport = null;
			String messageType = "";

			// read all data
			while (reader.ready()) {
				temp = reader.readLine();

				if (temp == null || temp.isEmpty()) {
					continue;
				}

				// check if the line starts with a marker
				if (temp.startsWith("!")) {
					// we found a new marker, time to parse previous lines
					Report r = rf.parse(sw.toString());

					if (!sw.toString().isEmpty() && !messageType.isEmpty()) {
						Message m = mf.parse(sw.toString());
						if (m != null) {
							ReportLog<Message> repLog = (ReportLog<Message>) lastReport;

							// add the message to the corresponding report
							if (messageType.equals("!ERROR")) {
								repLog.error(m);
							}
							else if (messageType.equals("!WARN")) {
								repLog.warn(m);
							}
							else if (messageType.equals("!INFO")) {
								repLog.info(m);
							}

							// reset message type
							messageType = "";
						}
					}
					else if (r != null) {
						// new report
						lastReport = r;
						session.addReport(lastReport);
					}

					// check if the new line is a marker for messages
					if (temp.startsWith("!ERROR")) {
						messageType = temp;
						temp = "";
					}
					else if (temp.startsWith("!WARN")) {
						messageType = temp;
						temp = "";
					}
					else if (temp.startsWith("!INFO")) {
						messageType = temp;
						temp = "";
					}

					// then flush sw
					sw = new StringBuilder();
					// and add the new line
					sw.append(temp + nl);
				}
				else {
					sw.append(temp + nl);
				}
			}

			// close reader
			reader.close();
		} catch (Exception e) {
			_log.error("Error while parsing a log file.", e);
		}

		return session;
	}

	/**
	 * Extract the identifier from the filename.
	 * 
	 * @param file the file
	 * 
	 * @return the id
	 */
	private long getIdentifier(File file) {
		String[] name = file.getName().split("[.]");
		String result = "";

		if (name[0].contains("-")) {
			name = name[0].split("[-]");
			result = name[1];
		}
		else {
			result = name[0];
		}

		long id = UNKNOWN_SESSION;
		try {
			id = Long.parseLong(result);
		} catch (NumberFormatException e) {
			_log.debug("Could not determine ReportSession ID, using current timestamp.");
			// XXX improvement would be determining the session ID from the file
			// content
		}

		return id;
	}
}
