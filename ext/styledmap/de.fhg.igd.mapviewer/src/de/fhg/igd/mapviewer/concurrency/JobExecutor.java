/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.concurrency;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Executor using Eclipse jobs.
 * 
 * @author Simon Templer
 */
public class JobExecutor implements Executor {

	/**
	 * Eclipse Job Progress
	 */
	public static class JobProgress implements Progress {

		private final IProgressMonitor monitor;

		private final String name;

		/**
		 * Creates a job progress
		 * 
		 * @param monitor the eclipse progress monitor
		 * @param name the job name
		 */
		public JobProgress(IProgressMonitor monitor, String name) {
			this.monitor = monitor;
			this.name = name;
		}

		/**
		 * @see Progress#begin(int)
		 */
		@Override
		public void begin(int total) {
			monitor.beginTask(name, total);
		}

		/**
		 * @see Progress#begin()
		 */
		@Override
		public void begin() {
			monitor.beginTask(name, IProgressMonitor.UNKNOWN);
		}

		/**
		 * @see Progress#isCanceled()
		 */
		@Override
		public boolean isCanceled() {
			return monitor.isCanceled();
		}

		/**
		 * @see Progress#progress(int)
		 */
		@Override
		public void progress(int work) {
			monitor.worked(work);
		}

		/**
		 * @see Progress#setTask(String)
		 */
		@Override
		public void setTask(String task) {
			monitor.setTaskName(task);
		}

	}

	/**
	 * Custom job
	 * 
	 * @param <R> the job result type
	 */
	public static class CustomJob<R> extends Job {

		private final IJob<R> job;

		/**
		 * Creates a eclipse job wrapping a
		 * {@link de.fhg.igd.mapviewer.concurrency.Job}
		 * 
		 * @param job the job to execute
		 */
		public CustomJob(IJob<R> job) {
			super(job.getName());

			this.job = job;

			if (job.isHidden()) {
				setSystem(true);
			}
			else if (!job.isBackground()) {
				setUser(true);
			}

			if (job.isExclusive()) {
				setRule(new ExclusiveSchedulingRule(job.getName()));
			}
		}

		/**
		 * @see Job#run(IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				Progress progress = new JobProgress(monitor, job.getName());
				R result = job.work(progress);
				if (job.getCallback() != null) {
					job.getCallback().done(result);
				}
				monitor.done();
				return Status.OK_STATUS;
			} catch (Throwable e) {
				if (job.getCallback() != null) {
					job.getCallback().failed(e);
				}
				return Status.CANCEL_STATUS;
			}
		}

	}

	/**
	 * @see Executor#start(IJob)
	 */
	@Override
	public <T> void start(IJob<T> job) {
		Job eclipseJob = new CustomJob<T>(job);
		eclipseJob.schedule();
	}

}
