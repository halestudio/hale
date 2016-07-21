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
 * Job interface
 * 
 * @author Simon Templer
 * @param <R> the result type
 */
public interface IJob<R> {

	/**
	 * Get the call-back
	 * 
	 * @return the call-back
	 */
	public abstract Callback<R> getCallback();

	/**
	 * Get the job name
	 * 
	 * @return the job name
	 */
	public abstract String getName();

	/**
	 * @return if the job is modal
	 */
	public abstract boolean isModal();

	/**
	 * @return if this is a hidden job
	 */
	public abstract boolean isHidden();

	/**
	 * @return if this is a background job
	 */
	public abstract boolean isBackground();

	/**
	 * @return if the job execution shall be represented by a log transaction
	 */
	public abstract boolean isLogTransaction();

	/**
	 * @return if the job execution shall be exclusive for jobs with the same
	 *         name
	 */
	public abstract boolean isExclusive();

	/**
	 * Gets the job done
	 * 
	 * @param progress the job progress monitor
	 * 
	 * @return the result (yes, I know you knew)
	 * @throws Exception if an error occurs
	 */
	public abstract R work(Progress progress) throws Exception;

}
