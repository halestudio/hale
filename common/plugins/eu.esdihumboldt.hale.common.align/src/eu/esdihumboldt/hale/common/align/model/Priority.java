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

package eu.esdihumboldt.hale.common.align.model;

/**
 * Represents a available priority levels for {@link Cell}s.
 * 
 * @author Andrea Antonello
 */
public enum Priority {
	/**
	 * High priority.
	 */
	HIGH("high"), //
	/**
	 * Normal priority.
	 */
	NORMAL("normal"), //
	/**
	 * Low priority
	 */
	LOW("low");

	private final String _label;

	private Priority(String label) {
		_label = label;
	}

	/**
	 * Getter for the label representation of the {@link Priority}.
	 * 
	 * @return the label string.
	 */
	public String getLabel() {
		return _label;
	}
}
