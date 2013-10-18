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

package eu.esdihumboldt.hale.server.progress;

import org.eclipse.core.runtime.jobs.Job;

/**
 * Listener for progress change on a specific job.
 * 
 * @author Simon Templer
 */
public interface JobProgressListener {

	/**
	 * Called when the progress of a job has been updated.
	 * 
	 * @param job the job
	 * @param progress the progress
	 */
	public void progressChanged(Job job, Progress progress);

	/**
	 * Called when a job has been completed.
	 * 
	 * @param job the completed job
	 */
	public void jobCompleted(Job job);

}
