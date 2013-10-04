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

import org.eclipse.core.runtime.jobs.Job;

/**
 * Service that provides access to progress information.
 * 
 * @author Simon Templer
 */
public interface ProgressService {

	/**
	 * Get the progress for a given job.
	 * 
	 * @param job the job
	 * @return the job progress, may be <code>null</code> if the progress
	 *         monitor was not created yet or the job has already finished
	 */
	public Progress getJobProgress(Job job);

	/**
	 * Add a progress listener for the given job.
	 * 
	 * @param job the job
	 * @param listener the listener to add
	 */
	public void addProgressListener(Job job, JobProgressListener listener);

	/**
	 * Remove a progress listener for the given job.
	 * 
	 * @param job the job
	 * @param listener the listener to remove
	 */
	public void removeProgressListener(Job job, JobProgressListener listener);

}
