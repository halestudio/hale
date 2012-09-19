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

package eu.esdihumboldt.hale.common.core.report.impl;

import java.util.Properties;

import eu.esdihumboldt.hale.common.core.report.Message;

/**
 * Object definition for {@link MessageImpl}
 * 
 * @author Simon Templer
 */
public class MessageImplDefinition extends AbstractMessageDefinition<Message> {

	/**
	 * Default constructor
	 */
	public MessageImplDefinition() {
		super(Message.class, "default");
	}

	/**
	 * @see AbstractMessageDefinition#createMessage(Properties)
	 */
	@Override
	protected MessageImpl createMessage(Properties props) {
		return new MessageImpl(props.getProperty(KEY_MESSAGE), null,
				props.getProperty(KEY_STACK_TRACE));
	}
}
