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

package eu.esdihumboldt.hale.common.core.io.impl;

import java.text.MessageFormat;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;

/**
 * Progress indicator that writes to the console
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class LogProgressIndicator implements ProgressIndicator {

	private static final ALogger log = ALoggerFactory.getMaskingLogger(LogProgressIndicator.class,
			null);

	private String currentTask = null; //$NON-NLS-1$

	private int totalWork = UNKNOWN;

	private int worked = 0;

	private String mainTaskName;

	/**
	 * @see ProgressIndicator#setCurrentTask(java.lang.String)
	 */
	@Override
	public void setCurrentTask(String taskName) {
		this.currentTask = taskName;
		trigger();
	}

	/**
	 * @see ProgressIndicator#advance(int)
	 */
	@Override
	public void advance(int workUnits) {
		this.worked += workUnits;
		trigger();
	}

	private void trigger() {
		if (totalWork > 0 && totalWork != UNKNOWN) {
			log.info(MessageFormat.format((currentTask == null) ? ("{0} - {1,number,percent}")
					: ("{0} - {1,number,percent} - {2}"), mainTaskName, (float) worked
					/ (float) totalWork), currentTask);
		}
		else {
			log.info(currentTask);
		}
	}

	/**
	 * @see ProgressIndicator#isCanceled()
	 */
	@Override
	public boolean isCanceled() {
		// no support for canceling
		return false;
	}

	/**
	 * @see ProgressIndicator#begin(String, int)
	 */
	@Override
	public void begin(String taskName, int totalWork) {
		this.mainTaskName = taskName;
		this.totalWork = totalWork;
		this.worked = 0;
		if (mainTaskName != null) {
			log.info(MessageFormat.format("Starting task ''{0}''...", mainTaskName));
		}
		else {
			log.info("Starting task...");
		}
	}

	/**
	 * @see ProgressIndicator#end()
	 */
	@Override
	public void end() {
		if (mainTaskName != null) {
			log.info(MessageFormat.format("Finished task ''{0}''.", mainTaskName));
		}
		else {
			log.info("Finished task.");
		}
	}

}
