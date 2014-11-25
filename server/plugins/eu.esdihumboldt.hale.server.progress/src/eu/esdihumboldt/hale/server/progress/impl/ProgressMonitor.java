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

import org.eclipse.core.runtime.IProgressMonitor;

import eu.esdihumboldt.hale.server.progress.Progress;

/**
 * Progress monitor that just stores the progress information.
 * 
 * @author Simon Templer
 */
public class ProgressMonitor implements IProgressMonitor, Progress {

	private volatile String taskName;
	private volatile String subtask;
	private volatile int worked = 0;
	private volatile int totalWork = 0;
	private volatile boolean indeterminate = true;
	private volatile boolean canceled = false;
	private final String id;

	/**
	 * Create a new progress monitor with the given identifier.
	 * 
	 * @param id the job identifier
	 */
	public ProgressMonitor(String id) {
		super();
		this.id = id;
	}

	@Override
	public String getJobId() {
		return id;
	}

	@Override
	public void beginTask(String name, int totalWork) {
		this.taskName = name;
		this.totalWork = totalWork;
		this.indeterminate = totalWork == UNKNOWN;
	}

	@Override
	public void done() {
		worked = totalWork;
	}

	@Override
	public void internalWorked(double work) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public void setCanceled(boolean value) {
		this.canceled = value;
	}

	@Override
	public void setTaskName(String name) {
		this.taskName = name;
	}

	@Override
	public void subTask(String name) {
		this.subtask = name;
	}

	@Override
	public void worked(int work) {
		this.worked = Math.min(this.worked + work, totalWork);
	}

	@Override
	public String getTaskName() {
		return taskName;
	}

	@Override
	public String getSubTask() {
		return subtask;
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

	@Override
	public void cancel() {
		setCanceled(true);
	}

}