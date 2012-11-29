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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.jobs.ProgressProvider;

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
			}

		});

		// register the progress manager
		Job.getJobManager().setProgressProvider(this);
	}

	/**
	 * @see ProgressProvider#createMonitor(Job)
	 */
	@Override
	public IProgressMonitor createMonitor(Job job) {
		ProgressMonitor monitor = new ProgressMonitor();
		synchronized (monitors) {
			monitors.put(job, monitor);
		}
		return monitor;
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

}
