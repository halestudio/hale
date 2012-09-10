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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportDefinition;
import eu.esdihumboldt.hale.common.core.report.Reporter;

/**
 * Abstract report definition.
 * 
 * @author Andreas Burchert
 * @param <T> the report type
 * @param <R> the reporter
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractReportDefinition<T extends Report<?>, R extends T> implements
		ReportDefinition<T> {

	private static final ALogger _log = ALoggerFactory.getLogger(AbstractReportDefinition.class);

	private final Class<T> reportClass;

	private final String identifier;

	/**
	 * Key for taskname
	 */
	public static final String KEY_REPORT_TASKNAME = "taskname";

	/**
	 * Key for success
	 */
	public static final String KEY_REPORT_SUCCESS = "success";

	/**
	 * Key for summary
	 */
	public static final String KEY_REPORT_SUMMARY = "summary";

	/**
	 * Key for starttime
	 */
	public static final String KEY_REPORT_STARTTIME = "starttime";

	/**
	 * Key for timestamp
	 */
	public static final String KEY_REPORT_TIME = "timestamp";

	/**
	 * Key for messagetype
	 */
	public static final String KEY_REPORT_MESSAGE_TYPE = "messagetype";

	/**
	 * Key for info messages
	 */
	public static final String KEY_REPORT_INFOS = "info";

	/**
	 * Key for error messages
	 */
	public static final String KEY_REPORT_ERRORS = "error";

	/**
	 * Key for warning messages
	 */
	public static final String KEY_REPORT_WARNINGS = "warning";

	/**
	 * Create report definition.
	 * 
	 * @param reportClass the report class
	 * @param id the identifier for the definition (without prefix)
	 */
	public AbstractReportDefinition(Class<T> reportClass, String id) {
		super();

		this.reportClass = reportClass;
		this.identifier = ID_PREFIX + id.toUpperCase();
	}

	/**
	 * @see eu.esdihumboldt.util.definition.ObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @see eu.esdihumboldt.util.definition.ObjectDefinition#getObjectClass()
	 */
	@Override
	public Class<T> getObjectClass() {
		return reportClass;
	}

	/**
	 * @see eu.esdihumboldt.util.definition.ObjectDefinition#parse(java.lang.String)
	 */
	@Override
	public T parse(String value) {
		Properties props = new Properties();
		StringReader reader = new StringReader(value.trim());
		try {
			props.load(reader);
		} catch (IOException e) {
			_log.error("Error loading report properties", e);
			return null;
		} finally {
			reader.close();
		}

		R reporter = createReport(props);
		configureReport(reporter, props);
		return reporter;
	}

	/**
	 * Create a report from a set of properties.
	 * 
	 * @param props the properties
	 * @return the report
	 */
	protected abstract R createReport(Properties props);

	/**
	 * Configure the report.
	 * 
	 * @param reporter report to configure
	 * @param props properties to set
	 * @return the report
	 */
	protected abstract T configureReport(R reporter, Properties props);

	/**
	 * Basic configuration that should be called from every child class!
	 * 
	 * @param reporter reporter
	 * @param props properties
	 * @throws Exception if parsing fails
	 */
	public static void configureBasicReporter(Reporter<?> reporter, Properties props)
			throws Exception {
		// set summary
		reporter.setSummary(props.getProperty(KEY_REPORT_SUMMARY));

		// set success
		reporter.setSuccess(Boolean.parseBoolean(props.getProperty(KEY_REPORT_SUCCESS)));

		// parse times and set them
		reporter.setStartTime(new Date(Long.parseLong(props.getProperty(KEY_REPORT_STARTTIME))));
		reporter.setTimestamp(new Date(Long.parseLong(props.getProperty(KEY_REPORT_TIME))));
	}

	/**
	 * @see eu.esdihumboldt.util.definition.ObjectDefinition#asString(java.lang.Object)
	 */
	@Override
	public String asString(T report) {
		String nl = System.getProperty("line.separator");
		Properties props = asProperties(report);

		StringWriter writer = new StringWriter();
		try {
			props.store(writer, null);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// ignore
			}
		}

		return nl + writer.toString() + nl + nl;
	}

	/**
	 * Get a {@link Properties} representation of the given report that can be
	 * used to create a new report instance using
	 * {@link #createReport(Properties)}.
	 * 
	 * @param report the message
	 * @return the properties representing the report
	 */
	protected Properties asProperties(T report) {
		Properties props = new Properties();

		props.setProperty(KEY_REPORT_TASKNAME, report.getTaskName());
		props.setProperty(KEY_REPORT_SUCCESS, "" + report.isSuccess());
		props.setProperty(KEY_REPORT_SUMMARY, report.getSummary());

		if (report.getTimestamp() != null) {
			props.setProperty(KEY_REPORT_TIME, "" + report.getTimestamp().getTime());
		}

		if (report.getStartTime() != null) {
			props.setProperty(KEY_REPORT_STARTTIME, "" + report.getStartTime().getTime());
		}

		if (report.getMessageType() != null) {
			props.setProperty(KEY_REPORT_MESSAGE_TYPE, ""
					+ report.getMessageType().getCanonicalName());
		}

		return props;
	}
}
