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

package eu.esdihumboldt.hale.ui.io.util;

import org.eclipse.core.runtime.IProgressMonitor;

import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;

/**
 * Progress indicator using an {@link IProgressMonitor}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class ProgressMonitorIndicator implements ProgressIndicator {

	private final IProgressMonitor monitor;

	/**
	 * Create a progress indicator based on an {@link IProgressMonitor}
	 * 
	 * @param monitor the progress monitor
	 */
	public ProgressMonitorIndicator(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * @see ProgressIndicator#begin(String, int)
	 */
	@Override
	public void begin(String taskName, int totalWork) {
		monitor.beginTask(taskName,
				(totalWork == ProgressIndicator.UNKNOWN) ? (IProgressMonitor.UNKNOWN) : (totalWork));
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
	 * @see ProgressIndicator#advance(int)
	 */
	@Override
	public synchronized void advance(int workUnits) {
		monitor.worked(workUnits);
	}

}
