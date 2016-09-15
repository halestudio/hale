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

/**
 * Progress interface.
 * 
 * @author Simon Templer
 */
public interface Progress {

	/**
	 * Begin the job
	 * 
	 * @param total the total number of work units
	 */
	public void begin(int total);

	/**
	 * Begin the job, the number of work units is indeterminate
	 */
	public void begin();

	/**
	 * Inform about work progression
	 * 
	 * @param work the number of work units that have just been done
	 */
	public void progress(int work);

	/**
	 * Set the name of the current task
	 * 
	 * @param task the task name
	 */
	public void setTask(String task);

	/**
	 * Determines if the job was canceled, long running jobs should check this
	 * method and cancel processing.
	 * 
	 * @return if the job was canceled
	 */
	public boolean isCanceled();

}
