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

package eu.esdihumboldt.hale.common.core.report.impl;

import java.util.Properties;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
* Object definition for {@link DefaultReporter}.
* @author Andreas Burchert
* @partner 01 / Fraunhofer Institute for Computer Graphics Research
*/
@SuppressWarnings("rawtypes")
public class ReportImplDefintion extends AbstractReportDefinition<Report> {
	
	public static final String KEY_REPORT_TASKNAME = "taskname";
	
	public static final String KEY_REPORT_SUCCESS = "success";
	
	public static final String KEY_REPORT_SUMMARY = "summary";
	
	public static final String KEY_REPORT_TIME = "timestamp";
	
	public static final String KEY_REPORT_MESSAGE_TYPE = "messagetype";
	
	public static final String KEY_REPORT_INFOS = "info";
	
	public static final String KEY_REPORT_ERRORS = "error";
	
	public static final String KEY_REPORT_WARNINGS = "warning";
	
	/**
	 * Default constructor.
	 */
	public ReportImplDefintion() {
		super(Report.class, "default");
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition#createReport(java.util.Properties)
	 */
	@Override
	protected Report<Message> createReport(final Properties props) {
		// TODO find a way for proper re-creation of reports
		DefaultReporter<Message> report = new DefaultReporter<Message>(props.getProperty(KEY_REPORT_TASKNAME), 
				null, false);
		report.setSummary(props.getProperty(KEY_REPORT_SUMMARY));
		
		// add infos, warnings and errors
//		report.getWarnings().addAll(Arrays.asList(StringUtils.split(props.getProperty(KEY_REPORT_WARNINGS), ";"))); // TODO add a proper way of adding old warnings and stuff
		
		
		return report;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.impl.AbstractReportDefinition#asProperties(eu.esdihumboldt.hale.common.core.report.Report)
	 */
	@Override
	protected Properties asProperties(Report<?> report) {
		Properties props = new Properties();
		
		// TODO implement asProperties() !
		props.setProperty(KEY_REPORT_TASKNAME, report.getTaskName());
		props.setProperty(KEY_REPORT_SUCCESS, ""+report.isSuccess());
		props.setProperty(KEY_REPORT_SUMMARY, report.getSummary());
		props.setProperty(KEY_REPORT_TIME, ""+report.getTimestamp());
		props.setProperty(KEY_REPORT_MESSAGE_TYPE, report.getMessageType().toString());
		
//		props.setProperty(KEY_REPORT_INFOS, StringUtils.join(report.getInfos(), ";"));
//		props.setProperty(KEY_REPORT_ERRORS, StringUtils.join(report.getErrors(), ";"));
//		props.setProperty(KEY_REPORT_WARNINGS, StringUtils.join(report.getWarnings(), ";"));
		
		return props ;
	}

}
