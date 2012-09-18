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

package eu.esdihumboldt.hale.common.core.io.report.impl;

import java.util.Properties;

import eu.esdihumboldt.hale.common.core.report.impl.AbstractMessageDefinition;

/**
 * Object definition for {@link IOMessageImpl}
 * 
 * @author Simon Templer
 */
public class IOMessageImplDefinition extends AbstractMessageDefinition<IOMessageImpl> {

	/**
	 * Key for the line string
	 */
	public static final String KEY_IO_LINE = "line";

	/**
	 * Key for the column string
	 */
	public static final String KEY_IO_COLUMN = "column";

	/**
	 * Default constructor
	 */
	public IOMessageImplDefinition() {
		super(IOMessageImpl.class, "io");
	}

	/**
	 * @see AbstractMessageDefinition#createMessage(Properties)
	 */
	@Override
	protected IOMessageImpl createMessage(Properties props) {
		return new IOMessageImpl(props.getProperty(KEY_MESSAGE), null,
				props.getProperty(KEY_STACK_TRACE),
				Integer.parseInt(props.getProperty(KEY_IO_LINE)), Integer.parseInt(props
						.getProperty(KEY_IO_COLUMN)));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.impl.AbstractMessageDefinition#asProperties(eu.esdihumboldt.hale.common.core.report.Message)
	 */
	@Override
	protected Properties asProperties(IOMessageImpl message) {
		Properties props = super.asProperties(message);

		props.setProperty(KEY_IO_LINE, "" + message.getLineNumber());
		props.setProperty(KEY_IO_COLUMN, "" + message.getColumn());

		return props;
	}
}
