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

package eu.esdihumboldt.hale.core.report.writer;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.core.report.Message;
import eu.esdihumboldt.hale.core.report.Report;


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
	private ArrayList<Report<Message>> reports = new ArrayList<Report<Message>>();

	public ReportWriter() {
		
	}
	
	/**
	 * Adds a {@link Report}.
	 * 
	 * @param report the report to add
	 */
	public void addReport(Report<Message> report) {
		this.reports.add(report);
	}
	
	/**
	 * Adds all {@link Report}s.
	 * 
	 * @param reports {@link List} of {@link Report}s
	 */
	public void addAllReports(List<Report<Message>> reports) {
		this.reports.addAll(reports);
	}
	
	/**
	 * Dummy function for tests.
	 */
	public void write() {
	    StringBuilder result = new StringBuilder();
	    String nl = System.getProperty("line.separator");
	    
	    for (Report<?> r : this.reports) {
	    	// check for specific type
	    	result.append("!REPORT_DEFAULT:"+nl);
	    	
	    	// print default data
	    	result.append(r.toString()+nl);
	    	
	    	// iterate through all messages
	    	for (Object m : r.getWarnings()) {
	    		String type = m.getClass().getSimpleName().toUpperCase().replace("IMPL", ""); // use this until there's a better idea
	    		result.append("!WARN"+nl);
	    		result.append("!MSG_"+type+":"+nl);
	    		result.append("message = "+((Message)m).getMessage()+nl);
	    		result.append("stack = "+((Message)m).getStackTrace()+nl);
	    		
	    		result.append(nl);
	    	}
	    	
	    	for (Object m : r.getErrors()) {
	    		String type = m.getClass().getSimpleName().toUpperCase().replace("IMPL", ""); // use this until there's a better idea
	    		result.append("!ERROR"+nl);
	    		result.append("!MSG_"+type+":"+nl);
	    		result.append("message = "+((Message)m).getMessage()+nl);
	    		result.append("stack = "+((Message)m).getStackTrace()+nl);
	    		
	    		result.append(nl);
	    	}
	    	
	    	for (Object m : r.getInfos()) {
	    		String type = m.getClass().getSimpleName().toUpperCase().replace("IMPL", ""); // use this until there's a better idea
	    		result.append("!INFO"+nl);
	    		result.append("!MSG_"+type+":"+nl);
	    		result.append("message = "+((Message)m).getMessage()+nl);
	    		result.append("stack = "+((Message)m).getStackTrace()+nl);
	    		
	    		result.append(nl);
	    	}
	    	
	    	// spacer
	    	result.append(nl+nl);
	    }
	    
	    System.out.print(result);
	}
}
