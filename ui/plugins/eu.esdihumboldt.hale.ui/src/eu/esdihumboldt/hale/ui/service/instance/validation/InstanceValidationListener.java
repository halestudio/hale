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

package eu.esdihumboldt.hale.ui.service.instance.validation;

import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationReport;

/**
 * Listener for instance validation.
 *
 * @author Kai Schwierczek
 */
public interface InstanceValidationListener {
	/**
	 * Called, when instance validation ran.
	 *
	 * @param report the resulting report
	 */
	public void instancesValidated(InstanceValidationReport report);

	/**
	 * Called, when the automatic instance validation gets enabled/disabled.
	 */
	public void validationEnabledChange();
}
