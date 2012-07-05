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

import java.util.Properties;

import eu.esdihumboldt.hale.common.core.report.impl.AbstractMessageDefinition;
import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationMessage;

/**
 * Definition for {@link InstanceValidationMessage}s.
 *
 * @author Kai Schwierczek
 */
public class InstanceValidationMessageDefinition extends AbstractMessageDefinition<InstanceValidationMessage>  {

	/**
	 * Constructor.
	 */
	public InstanceValidationMessageDefinition() {
		super(InstanceValidationMessage.class, "instance_validation_message");
	}

	/**
	 * @see AbstractMessageDefinition#createMessage(Properties)
	 */
	@Override
	protected InstanceValidationMessage createMessage(Properties props) {
		// instance reference isn't valid anymore either way... simply use null
		return new DefaultInstanceValidationMessage(null, props.getProperty(KEY_MESSAGE),
				null, props.getProperty(KEY_STACK_TRACE));
	}
}
