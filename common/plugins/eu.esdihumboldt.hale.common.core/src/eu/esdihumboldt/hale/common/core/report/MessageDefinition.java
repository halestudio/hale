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

package eu.esdihumboldt.hale.common.core.report;

import eu.esdihumboldt.util.definition.ObjectDefinition;

/**
 * Message definition interface. String representations of {@link Message} are
 * explicitly allowed to span multiple lines. Identifiers must begin with the
 * {@link #ID_PREFIX}.
 * 
 * @param <T> the message type
 * @author Simon Templer
 * @since 2.5
 */
public interface MessageDefinition<T extends Message> extends ObjectDefinition<T> {

	/**
	 * The common ID prefix for all message definitions
	 */
	public static final String ID_PREFIX = "!MSG_";

	// concrete typed interface

}
