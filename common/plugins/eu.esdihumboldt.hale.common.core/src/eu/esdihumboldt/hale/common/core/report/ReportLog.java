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

package eu.esdihumboldt.hale.common.core.report;

/**
 * Report log interface for contributing to a report
 * 
 * @param <T> the message type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface ReportLog<T extends Message> {

	/**
	 * Adds a warning to the report. If configured accordingly a log message
	 * will also be created.
	 * 
	 * @param message the warning message
	 */
	public void warn(T message);

	/**
	 * Adds an error to the report. If configured accordingly a log message will
	 * also be created.
	 * 
	 * @param message the error message
	 */
	public void error(T message);

	/**
	 * Adds an info to the report. If configured accordingly a log message will
	 * also be created.
	 * 
	 * @param message the info message
	 */
	public void info(T message);

}
