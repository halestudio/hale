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

package eu.esdihumboldt.hale.io.jdbc.constraints;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;

/**
 * Specifies the default value for a property.
 *
 * @author Kai Schwierczek
 */
@Constraint(mutable = false)
public class DefaultValue implements PropertyConstraint {
	private static final Object NO_DEFAULT = new Object();

	private final Object value;

	/**
	 * Constructor with no default value set.
	 */
	public DefaultValue() {
		value = NO_DEFAULT;
	}

	/**
	 * Constructor with the given default value.
	 *
	 * @param value the default value
	 */
	public DefaultValue(Object value) {
		this.value = value;
	}

	/**
	 * Returns the set default value. If no default value was set,
	 * <code>null</code> will be returned. Use {@link #isSet()} to
	 * differentiate between <code>null</code> as default value.
	 *
	 * @return the set default value, or <code>null</code> if none was set
	 */
	public Object getValue() {
		return value == NO_DEFAULT ? null : value;
	}

	/**
	 * Returns whether a default value is set or not.
	 *
	 * @return whether a default value is set or not
	 */
	public boolean isSet() {
		return value != NO_DEFAULT;
	}
}
