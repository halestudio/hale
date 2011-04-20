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

package eu.esdihumboldt.hale.ui.io;

import org.eclipse.core.runtime.IProgressMonitor;

import eu.esdihumboldt.hale.core.io.ProgressIndicator;

/**
 * Progress indicator using an {@link IProgressMonitor}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2 
 */
public class ProgressMonitorIndicator implements ProgressIndicator {
	
	private final IProgressMonitor monitor;
	
	private int worked = 0;
	
	private static final int MAX_WORK = 1000;
	
	/**
	 * Create a progress indicator based on an {@link IProgressMonitor}
	 * 
	 * @param monitor the progress monitor
	 */
	public ProgressMonitorIndicator(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * @see ProgressIndicator#begin(java.lang.String, boolean)
	 */
	@Override
	public void begin(String taskName, boolean undetermined) {
		monitor.beginTask(taskName, (undetermined)?(IProgressMonitor.UNKNOWN):(MAX_WORK));
	}

	/**
	 * @see ProgressIndicator#end()
	 */
	@Override
	public void end() {
		monitor.done();
	}

	/**
	 * @see ProgressIndicator#isCanceled()
	 */
	@Override
	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	/**
	 * @see ProgressIndicator#setCurrentTask(String)
	 */
	@Override
	public void setCurrentTask(String taskName) {
		monitor.subTask(taskName);
	}

	/**
	 * @see ProgressIndicator#setProgress(float)
	 */
	@Override
	public synchronized void setProgress(float percent) {
		int hasWorked = (int) (percent * MAX_WORK / 100);
		monitor.worked(hasWorked - worked);
		worked = hasWorked;
	}

}
