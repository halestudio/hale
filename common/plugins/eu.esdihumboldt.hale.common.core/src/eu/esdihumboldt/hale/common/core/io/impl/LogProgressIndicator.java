/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.common.core.io.impl;

import java.text.MessageFormat;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
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
