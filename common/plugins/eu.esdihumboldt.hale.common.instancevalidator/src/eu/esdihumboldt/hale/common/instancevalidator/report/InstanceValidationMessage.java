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

package eu.esdihumboldt.hale.common.instancevalidator.report;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * Instance validation message.
 *
 * @author Kai Schwierczek
 */
public interface InstanceValidationMessage extends Message {
	/**
	 * Returns the reference to the instance this message is about.
	 * The reference is only valid as long as the data set didn't change.
	 *
	 * @return the reference to the instance this message is about, may be null
	 */
	public InstanceReference getInstanceReference();
}
