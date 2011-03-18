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

package eu.esdihumboldt.hale.schemaprovider;

import org.apache.log4j.Logger;

/**
 * Progress indicator that writes to the console
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class LogProgressIndicator implements ProgressIndicator {
	
	private static final Logger log = Logger.getLogger(LogProgressIndicator.class);
	
	private String currentTask = "Task"; //$NON-NLS-1$
	
	private int progress = -1;

	/**
	 * @see ProgressIndicator#setCurrentTask(java.lang.String)
	 */
	public void setCurrentTask(String taskName) {
		this.currentTask = taskName;
		trigger();
	}

	/**
	 * @see ProgressIndicator#setProgress(int)
	 */
	public void setProgress(int percent) {
		this.progress = percent;
		trigger();
	}

	private void trigger() {
		if (progress >= 0) {
			log.info(currentTask + " " + progress + "%"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			log.info(currentTask);
		}
	}

}
