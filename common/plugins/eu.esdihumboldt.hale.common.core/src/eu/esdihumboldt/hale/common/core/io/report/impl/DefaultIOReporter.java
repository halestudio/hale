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

package eu.esdihumboldt.hale.common.core.io.report.impl;

import de.fhg.igd.slf4jplus.ALogger;
import eu.esdihumboldt.hale.common.core.io.report.IOMessage;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.MutableTargetIOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.core.report.impl.DefaultReporter;

/**
 * Default I/O report implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class DefaultIOReporter extends DefaultReporter<IOMessage>
		implements IOReporter, MutableTargetIOReport {

	private Locatable target;

	/**
	 * Create an empty I/O report. It is set to not successful by default. But
	 * you should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @see DefaultReporter#DefaultReporter(String, Class, boolean)
	 * 
	 * @param target the locatable target
	 * @param taskName the name of the task the report is related to
	 * @param doLog if added messages shall also be logged using {@link ALogger}
	 */
	public DefaultIOReporter(Locatable target, String taskName, boolean doLog) {
		super(taskName, IOMessage.class, doLog);

		this.target = target;
	}

	/**
	 * @see IOReport#getTarget()
	 */
	@Override
	public Locatable getTarget() {
		return target;
	}

	/**
	 * Update the target, e.g. if the target URL is not known at the time the
	 * IOReporter is created.
	 * 
	 * @param target Updated target
	 */
	@Override
	public void setTarget(Locatable target) {
		this.target = target;
	}
}
