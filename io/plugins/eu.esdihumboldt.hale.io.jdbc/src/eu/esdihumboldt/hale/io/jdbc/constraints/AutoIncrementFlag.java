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
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Specifies whether a property is an auto increment property.
 * 
 * XXX It is questionable whether it is possible to really use this, as it is
 * not supported by all database types
 * 
 * @author Kai Schwierczek
 */
@Constraint(mutable = false)
public class AutoIncrementFlag extends AbstractFlagConstraint implements PropertyConstraint {

	/**
	 * Enabled auto increment flag instance.
	 */
	public static final AutoIncrementFlag ENABLED = new AutoIncrementFlag(true);

	/**
	 * Disabled auto increment flag instance.
	 */
	public static final AutoIncrementFlag DISABLED = new AutoIncrementFlag(false);

	/**
	 * Get the auto increment flag.
	 * 
	 * @param isAutoIncrement if the flag shall be enabled
	 * @return the flag
	 */
	public static AutoIncrementFlag get(boolean isAutoIncrement) {
		return isAutoIncrement ? ENABLED : DISABLED;
	}

	/**
	 * Creates a default auto increment flag, which is <code>false</code>.
	 */
	public AutoIncrementFlag() {
		super(false);
	}

	/**
	 * Create a auto increment flag with the specified enabled state.
	 * 
	 * @param enabled if the flag shall be enabled
	 */
	private AutoIncrementFlag(boolean enabled) {
		super(enabled);
	}
}
