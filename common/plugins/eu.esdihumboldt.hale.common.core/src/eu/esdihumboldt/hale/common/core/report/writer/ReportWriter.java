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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.MessageFactory;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportFactory;


/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5 
 */
public class ReportWriter {
	
	/**
	 * Contains all {@link Report}s.
	 */
	private Multimap<Class<? extends Report<?>>, Report<?>> reports = HashMultimap.create();
	
	private static final ALogger _log = ALoggerFactory.getLogger(ReportWriter.class);

	/**
	 * Constructor.
	 */
	public ReportWriter() {
		/* nothing */
	}
	
	/**
	 * Adds all {@link Report}s.
	 * 
	 * @param multimap {@link List} of {@link Report}s
	 */
	public void addAllReports(Multimap<Class<? extends Report<?>>, Report<?>> multimap) {
		this.reports.putAll(multimap);
	}
	
	/**
	 * Writes all {@link Report}s to a {@link File}.
	 * 
	 * @param file target file
	 * 
	 * @return true on success
	 * 
	 * @throws IOException if IO fails
	 */
	public boolean writeAll(File file) throws IOException {
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
		PrintStream p = new PrintStream(file);
		
		// get an instance of ReportFactory
		ReportFactory rf = ReportFactory.getInstance();
		MessageFactory mf = MessageFactory.getInstance();
		
		// iterate through all reports
		for (Report<?> r : this.reports.values()) {
			// write them to the file
			p.print(rf.asString(r));
			
			for (Message m : r.getErrors()) {
				p.print(mf.asString(m));
			}
			
			for (Message m : r.getWarnings()) {
				p.print(mf.asString(m));
			}
			
			for (Message m : r.getInfos()) {
				p.print(mf.asString(m));
			}
		}
		
		// close stream
		p.flush();
		p.close();
		
		return true;
	}
}
