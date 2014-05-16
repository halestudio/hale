/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.app.transform;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;

/**
 * Simple Progress manager that creates progress monitors that write to the
 * console.
 * 
 * @author Simon Templer
 */
public class ConsoleProgressManager extends ProgressProvider {

	/**
	 * Default constructor
	 */
	public ConsoleProgressManager() {
		super();

		// register the progress manager
		Job.getJobManager().setProgressProvider(this);
	}

	/**
	 * @see ProgressProvider#createMonitor(Job)
	 */
	@Override
	public IProgressMonitor createMonitor(Job job) {
		return new ConsoleProgressMonitor();
	}

}
