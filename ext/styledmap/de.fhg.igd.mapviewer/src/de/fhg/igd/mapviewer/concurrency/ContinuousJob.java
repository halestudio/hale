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

import java.util.List;

/**
 * Job that allows handling partial results using a {@link Callback}
 * 
 * @param <T> the partial result type
 * @author Simon Templer
 */
public abstract class ContinuousJob<T> extends AbstractContinuousJob<T> {

	private final Callback<List<T>> callback;

	/**
	 * Constructor
	 * 
	 * @param name the job name
	 * @param callback the call-back for partial results
	 */
	public ContinuousJob(String name, Callback<List<T>> callback) {
		super(name);

		this.callback = callback;
	}

	/**
	 * @see AbstractContinuousJob#error(Throwable)
	 */
	@Override
	protected void error(Throwable e) {
		callback.failed(e);
	}

	/**
	 * @see AbstractContinuousJob#process(List)
	 */
	@Override
	protected void process(List<T> currentResults) {
		callback.done(currentResults);
	}

}
