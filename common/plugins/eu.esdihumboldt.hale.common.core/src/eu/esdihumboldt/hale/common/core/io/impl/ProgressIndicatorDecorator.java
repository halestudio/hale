/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
