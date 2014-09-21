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

package eu.esdihumboldt.hale.ui.util.io.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;

/**
 * Custom progress monitor. Wraps a given progress monitor and states if
 * {@link #done()} was called.
 * 
 * @author Simon Templer
 */
public class StatesIfDoneProgressMonitor extends ProgressMonitorWrapper {

	private boolean done;

	/**
	 * Create a custom progress monitor.
	 * 
	 * @param monitor the progress monitor to wrap
	 */
	public StatesIfDoneProgressMonitor(IProgressMonitor monitor) {
		super(monitor);
	}

	/**
	 * If {@link #done()} has been called on the monitor.
	 * 
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
