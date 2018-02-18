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

package eu.esdihumboldt.hale.common.instance.extension.validation.report.impl;

import de.fhg.igd.slf4jplus.ALogger;
import eu.esdihumboldt.hale.common.core.report.impl.DefaultReporter;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationMessage;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReport;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReporter;

/**
 * Reporter for instance validation messages.
 * 
 * @author Kai Schwierczek
 */
public class DefaultInstanceValidationReporter extends DefaultReporter<InstanceValidationMessage>
		implements InstanceValidationReport, InstanceValidationReporter {

	/**
	 * Create an empty report. You should always call
	 * {@link #setSuccess(boolean)} at least once on a report.
	 * 
	 * @param doLog if added messages shall also be logged using {@link ALogger}
	 */
	public DefaultInstanceValidationReporter(boolean doLog) {
		super("Instance validation", TASK_TYPE, InstanceValidationMessage.class, doLog);
	}
}
