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
 * Dummy progress indicator ignoring everything you tell him.
 * @author Simon Templer
 */
public class NullProgressIndicator implements ProgressIndicator {

	@Override
	public void begin(String taskName, int totalWork) {
		// ignore
	}

	@Override
	public void setCurrentTask(String taskName) {
		// ignore
	}

	@Override
	public void advance(int workUnits) {
		// ignore
	}

	@Override
	public boolean isCanceled() {
		return false;
	}

	@Override
	public void end() {
		// ignore
	}

}
