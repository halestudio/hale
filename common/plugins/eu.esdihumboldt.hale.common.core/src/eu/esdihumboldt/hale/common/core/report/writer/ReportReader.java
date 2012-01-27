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

package eu.esdihumboldt.hale.common.core.report.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.MessageFactory;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportFactory;
import eu.esdihumboldt.hale.common.core.report.ReportSession;

/**
 * This is the ReportReader which extracts {@link Report}s
 * and their {@link Message}s from a previous saved file.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportReader {
	
	private ReportFactory rf = ReportFactory.getInstance();
	private MessageFactory mf = MessageFactory.getInstance();
	private static final ALogger _log = ALoggerFactory.getLogger(ReportReader.class);

	/**
	 * Extracts all {@link ReportSession}s from a given
	 * directory.
	 * 
	 * @param dir directory containing all log files
	 * 
	 * @return array of old {@link ReportSession}s
	 */
	public List<ReportSession> read(File dir) {
		if (!dir.exists() && dir.mkdirs()) {
			// folder does not exist so there are no reports
			return new ArrayList<ReportSession>();
		}
		
		// create a list containg the result
		List<ReportSession> list = new ArrayList<ReportSession>();
		
		// iterate through all files from the directory
		for (File f : dir.listFiles()) {
			// extract the id from filename
			long id = this.getIdentifier(f);
			
			// parse the session
			ReportSession session = this.parse(f, id);
			
			// add it to result
			list.add(session);
		}
		
		return list;
	}
	
	/**
	 * Parse a file and creates a {@link ReportSession}.
	 * 
	 * @param file file to parse
	 * @param id identifier for {@link ReportSession}
	 * 
	 * @return the {@link ReportSession}
	 */
	@SuppressWarnings("rawtypes")
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
			
			// read all data
			while (reader.ready()) {
				temp = reader.readLine();
				
				if (temp.startsWith("!") && !sw.toString().equals("")) {
					// we found a new marker, time to parse previous lines
					Report r = rf.parse(sw.toString());
					if (r != null) {
//						if (lastReport != null) {
							session.addReport(r);
//						}
						
						// new report
						lastReport = r;
					} else {
						Message m = mf.parse(sw.toString());
						if (m != null) {
							// TODO this does not work!
//							Collection infos = lastReport.getInfos();
//							infos.add(m);
						} else {
							// unsupported
						}
					}
					// then flush sw
					sw = new StringBuilder();
					// and add the new line
					sw.append(temp+nl);
				} else {
					sw.append(temp+nl);
				}
			}
			
			// close reader
			reader.close();
		} catch (Exception e) {
			_log.error("Error while parsing a log file.", e.getStackTrace());
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
		return Long.parseLong(name[0]);
	}
}
