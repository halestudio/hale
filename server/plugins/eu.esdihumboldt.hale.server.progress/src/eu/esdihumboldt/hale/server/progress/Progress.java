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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.progress;

/**
 * Progress information on a job/task.
 * 
 * @author Simon Templer
 */
public interface Progress {

	/**
	 * @return a unique string identifier for the associated job
	 */
	public String getJobId();

	/**
	 * @return the task name, may be <code>null</code>
	 */
	public String getTaskName();

	/**
	 * @return the name of the current subtask, may be <code>null</code>
	 */
	public String getSubTask();

	/**
	 * @return if the progress is indeterminate
	 */
	public boolean isIndeterminate();

	/**
	 * @return the already worked work units, should be ignored if
	 *         {@link #isIndeterminate()} is <code>true</code>
	 */
	public int getWorked();

	/**
	 * @return the total work units, should be ignored if
	 *         {@link #isIndeterminate()} is <code>true</code>
	 */
	public int getTotalWork();

	/**
	 * @return if the job/task is canceled
	 */
	public boolean isCanceled();

	/**
	 * Cancel the job/task. Behavior on cancel depends on the implementation of
	 * job/task.
	 */
	public void cancel();

}
