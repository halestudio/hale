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
 * Job call-back
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 * 
 * @param <R> the job result type
 */
public interface Callback<R> {

	/**
	 * Called then job is done
	 * 
	 * @param result the job result
	 */
	public void done(R result);

	/**
	 * Called when the job failed
	 * 
	 * @param e the error
	 */
	public void failed(Throwable e);

}
