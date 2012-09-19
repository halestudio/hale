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

/**
 * Progress indicator
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface ProgressIndicator {

	/**
	 * Unknown amount of work units
	 */
	public static final int UNKNOWN = 0;

	/**
	 * Start the progress tracking
	 * 
	 * @param taskName the main task name
	 * @param totalWork the total work units for the progress indicator, if
	 *            unknown use {@link #UNKNOWN}
	 */
	public void begin(String taskName, int totalWork);

	/**
	 * Sets the current task name
	 * 
	 * @param taskName the task name
	 */
	public void setCurrentTask(String taskName);

	/**
	 * Advances the progress by the given work units
	 * 
	 * @param workUnits the work units
	 */
	public void advance(int workUnits);

	/**
	 * States if the execution was canceled
	 * 
	 * @return if the execution was canceled
	 */
	public boolean isCanceled();

	/**
	 * Stop the progress tracking, signaling that the task is done
	 */
	public void end();

}
