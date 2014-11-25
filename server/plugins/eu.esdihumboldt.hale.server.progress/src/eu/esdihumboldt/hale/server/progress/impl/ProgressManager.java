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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.jobs.ProgressProvider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.server.progress.JobProgressListener;
import eu.esdihumboldt.hale.server.progress.Progress;
import eu.esdihumboldt.hale.server.progress.ProgressService;

/**
 * Progress manager to replace the progress provider available in Eclipse in a
 * context without Eclipse UI.
 * 
 * @author Simon Templer
 */
public class ProgressManager extends ProgressProvider implements ProgressService {

	private final Map<Job, ProgressMonitor> monitors = new HashMap<Job, ProgressMonitor>();

	private final Multimap<Job, JobProgressListener> listeners = HashMultimap.create();

	/**
	 * Default constructor
	 */
	public ProgressManager() {
		super();

		// add listener to job manager to remove finished jobs
		Job.getJobManager().addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				synchronized (monitors) {
					monitors.remove(event.getJob());
				}
				synchronized (listeners) {
					Collection<JobProgressListener> jobListeners = listeners.get(event.getJob());
					Iterator<JobProgressListener> it = jobListeners.iterator();
					while (it.hasNext()) {
						// inform about completed job
						it.next().jobCompleted(event.getJob());

						// also remove listener
						it.remove();
					}
				}
			}

		});

		// register the progress manager
		Job.getJobManager().setProgressProvider(this);
	}

	/**
	 * @see ProgressProvider#createMonitor(Job)
	 */
	@Override
	public IProgressMonitor createMonitor(final Job job) {
		// derive ID from job or generate ID for job

		// the toString method uses the intern job number
		// XXX this is unreliable (e.g. if toString is overridden)
//		final String id = job.toString();
		// use generated ID
		final String id = UUID.randomUUID().toString();

		ProgressMonitor monitor = new ProgressMonitor(id) {

			@Override
			public void beginTask(String name, int totalWork) {
				super.beginTask(name, totalWork);
				notifyProgressChanged(job, this);
			}

			@Override
			public void setCanceled(boolean value) {
				super.setCanceled(value);
				notifyProgressChanged(job, this);
			}

			@Override
			public void setTaskName(String name) {
				super.setTaskName(name);
				notifyProgressChanged(job, this);
			}

			@Override
			public void subTask(String name) {
				super.subTask(name);
				notifyProgressChanged(job, this);
			}

			@Override
			public void worked(int work) {
				super.worked(work);
				notifyProgressChanged(job, this);
			}

			@Override
			public void cancel() {
				super.cancel();
				notifyProgressChanged(job, this);
			}

		};
		synchronized (monitors) {
			monitors.put(job, monitor);
		}
		return monitor;
	}

	/**
	 * Notify on a job progress change.
	 * 
	 * @param job the job
	 * @param progress the progress
	 */
	protected void notifyProgressChanged(Job job, Progress progress) {
		synchronized (listeners) {
			for (JobProgressListener listener : listeners.get(job)) {
				listener.progressChanged(job, progress);
			}
		}
	}

	/**
	 * @see ProgressService#getJobProgress(Job)
	 */
	@Override
	public Progress getJobProgress(Job job) {
		ProgressMonitor monitor;
		synchronized (monitors) {
			monitor = monitors.get(job);
		}
		return monitor;
	}

	@Override
	public void addProgressListener(Job job, JobProgressListener listener) {
		synchronized (listeners) {
			listeners.put(job, listener);
		}
	}

	@Override
	public void removeProgressListener(Job job, JobProgressListener listener) {
		synchronized (listeners) {
			listeners.remove(job, listener);
		}
	}

}
