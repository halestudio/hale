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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.impl;

import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;

/**
 * Progress indicator decorator
 * 
 * @author Simon Templer
 */
public abstract class ProgressIndicatorDecorator implements ProgressIndicator {

	private final ProgressIndicator decoratee;

	/**
	 * Create a progress indicator decorator
	 * 
	 * @param decoratee the decoratee
	 */
	public ProgressIndicatorDecorator(ProgressIndicator decoratee) {
		super();
		this.decoratee = decoratee;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ProgressIndicator#begin(java.lang.String,
	 *      int)
	 */
	@Override
	public void begin(String taskName, int totalWork) {
		decoratee.begin(taskName, totalWork);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ProgressIndicator#setCurrentTask(java.lang.String)
	 */
	@Override
	public void setCurrentTask(String taskName) {
		decoratee.setCurrentTask(taskName);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ProgressIndicator#advance(int)
	 */
	@Override
	public void advance(int workUnits) {
		decoratee.advance(workUnits);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ProgressIndicator#isCanceled()
	 */
	@Override
	public boolean isCanceled() {
		return decoratee.isCanceled();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ProgressIndicator#end()
	 */
	@Override
	public void end() {
		decoratee.end();
	}

}
