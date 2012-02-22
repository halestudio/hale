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

package eu.esdihumboldt.hale.ui.io.util.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;

/**
 * Custom progress monitor. Wraps a given progress monitor and states if
 * {@link #done()} was called.
 * @author Simon Templer
 */
public class StatesIfDoneProgressMonitor extends ProgressMonitorWrapper {

	private boolean done;
	
	/**
	 * Create a custom progress monitor.
	 * @param monitor the progress monitor to wrap
	 */
	public StatesIfDoneProgressMonitor(IProgressMonitor monitor) {
		super(monitor);
	}

	/**
	 * If {@link #done()} has been called on the monitor.
	 * @return the done
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * @see ProgressMonitorWrapper#done()
	 */
	@Override
	public void done() {
		super.done();
		
		done = true;
	}

}
