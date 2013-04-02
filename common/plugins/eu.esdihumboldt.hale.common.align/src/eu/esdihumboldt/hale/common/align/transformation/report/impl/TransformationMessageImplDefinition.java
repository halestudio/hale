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

package eu.esdihumboldt.hale.common.align.transformation.report.impl;

import java.util.Properties;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.core.report.impl.AbstractMessageDefinition;

/**
 * Object definition for {@link TransformationMessageImpl}
 * 
 * @author Simon Templer
 */
public class TransformationMessageImplDefinition extends
		AbstractMessageDefinition<TransformationMessage> {

	private static final ALogger _log = ALoggerFactory.getLogger(TransformationMessage.class);

	/**
	 * Key for the cell identifier.
	 */
	public static final String KEY_CELL_ID = "cellId";

	/**
	 * Default constructor
	 */
	public TransformationMessageImplDefinition() {
		super(TransformationMessage.class, "transformation");
	}

	/**
	 * @see AbstractMessageDefinition#createMessage(Properties)
	 */
	@Override
	protected TransformationMessageImpl createMessage(Properties props) {
		try {
			TransformationMessageImpl message = new TransformationMessageImpl(
					props.getProperty(KEY_CELL_ID), props.getProperty(KEY_MESSAGE), null,
					props.getProperty(KEY_STACK_TRACE));

			return message;
		} catch (Exception e) {
			_log.error("Could not recover saved cell.", e.getCause());
		}

		// if the message could not be recovered
		return null;
	}

	@Override
	protected Properties asProperties(TransformationMessage message) {
		Properties props = super.asProperties(message);

		if (message.getCellId() != null) {
			props.setProperty(KEY_CELL_ID, message.getCellId());
		}

		return props;

	}
}
