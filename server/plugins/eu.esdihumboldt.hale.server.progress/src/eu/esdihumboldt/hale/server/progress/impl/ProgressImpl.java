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

package eu.esdihumboldt.hale.server.progress.impl;

import eu.esdihumboldt.hale.server.progress.Progress;

/**
 * Progress implementation that represents a snapshot with no support for
 * canceling.
 * 
 * @author Simon Templer
 */
public class ProgressImpl implements Progress {

	private final String taskName;
	private final String subTask;
	private final boolean indeterminate;
	private final int worked;
	private final int totalWork;
	private final String jobId;

	/**
	 * Create a progress information object.
	 * 
	 * @param taskName the task name, may be <code>null</code>
	 * @param subTask the sub task name, may be <code>null</code>
	 * @param indeterminate if the progress is indeterminate
	 * @param worked the number of worked work units
	 * @param totalWork the total work units
	 * @param jobId a unique identifier for the associated job
	 */
	public ProgressImpl(String taskName, String subTask, boolean indeterminate, int worked,
			int totalWork, String jobId) {
		super();
		this.taskName = taskName;
		this.subTask = subTask;
		this.indeterminate = indeterminate;
		this.worked = worked;
		this.totalWork = totalWork;
		this.jobId = jobId;
	}

	@Override
	public String getJobId() {
		return jobId;
	}

	@Override
	public String getTaskName() {
		return taskName;
	}

	@Override
	public String getSubTask() {
		return subTask;
	}

	@Override
	public boolean isIndeterminate() {
		return indeterminate;
	}

	@Override
	public int getWorked() {
		return worked;
	}

	@Override
	public int getTotalWork() {
		return totalWork;
	}

	/**
	 * @see eu.esdihumboldt.hale.server.progress.Progress#isCanceled()
	 */
	@Override
	public boolean isCanceled() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.server.progress.Progress#cancel()
	 */
	@Override
	public void cancel() {
		// do nothing
	}

}
