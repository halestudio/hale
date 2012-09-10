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

package eu.esdihumboldt.hale.common.align.transformation.report.impl;

import de.cs3d.util.logging.ALogger;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.core.report.impl.DefaultReporter;

/**
 * Reporter for transformation messages
 * 
 * @author Simon Templer
 */
public class DefaultTransformationReporter extends DefaultReporter<TransformationMessage> implements
		TransformationReport, TransformationReporter {

	/**
	 * Create an empty report. It is set to not successful by default. But you
	 * should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @param taskName the name of the task the report is related to
	 * @param doLog if added messages shall also be logged using {@link ALogger}
	 */
	public DefaultTransformationReporter(String taskName, boolean doLog) {
		super(taskName, TransformationMessage.class, doLog);
	}

}
