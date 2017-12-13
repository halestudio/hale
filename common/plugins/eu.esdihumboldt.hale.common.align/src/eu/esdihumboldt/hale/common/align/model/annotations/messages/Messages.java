/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.align.model.annotations.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;

/**
 * List of {@link Message}s.
 * 
 * @author Simon Templer
 */
public class Messages {

	private final List<Message> messages = new ArrayList<>();

	/**
	 * Add a message.
	 * 
	 * @param msg the message to add
	 * @return this for chaining
	 */
	public Messages addMessage(Message msg) {
		messages.add(msg);
		return this;
	}

	/**
	 * @return the messages
	 */
	public List<Message> getMessages() {
		return messages;
	}

	/**
	 * Create a messages object from a value.
	 * 
	 * @param value the value
	 * @return the messages object
	 */
	public static Messages fromValue(Value value) {
		Messages result = new Messages();

		ValueList vals = value.as(ValueList.class);
		if (vals != null) {
			for (Value val : vals) {
				Optional<Message> msg = Message.fromValue(val);
				msg.ifPresent(m -> result.addMessage(m));
			}
		}
		return result;
	}

	/**
	 * Convert to a {@link Value}.
	 * 
	 * @return the value representation
	 */
	public Value toValue() {
		ValueList vals = new ValueList();

		for (Message msg : messages) {
			vals.add(msg.toValue());
		}

		return vals.toValue();
	}

}
