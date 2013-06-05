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

package eu.esdihumboldt.hale.common.core.io;

import org.eclipse.core.runtime.IProgressMonitor;

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
		try {
			monitor.subTask(taskName);
		} catch (Error e) {
			/*
			 * Catch SWTErrors that may occur because an update on the monitor
			 * is not triggered from the same thread as it was created in. This
			 * may happen in conjunction with ThreadProgressMonitor, where a
			 * progress dialog may be shared between different tasks. In that
			 * case the offending call is in Dialog.shortenText (JFace).
			 */
			if (!"org.eclipse.swt.SWTError".equals(e.getClass().getName())) {
				throw e;
			}
		} catch (RuntimeException e) {
			// see above
			if (!"org.eclipse.swt.SWTException".equals(e.getClass().getName())) {
				throw e;
			}
		}
	}

	/**
	 * @see ProgressIndicator#advance(int)
	 */
	@Override
	public synchronized void advance(int workUnits) {
		monitor.worked(workUnits);
	}

}
