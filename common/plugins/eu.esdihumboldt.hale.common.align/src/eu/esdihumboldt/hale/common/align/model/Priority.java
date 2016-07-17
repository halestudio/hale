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

import java.util.Comparator;

/**
 * Represents a available priority levels for {@link Cell}s.
 * 
 * @author Andrea Antonello
 */
public enum Priority {
	/**
	 * Highest priority.
	 */
	HIGHEST("highest", Integer.MAX_VALUE), //
	/**
	 * Higher priority.
	 */
	HIGHER("higher", 2), //
	/**
	 * High priority.
	 */
	HIGH("high", 1), //
	/**
	 * Normal priority.
	 */
	NORMAL("normal", 0), //
	/**
	 * Low priority.
	 */
	LOW("low", -1), //
	/**
	 * Lower priority.
	 */
	LOWER("lower", -2), //
	/**
	 * Lowest priority.
	 */
	LOWEST("lowest", Integer.MIN_VALUE);

	private final String _value;
	private final int _numericPriority;

	private Priority(String value, int numericPriority) {
		_value = value;
		_numericPriority = numericPriority;
	}

	/**
	 * Getter for the label representation of the {@link Priority}.
	 * 
	 * @return the label string.
	 */
	public String value() {
		return _value;
	}

	/**
	 * Priority expressed as number for simple comparison.
	 * 
	 * <p>
	 * Bigger number means higher priority.
	 * </p>
	 * 
	 * @return the priority expressed in number.
	 */
	public int getPriorityNumber() {
		return _numericPriority;
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return value();
	}

	/**
	 * Compare two priorities.
	 * 
	 * <p>
	 * Return the values ready to be used in a {@link Comparator} interface.
	 * </p>
	 * 
	 * @param priority1 the first priority.
	 * @param priority2 the second priority.
	 * @return a negative integer, zero, or a positive integer as p1 is less
	 *         than, equal to, or greater than p2.
	 */
	public static int compare(Priority priority1, Priority priority2) {
		if (priority1.getPriorityNumber() > priority2.getPriorityNumber()) {
			return 1;
		}
		else if (priority1.getPriorityNumber() < priority2.getPriorityNumber()) {
			return -1;
		}
		else {
			return 0;
		}
	}

	/**
	 * Get {@link Priority} from string value.
	 * 
	 * @param v the string value.
	 * @return the {@link Priority}.
	 */
	public static Priority fromValue(String v) {
		for (Priority c : Priority.values()) {
			if (c.value().equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
