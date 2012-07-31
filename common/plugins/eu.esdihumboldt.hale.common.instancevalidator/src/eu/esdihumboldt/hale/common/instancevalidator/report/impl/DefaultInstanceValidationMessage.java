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

package eu.esdihumboldt.hale.common.instancevalidator.report.impl;

import eu.esdihumboldt.hale.common.core.report.impl.MessageImpl;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationMessage;

/**
 * Default implementation of {@link InstanceValidationMessage}.
 *
 * @author Kai Schwierczek
 */
public class DefaultInstanceValidationMessage extends MessageImpl implements InstanceValidationMessage {
	private final InstanceReference instanceReference;

	/**
	 * Create a new instance validation message.
	 *
	 * @param instanceReference the instance reference this message is associated to, may be null
	 * @param message the message string
	 */
	public DefaultInstanceValidationMessage(InstanceReference instanceReference, String message) {
		super(message, null);
		this.instanceReference = instanceReference;
	}

	/**
	 * @see InstanceValidationMessage#getInstanceReference()
	 */
	@Override
	public InstanceReference getInstanceReference() {
		return instanceReference;
	}
}
