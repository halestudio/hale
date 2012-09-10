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

package eu.esdihumboldt.hale.common.align.transformation.report;

import eu.esdihumboldt.hale.common.core.report.ReportLog;

/**
 * Report log for transformation functions. Messages for the report should be
 * created using {@link #createMessage(String, Throwable)}
 * 
 * @author Simon Templer
 * @since 2.5
 */
public interface TransformationLog extends ReportLog<TransformationMessage> {

	/**
	 * Create a message configured with the current cell for use with the log
	 * 
	 * @param message the message string
	 * @param throwable a throwable associated to the message, may be
	 *            <code>null</code>
	 * @return the message
	 */
	TransformationMessage createMessage(String message, Throwable throwable);

}
