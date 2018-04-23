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

package eu.esdihumboldt.hale.common.align.transformation.report.impl;

import de.fhg.igd.slf4jplus.ALogger;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.core.report.impl.DefaultReporter;

/**
 * Reporter for transformation messages
 * 
 * @author Simon Templer
 */
public class PreparedTransformationReporter extends DefaultReporter<TransformationMessage>
		implements TransformationReport, TransformationReporter {

	/**
	 * Create an empty report. It is set to not successful by default. But you
	 * should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @param taskName the name of the task the report is related to
	 * @param taskType the identifier of the task type
	 * @param doLog if added messages shall also be logged using {@link ALogger}
	 */
	public PreparedTransformationReporter(String taskName, String taskType, boolean doLog) {
		super(taskName, taskType, TransformationMessage.class, doLog);
	}

}
