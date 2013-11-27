/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.application.workbench;

import org.eclipse.ui.IWorkbench;

/**
 * Hook for workbench events. This serves for execution at startup or shutdown
 * without the need to extend the application workbench advisor.
 * 
 * @author Simon Templer
 */
public interface WorkbenchHook {

	/**
	 * Performs arbitrary actions just before the first workbench window is
	 * opened (or restored), and before the main event loop is run.
	 * <p>
	 * This method is called after the workbench has been initialized and just
	 * before the first window is about to be opened.
	 * </p>
	 * 
	 * @param workbench the workbench
	 */
	public void preStartup(IWorkbench workbench);

	/**
	 * Performs arbitrary actions after the workbench windows have been opened
	 * (or restored), but before the main event loop is run.
	 * <p>
	 * This method is called just after the windows have been opened.
	 * </p>
	 * 
	 * @param workbench the workbench
	 */
	public void postStartup(IWorkbench workbench);

	/**
	 * Performs arbitrary finalization before the workbench is about to shut
	 * down.
	 * <p>
	 * This method is called immediately prior to workbench shutdown before any
	 * windows have been closed.
	 * </p>
	 * <p>
	 * The advisor may veto a regular shutdown by returning <code>false</code>,
	 * although this will be ignored if the workbench is being forced to shut
	 * down or another listener already vetoed.
	 * </p>
	 * 
	 * @param workbench the workbench
	 * @return <code>true</code> to allow the workbench to proceed with
	 *         shutdown, <code>false</code> to veto a non-forced shutdown
	 */
	public boolean preShutdown(IWorkbench workbench);

	/**
	 * Performs arbitrary finalization after the workbench stops running.
	 * <p>
	 * This method is called during workbench shutdown after all windows have
	 * been closed.
	 * </p>
	 * 
	 * @param workbench the workbench
	 */
	public void postShutdown(IWorkbench workbench);

}
